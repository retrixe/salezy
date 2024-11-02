import { ajv } from '../../utils.js'

export interface Invoice {
  id: number
  customerId: number
  costPreTax: string
  costPostTax: string
  taxRate: number
  issuedOn: number
  items: InvoicedItem[]
  paymentMethod: number
  giftCardCode?: string
  giftCardAmount?: string
  notes?: string
}

export interface InvoicedItem {
  upc: string
  quantity: number

  name: string
  sku: string
  costPrice: string
  sellingPrice: string
}

export type EphemeralInvoice = Invoice & { items: Array<{ upc: string; quantity: number }> }

export const validateInvoice = ajv.compile<Invoice>({
  type: 'object',
  properties: {
    id: { type: 'integer' },
    customerId: { type: 'integer' },
    costPreTax: { type: 'string', pattern: '^\\d+$' },
    costPostTax: { type: 'string', pattern: '^\\d+$' },
    taxRate: { type: 'number' },
    issuedOn: { type: 'integer' },
    items: {
      type: 'array',
      items: {
        type: 'object',
        properties: {
          upc: { type: 'string', pattern: '^\\d+$' },
          quantity: { type: 'integer' },
          name: { type: 'string', maxLength: 320, minLength: 1 },
          sku: { type: 'string', maxLength: 64, minLength: 1 },
          costPrice: { type: 'string', pattern: '^\\d+$' },
          sellingPrice: { type: 'string', pattern: '^\\d+$' },
        },
        required: ['upc', 'quantity', 'name', 'costPrice', 'sellingPrice'],
        additionalProperties: false,
      },
    },
    paymentMethod: { type: 'number' },
    giftCardCode: { type: 'string', nullable: true },
    giftCardAmount: { type: 'string', nullable: true, pattern: '^\\d+$' },
    notes: { type: 'string', nullable: true },
  },
  required: [
    'id',
    'customerId',
    'costPreTax',
    'costPostTax',
    'taxRate',
    'issuedOn',
    'items',
    'paymentMethod',
  ],
  additionalProperties: false,
})
