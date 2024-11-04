import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { ajv, verifyRequest } from '../utils.js'
import { type User, validateUser } from '../database/entities/user.js'
import { hash } from 'argon2'

export const getAccountsHandler: RouteHandlerMethod = async (request, reply) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  return (await sql<Array<{ username: string }>>`SELECT username FROM users;`).map(u => u.username)
}

export const postAccountHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const { body } = request
  if (!validateUser(body)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  body.password = await hash(body.password) // Hash password if set
  return await sql.begin(async sql => {
    if ((await sql<User[]>`SELECT * FROM users WHERE username = ${body.username};`).length) {
      reply.statusCode = 409
      return { error: 'User already exists!' }
    }

    await sql`INSERT INTO users ${sql(body)};`
    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 0, 'user', ${body.username}, NULL, ${body as any}::jsonb
    );`
  })
}

const { ...partialUserSchema } = validateUser.schema as Record<string, any>
delete partialUserSchema.required
export const validatePartialUser = ajv.compile<Partial<User>>(partialUserSchema)

export const patchAccountHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const { body } = request
  const { username } = request.params as Record<string, string>
  if (!validatePartialUser(body) || typeof username !== 'string') {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  } else if (username === user.username && body.username) {
    reply.statusCode = 409
    return { error: 'Cannot change own username!' }
  }

  if (body.password) body.password = await hash(body.password) // Hash password if set
  return await sql.begin(async sql => {
    const [oldUser] = await sql<User[]>`SELECT * FROM users WHERE username = ${username};`
    if (!oldUser) {
      reply.statusCode = 404
      return { error: 'User not found!' }
    }
    await sql`UPDATE users SET ${sql(body)} WHERE username = ${username};`
    const newUser = { ...oldUser, ...body }
    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 2, 'user', ${username}, ${oldUser as any}, ${newUser as any}::jsonb
    );`
  })
}

export const deleteAccountHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const { username } = request.params as Record<string, string>
  if (typeof username !== 'string') {
    reply.statusCode = 400
    return { error: 'Invalid username provided!' }
  } else if (username === user.username) {
    reply.statusCode = 409
    return { error: 'Cannot delete own account!' }
  }

  return await sql.begin(async sql => {
    const [oldUser] = await sql<User[]>`SELECT * FROM users WHERE username = ${username};`
    if (!oldUser) {
      reply.statusCode = 404
      return { error: 'User not found!' }
    }
    await sql`DELETE FROM users WHERE username = ${username};`
    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 1, 'user', ${username}, ${oldUser as any}, NULL
    );`
  })
}
