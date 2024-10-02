import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { ajv, verifyRequest } from '../utils.js'
import { server } from '../main.js'
import { validateSettings, type SettingRow, type Settings } from '../database/entities/settings.js'

export const getSettingsHandler: RouteHandlerMethod = async (request, reply) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const settings = await sql`SELECT key, value FROM settings;`
  const settingsObj = Object.fromEntries(settings.map(s => [s.key, s.value]))
  if (!validateSettings(settingsObj)) {
    reply.statusCode = 500
    server.log.error('Invalid settings!', settings)
    return { error: 'Internal Server Error: Invalid settings!' }
  }
  return settingsObj
}

type PostSettingsBody = Partial<Settings>
const { ...schemaPostSettingsBody } = validateSettings.schema as Record<string, any>
delete schemaPostSettingsBody.required
export const validatePostSettingsBody = ajv.compile<PostSettingsBody>(schemaPostSettingsBody)

export const postSettingsHandler: RouteHandlerMethod = async (request, reply) => {
  const user = verifyRequest(request)
  if (!user) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }

  if (!validatePostSettingsBody(request.body)) {
    reply.statusCode = 400
    return { error: 'Invalid request body!' }
  }

  const body = request.body
  await sql.begin(async sql => {
    const settings = await sql<SettingRow[]>`SELECT key, value FROM settings;`
    const settingsObj = Object.fromEntries(settings.map(s => [s.key, JSON.parse(s.value)]))

    for (const [key, value] of Object.entries(body)) {
      await sql`UPDATE settings SET value = ${value}::jsonb WHERE key = ${key};`
    }

    await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
      ${user.username}, 2, 'settings', -1,
      ${JSON.stringify(settingsObj)}::jsonb,
      ${JSON.stringify({ ...settingsObj, ...body })}::jsonb
    );`
  })

  return { success: true }
}
