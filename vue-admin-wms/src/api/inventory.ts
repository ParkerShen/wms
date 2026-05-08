import request from '@/utils/request'

export interface InvLocItem {
  id: number; whId: number; whCode: string; locCode: string; zoneCode: string
  skuId: number; skuCode: string; custId: number; custCode: string; lotNo: string
  qty: number; totalQty: number; badQty: number
  holdQty: number; allocQty: number; pickingQty: number
  lastUpdateTime: string; createTime: string
}

export interface InvLocPageResult { records: InvLocItem[]; total: number; current: number; size: number; pages: number }
export interface InvLocPageParams { page: number; pageSize: number; whCode?: string; locCode?: string; skuCode?: string; custCode?: string }

export function getInvLocPageApi(params: InvLocPageParams) { return request.get<any, InvLocPageResult>('/wms/inventory/page', { params }) }
