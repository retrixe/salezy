import fastify from 'fastify'
import config from './config.js'
import postLoginHandler from './endpoints/login.js'
import { getSettingsHandler, postSettingsHandler } from './endpoints/settings.js'
import {
  getCustomersHandler,
  patchCustomerHandler,
  postCustomerHandler,
} from './endpoints/customers.js'

export const server = fastify({ logger: true })

server.get('/', () => ({ hello: 'world' }))

server.post('/login', postLoginHandler)

server.get('/settings', getSettingsHandler)
server.post('/settings', postSettingsHandler)

server.get('/customers', getCustomersHandler)
server.post('/customer', postCustomerHandler)
server.patch('/customer/:id', patchCustomerHandler)

try {
  await server.listen({ port: config.port })
} catch (err) {
  server.log.error(err)
  process.exitCode = 1
}
