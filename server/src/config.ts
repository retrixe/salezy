import type postgres from 'postgres'

import parsedConfig from '../config.json' with { type: 'json' }

interface Config {
  port: number
  jwtSecret: string
  postgresConf: postgres.Options<any>
}

const defaultConfig: Omit<Config, 'jwtSecret'> = {
  port: 3000,
  postgresConf: { database: 'salezy' },
}

const config: Config = Object.assign(defaultConfig, parsedConfig)

export const { port, jwtSecret, postgresConf } = config

export default config
