import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { verifyRequest } from '../utils.js'
import { server } from '../main.js'
import {
  type InventoryItem,
  validateInventoryItem,
  validateEphemeralInventoryItem,
} from '../database/entities/inventoryItem.js'
import { hash } from 'crypto'
import type { PostgresError } from 'postgres'

export const getInventoryItemsHandler: RouteHandlerMethod = async (request, reply) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const inventoryItems = await sql<InventoryItem[]>`SELECT * FROM inventory_items;`
  const invalidInventoryItems = inventoryItems.filter(c => !validateInventoryItem(c))
  if (invalidInventoryItems.length) {
    reply.statusCode = 500
    server.log.error('Invalid inventory items stored!', invalidInventoryItems)
    return { error: 'Internal Server Error: Invalid inventory items stored!' }
  }
  return inventoryItems
}

export const getInventoryItemQueryByIDHandler: RouteHandlerMethod = async (request, reply) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const { query } = request.params as Record<string, string>
  if (typeof query !== 'string') {
    reply.statusCode = 400
    return { error: 'Invalid query!' }
  }
  const [inventoryItem]: [InventoryItem | undefined] = await sql`SELECT * FROM inventory_items ${
    /^\d+$/.test(query) ? sql`WHERE upc = ${query} OR sku = ${query}` : sql`WHERE sku = ${query}`
  } LIMIT 1;`
  if (!inventoryItem) {
    reply.statusCode = 404
    return { error: 'Inventory item not found!' }
  }
  const validInventoryItem = validateInventoryItem(inventoryItem)
  if (!validInventoryItem) {
    reply.statusCode = 500
    server.log.error('Invalid inventory item stored!', validInventoryItem)
    return { error: 'Internal Server Error: Invalid inventory item stored!' }
  }
  return inventoryItem
}

export const postInventoryItemHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  if (!validateEphemeralInventoryItem(request.body)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const { body } = request
  try {
    return await sql.begin(async sql => {
      const { image, ...rest } = body
      const item = rest as InventoryItem
      if (image) {
        const data = Buffer.from(image, 'base64')
        const sha256sum = hash('sha256', data, 'hex')
        await sql`INSERT INTO assets (hash, data) VALUES (${sha256sum}, ${data}) ON CONFLICT DO NOTHING;`
        item.imageId = sha256sum
      }
      await sql`INSERT INTO inventory_items ${sql(item)};`

      await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
        ${user.username}, 0, 'inventory_item', ${item.upc}, NULL, ${item as any}::jsonb
      );`
      return item
    })
  } catch (e) {
    if ((e as PostgresError).code === '23505' /* UNIQUE VIOLATION */) {
      reply.statusCode = 409
      return { error: 'Inventory item with this UPC already exists!' }
    } else throw e as Error
  }
}

export const patchInventoryItemHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  const { upc } = request.params as Record<string, string>
  if (!validateEphemeralInventoryItem(request.body) || !/^\d+$/.test(upc)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const { body } = request
  const [newItem, oldImageId] = await sql.begin(async sql => {
    const [oldItem]: InventoryItem[] = await sql`SELECT * FROM inventory_items WHERE upc = ${upc};`
    if (!oldItem) {
      reply.statusCode = 404
      return [{ error: 'Inventory item not found!' }]
    }
    const { image, upc: _, ...newItemData } = body // Prohibit UPC from being modified
    if (image) {
      const data = Buffer.from(image, 'base64')
      const sha256sum = hash('sha256', data, 'hex')
      await sql`INSERT INTO assets (hash, data) VALUES (${sha256sum}, ${data}) ON CONFLICT DO NOTHING;`
      newItemData.imageId = sha256sum
    }
    await sql`UPDATE inventory_items SET ${sql(newItemData)} WHERE upc = ${upc};`
    const oldImageId = newItemData.imageId !== oldItem.imageId && oldItem.imageId

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 2, 'inventory_item', ${upc}, ${oldItem as any}, ${newItemData as any}::jsonb
    );`
    return [{ upc, ...newItemData }, oldImageId]
  })

  if (oldImageId && typeof oldImageId === 'string') {
    try {
      await sql`DELETE FROM assets WHERE hash = ${oldImageId};`
    } catch (e) {
      /* Do nothing */
    }
  }

  return newItem
}
