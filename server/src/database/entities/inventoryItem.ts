import { ajv } from '../../utils.js'

export interface InventoryItem {
  name: string
  imageId: string | null
  upc: number
  sku: string
  costPrice: number
  sellingPrice: number
  quantity: number
}

export type EphemeralInventoryItem = InventoryItem & { image: string | null }

export const validateInventoryItem = ajv.compile<InventoryItem>({
  type: 'object',
  properties: {
    name: { type: 'string', maxLength: 320, minLength: 1 },
    imageId: { type: 'string', nullable: true },
    upc: { type: 'integer' },
    sku: { type: 'string', maxLength: 64, minLength: 1 },
    costPrice: { type: 'integer' },
    sellingPrice: { type: 'integer' },
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
