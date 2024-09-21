import fastify from 'fastify'
import config from './config.js'
import loginHandler from './endpoints/login.js'

const server = fastify({ logger: true })

server.get('/', () => ({ hello: 'world' }))

server.post('/login', loginHandler)

try {
  await server.listen({ port: config.port })
} catch (err) {
  server.log.error(err)
  process.exitCode = 1
}
