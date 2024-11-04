import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { ajv, verifyRequest } from '../utils.js'
import { type User, validateUser } from '../database/entities/user.js'
import { hash } from 'argon2'
import { AuditLogAction, insertAuditLog } from '../database/entities/auditLog.js'

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
    await insertAuditLog(user.username, AuditLogAction.CREATE, 'user', body.username, null, body)
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
    await insertAuditLog(user.username, AuditLogAction.UPDATE, 'user', username, oldUser, newUser)
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
    await insertAuditLog(user.username, AuditLogAction.DELETE, 'user', username, oldUser, null)
  })
}
