import { ajv } from '../../utils.js'

export interface InventoryItem {
  name: string
  imageId: string | null
  upc: string
  sku: string
  costPrice: string
  sellingPrice: string
  quantity: number
}

export type EphemeralInventoryItem = InventoryItem & { image: string | null }

export const validateInventoryItem = ajv.compile<InventoryItem>({
  type: 'object',
  properties: {
    name: { type: 'string', maxLength: 320, minLength: 1 },
    imageId: { type: 'string', nullable: true },
    upc: { type: 'string', pattern: '^\\d+$' },
    sku: { type: 'string', maxLength: 64, minLength: 1 },
    costPrice: { type: 'string', pattern: '^\\d+$' },
    sellingPrice: { type: 'string', pattern: '^\\d+$' },
    quantity: { type: 'integer' },
  },
  required: ['name', 'upc', 'sku', 'costPrice', 'sellingPrice', 'quantity'],
  additionalProperties: false,
})

const { ...ephemeralInventoryItemSchema } = validateInventoryItem.schema as Record<string, any>
ephemeralInventoryItemSchema.properties.image = { type: 'string', nullable: true, minLength: 1 }
export const validateEphemeralInventoryItem = ajv.compile<EphemeralInventoryItem>(
  ephemeralInventoryItemSchema,
)
