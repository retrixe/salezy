import fastify from 'fastify'
import config from './config.js'
import postLoginHandler from './endpoints/login.js'
import { getSettingsHandler, postSettingsHandler } from './endpoints/settings.js'
import {
  getCustomersHandler,
  patchCustomerHandler,
  postCustomerHandler,
} from './endpoints/customers.js'
import {
  getInventoryItemsHandler,
  getInventoryItemQueryByIDHandler,
  patchInventoryItemHandler,
  postInventoryItemHandler,
} from './endpoints/inventoryItems.js'
import { getAssetHandler } from './endpoints/assets.js'
import {
  deleteAccountHandler,
  getAccountsHandler,
  patchAccountHandler,
  postAccountHandler,
} from './endpoints/accounts.js'

export const server = fastify({ logger: true, bodyLimit: 25 * 1024 * 1024 })

server.get('/', () => ({ hello: 'world' }))

server.post('/login', postLoginHandler)

server.get('/accounts', getAccountsHandler)
server.post('/account', postAccountHandler)
server.patch('/account/:username', patchAccountHandler)
server.delete('/account/:username', deleteAccountHandler)

server.get('/settings', getSettingsHandler)
server.post('/settings', postSettingsHandler)

server.get('/customers', getCustomersHandler)
server.post('/customer', postCustomerHandler)
server.patch('/customer/:id', patchCustomerHandler)

server.get('/inventoryItems', getInventoryItemsHandler)
server.get('/inventoryItem/queryByID/:query', getInventoryItemQueryByIDHandler)
server.post('/inventoryItem', postInventoryItemHandler)
server.patch('/inventoryItem/:upc', patchInventoryItemHandler)

server.get('/asset/:id', getAssetHandler)

try {
  await server.listen({ port: config.port })
} catch (err) {
  server.log.error(err)
  process.exitCode = 1
}
