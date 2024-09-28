import { ajv } from '../../utils.js'

export interface SettingRow {
  key: string
  value: string
}

export interface Settings {
  taxRate: number
}

export const validateSettings = ajv.compile<Settings>({
  type: 'object',
  properties: {
    taxRate: { type: 'integer' },
  },
  required: ['taxRate'],
  additionalProperties: false,
})
