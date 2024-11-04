import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'
import { ajv, verifyRequest } from '../utils.js'
import { server } from '../main.js'
import { validateSettings, type SettingRow, type Settings } from '../database/entities/settings.js'
import { AuditLogAction, insertAuditLog } from '../database/entities/auditLog.js'

export const getSettingsHandler: RouteHandlerMethod = async (request, reply) => {
  if (!verifyRequest(request)) {
    reply.statusCode = 401
    return { error: 'Invalid authorization token!' }
  }
  const settingRows = await sql<SettingRow[]>`SELECT key, value FROM settings;`
  const settings = Object.fromEntries(settingRows.map(s => [s.key, s.value]))
  if (!validateSettings(settings)) {
    reply.statusCode = 500
    server.log.error('Invalid settings!', settingRows)
    return { error: 'Internal Server Error: Invalid settings!' }
  }
  return settings
}

type PostSettingsBody = Partial<Settings>
const { ...postSettingsBodySchema } = validateSettings.schema as Record<string, any>
delete postSettingsBodySchema.required
export const validatePostSettingsBody = ajv.compile<PostSettingsBody>(postSettingsBodySchema)

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

  const { body } = request
  await sql.begin(async sql => {
    const settings = await sql<SettingRow[]>`SELECT key, value FROM settings;`
    const oldSettings = Object.fromEntries(settings.map(s => [s.key, JSON.parse(s.value)]))
    const newSettings = { ...oldSettings, ...body }

    for (const [key, value] of Object.entries(body)) {
      await sql`UPDATE settings SET value = ${value}::jsonb WHERE key = ${key};`
    }
    await insertAuditLog(
      user.username,
      AuditLogAction.UPDATE,
      'settings',
      -1,
      oldSettings,
      newSettings,
    )
  })

  return { success: true }
}
