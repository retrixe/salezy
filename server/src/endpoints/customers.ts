import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { verifyRequest } from '../utils.js'
import { server } from '../main.js'
import {
  type Customer,
  validateCustomer,
  validateEphemeralCustomer,
} from '../database/entities/customer.js'
import { AuditLogAction, insertAuditLog } from '../database/entities/auditLog.js'

export const getCustomersHandler: RouteHandlerMethod = async (request, reply) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const customers = await sql<Customer[]>`SELECT * FROM customers;`
  const invalidCustomers = customers.filter(c => !validateCustomer(c))
  if (invalidCustomers.length) {
    reply.statusCode = 500
    server.log.error('Invalid customers stored!', invalidCustomers)
    return { error: 'Internal Server Error: Invalid customers stored!' }
  }
  return customers
}

export const postCustomerHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  if (!validateEphemeralCustomer(request.body)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const { body } = request
  return await sql.begin(async sql => {
    const [{ id }] = await sql<[{ id: number }]>`INSERT INTO customers ${sql(body)} RETURNING id;`
    const customer = { ...body, id }
    await insertAuditLog(user.username, AuditLogAction.CREATE, 'customer', id, null, customer)
    return customer
  })
}

export const patchCustomerHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  const { id } = request.params as Record<string, string>
  if (!validateEphemeralCustomer(request.body) || !/^\d+$/.test(id)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const { body } = request
  return await sql.begin(async sql => {
    const [oldCust] = await sql<Customer[]>`SELECT * FROM customers WHERE id = ${id};`
    if (!oldCust) {
      reply.statusCode = 404
      return { error: 'Customer not found!' }
    }
    const [newCust]: [Customer] =
      await sql`UPDATE customers SET ${sql(body)} WHERE id = ${id} RETURNING *;`
    await insertAuditLog(user.username, AuditLogAction.UPDATE, 'customer', id, oldCust, newCust)
    return newCust
  })
}
