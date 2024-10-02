import { hash } from 'argon2'
import postgres from 'postgres'
import { postgresConf } from '../config.js'

const sql = postgres({ ...postgresConf, transform: postgres.camel })

// https://stackoverflow.com/questions/4107915/postgresql-default-constraint-names/4108266#4108266

// Users table
await sql`CREATE TABLE IF NOT EXISTS users (
  username VARCHAR(320) PRIMARY KEY NOT NULL,
  password VARCHAR(128) NOT NULL
);`
await sql`INSERT INTO users (username, password) VALUES ('admin', ${await hash('admin')})
          ON CONFLICT DO NOTHING;`

// Customers table
await sql`CREATE TABLE IF NOT EXISTS customers (
  id SERIAL PRIMARY KEY,
  name VARCHAR(320) NULL,
  email VARCHAR(320) NULL,
  phone VARCHAR(20) NULL,
  address TEXT NULL,
  tax_id_number VARCHAR(64) NULL,
  notes TEXT NULL
);`
// TODO: Provide indexes...

// Settings table
await sql`CREATE TABLE IF NOT EXISTS settings (
  key VARCHAR(64) PRIMARY KEY NOT NULL,
  value JSONB NOT NULL
);`
await sql`INSERT INTO settings (key, value) VALUES ('taxRate', '2000'::jsonb)
          ON CONFLICT DO NOTHING;`

// Audit log table
await sql`CREATE TABLE IF NOT EXISTS audit_log (
  id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  actor VARCHAR(320) NOT NULL,
  action INT NOT NULL, /* 0 - create; 1 - delete; 2 - update */
  entity VARCHAR(64) NOT NULL,
  entity_id INT NOT NULL,
  prev_value JSONB NULL,
  new_value JSONB NOT NULL,
  timestamp TIMESTAMPTZ NOT NULL DEFAULT NOW()
);`

export default sql
