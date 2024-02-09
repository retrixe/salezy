import { readFile } from 'node:fs/promises'

interface Config {
  port: number
  jwtSecret: string
}

const config: Config = JSON.parse(
  await readFile(new URL('../config.json', import.meta.url), {
    encoding: 'utf-8',
  }),
)

export const { port = 3000, jwtSecret } = config

export default Object.assign({ port }, config)
