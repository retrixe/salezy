import { Ajv } from 'ajv'
import type { FastifyRequest } from 'fastify'
import jwt, { type JwtPayload } from 'jsonwebtoken'
import config from './config.js'

export const ajv = new Ajv()

export type JwtTokenPayload = JwtPayload & { username: string }

export const verifyRequest = (req: FastifyRequest): JwtTokenPayload | null => {
  const token = (
    req.headers.authorization ??
    req.headers.cookie
      ?.split('; ')
      ?.find(c => c.startsWith('x-authorization'))
      ?.split('=')[1]
  )?.replace(/^Bearer /, '')

  if (token) {
    try {
      return jwt.verify(token, config.jwtSecret) as JwtTokenPayload
    } catch {
      return null
    }
  } else return null
}
