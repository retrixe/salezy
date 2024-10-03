import { ajv } from '../../utils.js'

export interface Customer {
  id: number
  phone: string | null
  name: string | null
  email: string | null
  address: string | null
  taxIdNumber: string | null
  notes: string | null
}

export type EphemeralCustomer = Omit<Customer, 'id'>

export const validateCustomer = ajv.compile<Customer>({
  type: 'object',
  properties: {
    id: { type: 'integer' },
    phone: { type: 'string', maxLength: 20, minLength: 1, nullable: true },
    name: { type: 'string', maxLength: 320, minLength: 1, nullable: true },
    email: { type: 'string', maxLength: 320, minLength: 1, nullable: true },
    address: { type: 'string', nullable: true },
    taxIdNumber: { type: 'string', maxLength: 64, minLength: 1, nullable: true },
    notes: { type: 'string', nullable: true },
  },
  required: ['id'],
  additionalProperties: false,
})

const { ...ephemeralCustomerSchema } = validateCustomer.schema as Record<string, any>
delete ephemeralCustomerSchema.required
delete ephemeralCustomerSchema.properties.id
export const validateEphemeralCustomer = ajv.compile<EphemeralCustomer>(ephemeralCustomerSchema)
