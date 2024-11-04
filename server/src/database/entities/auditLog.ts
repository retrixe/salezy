import sql from '../sql.js'

export enum AuditLogAction {
  CREATE = 0,
  DELETE = 1,
  UPDATE = 2,
}

export interface AuditLog {
  id: number
  actor: string
  action: AuditLogAction
  entity: 'customer' | 'user' | 'inventory_item' | 'settings'
  entityId: string
  prevValue: any
  newValue: any
  timestamp: number
}

export type EphemeralAuditLog = Omit<AuditLog, 'id' | 'timestamp'> & { entityId: number | string }

export const insertAuditLog = async (
  actor: string,
  action: AuditLogAction,
  entity: AuditLog['entity'],
  entityId: string | number,
  prevValue: any,
  newValue: any,
): Promise<void> => {
  await sql`INSERT INTO audit_log (actor, action, entity, entity_id, prev_value, new_value) VALUES (
    ${actor}, ${action}, ${entity}, ${entityId}, ${prevValue}, ${newValue}
  );`
}
