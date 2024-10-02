import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { ajv, verifyRequest } from '../utils.js'
import { server } from '../main.js'
import { type Customer, validateCustomer } from '../database/entities/customer.js'

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

type CustomerRequestBody = Omit<Customer, 'id'>
const { ...customerRequestBodySchema } = validateCustomer.schema as Record<string, any>
delete customerRequestBodySchema.required
delete customerRequestBodySchema.properties.id
export const validateCustomerRequestBody =
  ajv.compile<CustomerRequestBody>(customerRequestBodySchema)

export const postCustomerHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  if (!validateCustomerRequestBody(request.body)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const body = request.body
  const resId = await sql.begin(async sql => {
    const [{ id }] = await sql<[{ id: number }]>`INSERT INTO customers ${sql(body)} RETURNING id;`

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 0, 'customer', ${id}, NULL, ${body as any}::jsonb
    );`
    return id
  })

  return { ...body, id: resId }
}

export const patchCustomerHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  const { id } = request.params as Record<string, string>
  if (!validateCustomerRequestBody(request.body) || typeof id !== 'string') {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const body = request.body
  const customer = await sql.begin(async sql => {
    const [oldCustomer] = await sql<Customer[]>`SELECT * FROM customers WHERE id = ${id};`
    const [newCustomer]: [Customer] =
      await sql`UPDATE customers SET ${sql(body)} WHERE id = ${id} RETURNING *;`

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 0, 'customer', ${id}, ${oldCustomer as any}, ${newCustomer as any}::jsonb
    );`
    return newCustomer
  })

  return customer
}
