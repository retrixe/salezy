import type { RouteHandlerMethod } from 'fastify'
import jwt from 'jsonwebtoken'
import { verify } from 'argon2'
import { jwtSecret } from '../config.js'
import sql from '../db.js'

const loginHandler: RouteHandlerMethod = async (request, reply) => {
  const { username, password } = (request.body ?? {}) as {
    username?: string
    password?: string
  }
  if (!username || !password) {
    await reply.code(400)
    return { error: 'Invalid body!' }
  }
  const [row]: [{ password: string }] =
    await sql`SELECT password FROM users WHERE username = ${username} LIMIT 1;`
  if (!row || !(await verify(row.password, password))) {
    await reply.code(401)
    return { error: 'Invalid username or password!' }
  }
  // Sign JWT and return it.
  await new Promise((resolve, reject) =>
    jwt.sign({ username }, jwtSecret, { expiresIn: '30d' }, (err, encoded) => {
      if (err) reject(err)
      else resolve(encoded)
    }),
  )
}

export default loginHandler
