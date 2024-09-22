import { hash } from 'argon2'
import postgres from 'postgres'
import { postgresConf } from './config.js'

const sql = postgres(postgresConf)

// Users table
await sql`CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(320) PRIMARY KEY NOT NULL,
  password VARCHAR(128) NOT NULL
);`
await sql`INSERT INTO users (username, password) VALUES ('admin', ${await hash('admin')})
          ON CONFLICT DO NOTHING;`

// Settings table
await sql`CREATE TABLE IF NOT EXISTS settings (
  key VARCHAR(64) PRIMARY KEY NOT NULL,
  value JSONB NOT NULL
);`
await sql`INSERT INTO settings (key, value) VALUES ('taxRate', '2000'::jsonb)
          ON CONFLICT DO NOTHING;`

export default sql
