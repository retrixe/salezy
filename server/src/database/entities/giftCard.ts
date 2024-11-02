import { ajv } from '../../utils.js'

export interface GiftCard {
  code: string
  issuedBalance: string
  currentBalance: string
  issuedOn: number
  expiresOn: number
  invalid: boolean
}

export const validateGiftCard = ajv.compile<GiftCard>({
  type: 'object',
  properties: {
    code: { type: 'string', maxLength: 320, minLength: 1 },
    issuedBalance: { type: 'string', pattern: '^\\d+$' },
    currentBalance: { type: 'string', pattern: '^\\d+$' },
    issuedOn: { type: 'integer' },
    expiresOn: { type: 'integer' },
    invalid: { type: 'boolean' },
  },
  required: ['code', 'issuedBalance', 'currentBalance', 'issuedOn', 'expiresOn', 'invalid'],
  additionalProperties: false,
})
