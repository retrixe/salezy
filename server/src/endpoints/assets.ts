import type { RouteHandlerMethod } from 'fastify'
import sql from '../database/sql.js'

export const getAssetHandler: RouteHandlerMethod = async (request, reply) => {
  // No request verification is needed for this endpoint
  const { id } = request.params as { id: string }
  if (!id) {
    reply.statusCode = 400
    return { error: 'Invalid request parameters!' }
  }
  return (await sql<[{ data: Buffer }]>`SELECT data FROM assets WHERE hash = ${id} LIMIT 1;`)[0]
    .data
}
