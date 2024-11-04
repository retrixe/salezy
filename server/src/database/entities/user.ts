import { ajv } from '../../utils.js'

export interface User {
  username: string
  password: string
}

export const validateUser = ajv.compile<User>({
  type: 'object',
  properties: {
    username: { type: 'string', minLength: 1 },
    password: { type: 'string', minLength: 1 },
  },
  required: ['username', 'password'],
  additionalProperties: false,
})
