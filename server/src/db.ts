import { hash } from 'argon2'
import postgres from 'postgres'
import { postgresConf } from './config.js'

const sql = postgres(postgresConf)

// Users table
await sql`CREATE TABLE IF NOT EXISTS users (
  id SERIAL PRIMARY KEY,
  username VARCHAR(320) UNIQUE NOT NULL,
  password VARCHAR(128) NOT NULL
);`
await sql`INSERT INTO users (username, password) VALUES ('admin', ${await hash('admin')})
          ON CONFLICT DO NOTHING;`

export default sql
