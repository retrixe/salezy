import type { RouteHandlerMethod } from 'fastify'
import sql from '../db.js'
import { verifyRequest } from '../utils.js'
import { Ajv } from 'ajv'
import { server } from '../main.js'

const ajv = new Ajv()

export const getSettingsHandler: RouteHandlerMethod = async (
  request,
  reply,
) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const settings = await sql`SELECT key, value FROM settings;`
  const taxRate = settings.find(s => s.key === 'taxRate')?.value
  if (!taxRate) {
    server.log.error('Tax rate not found in settings!')
    reply.statusCode = 500
    return { error: 'Internal server error!' }
  }
  return {
    taxRate,
  }
}

interface PostSettingsBody {
  taxRate: number
}

const validatePostSettingsBody = ajv.compile<PostSettingsBody>({
  type: 'object',
  properties: {
    taxRate: { type: 'integer' },
  },
  required: ['taxRate'],
  additionalProperties: false,
})

export const postSettingsHandler: RouteHandlerMethod = async (
  request,
  reply,
) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  if (!validatePostSettingsBody(request.body)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const body = request.body
  await sql.begin(async sql => {
    await sql`UPDATE settings SET value = ${JSON.stringify(body.taxRate)}::jsonb WHERE key = 'taxRate';`
  })

  return { success: true }
}
