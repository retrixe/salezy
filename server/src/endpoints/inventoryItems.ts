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

export const getInventoryItemsHandler: RouteHandlerMethod = async (request, reply) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  // FIXME pls fix bigint shenanigans bigints should be represent as numbers
  const inventoryItems = (await sql<InventoryItem[]>`SELECT * FROM inventory_items;`).map(c => ({
    ...c,
    upc: Number(c.upc),
    costPrice: Number(c.costPrice),
    sellingPrice: Number(c.sellingPrice),
  }))
  const invalidInventoryItems = inventoryItems.filter(c => !validateInventoryItem(c))
  if (invalidInventoryItems.length) {
    reply.statusCode = 500
    server.log.error('Invalid inventory items stored!', invalidInventoryItems)
    return { error: 'Internal Server Error: Invalid inventory items stored!' }
  }
  return inventoryItems
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

  const body = request.body
  return await sql.begin(async sql => {
    const { image, ...rest } = body
    const item = rest as InventoryItem
    if (image) {
      const data = Buffer.from(image, 'base64')
      const sha256sum = hash('sha256', data, 'hex')
      await sql`INSERT INTO assets (hash, data) VALUES (${sha256sum}, ${data}) ON CONFLICT DO NOTHING;`
      item.imageId = sha256sum
    }
    // FIXME: bigint shenanigans
    await sql`INSERT INTO inventory_items ${sql(item)};`

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 0, 'inventory_item', ${item.upc}, NULL, ${item as any}::jsonb
    );`
    return item
  })
}

export const patchInventoryItemHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  const { upc: upcStr } = request.params as Record<string, any>
  const upc = +upcStr
  if (!validateEphemeralInventoryItem(request.body) || typeof upc !== 'number' || isNaN(upc)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const body = request.body
  return await sql.begin(async sql => {
    const [oldItem]: InventoryItem[] = await sql`SELECT * FROM inventory_items WHERE upc = ${upc};`
    // FIXME: AAAAA
    oldItem.upc = Number(oldItem.upc)
    oldItem.costPrice = Number(oldItem.costPrice)
    oldItem.sellingPrice = Number(oldItem.sellingPrice)
    if (!oldItem) {
      reply.statusCode = 404
      return { error: 'Inventory item not found!' }
    }
    const { image, ...rest } = body
    const newItem = rest as InventoryItem
    if (image) {
      const data = Buffer.from(image, 'base64')
      const sha256sum = hash('sha256', data, 'hex')
      await sql`INSERT INTO assets (hash, data) VALUES (${sha256sum}, ${data}) ON CONFLICT DO NOTHING;`
      newItem.imageId = sha256sum
    }
    await sql`UPDATE inventory_items SET ${sql(newItem)} WHERE upc = ${upc};`

    if (newItem.imageId !== oldItem.imageId && oldItem.imageId) {
      await sql`DELETE FROM assets WHERE hash = ${oldItem.imageId} AND NOT EXISTS (
        SELECT 1 FROM inventory_items WHERE image_id = ${oldItem.imageId}
      );`
    }

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 2, 'inventory_item', ${upc}, ${oldItem as any}, ${newItem as any}::jsonb
    );`
    return newItem
  })
}
