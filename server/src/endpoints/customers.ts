import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { verifyRequest } from '../utils.js'
import { server } from '../main.js'
import {
  type Customer,
  validateCustomer,
  validateEphemeralCustomer,
} from '../database/entities/customer.js'

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

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 0, 'customer', ${id}, NULL, ${{ ...body, id } as any}::jsonb
    );`
    return { ...body, id }
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
    const [oldCustomer] = await sql<Customer[]>`SELECT * FROM customers WHERE id = ${id};`
    if (!oldCustomer) {
      reply.statusCode = 404
      return { error: 'Customer not found!' }
    }
    const [newCustomer]: [Customer] =
      await sql`UPDATE customers SET ${sql(body)} WHERE id = ${id} RETURNING *;`

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 2, 'customer', ${id}, ${oldCustomer as any}, ${newCustomer as any}::jsonb
    );`
    return newCustomer
  })
}
