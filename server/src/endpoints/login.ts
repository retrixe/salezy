import type { RouteHandlerMethod } from 'fastify'
import jwt from 'jsonwebtoken'
import { verify } from 'argon2'
import { jwtSecret } from '../config.js'
import sql from '../database/sql.js'
import { validateUser } from '../database/entities/user.js'

const postLoginHandler: RouteHandlerMethod = async (request, reply) => {
  const { body } = request
  if (!validateUser(body)) {
    reply.statusCode = 400
    return { error: 'Missing username and/or password!' }
  }
  const { username, password } = body
  const [row]: [{ password: string }?] =
    await sql`SELECT password FROM users WHERE username = ${username} LIMIT 1;`
  if (!row || !(await verify(row.password, password))) {
    reply.statusCode = 401
    return { error: 'Invalid username or password!' }
  }
  // Sign JWT and return it.
  const token = await new Promise<string | undefined>((resolve, reject) =>
    jwt.sign({ username }, jwtSecret, { expiresIn: '30d' }, (err, encoded) => {
      if (err) reject(err)
      else resolve(encoded)
    }),
  )
  reply.header(
    'set-cookie',
    `x-authorization=${token}; HttpOnly; Secure; SameSite=Strict;`,
  ) as unknown
  return { token }
}

export default postLoginHandler
