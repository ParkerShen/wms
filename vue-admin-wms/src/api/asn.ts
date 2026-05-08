import request from '@/utils/request'

export interface AsnSkuItem {
  id?: number
  skuId?: number
  skuCode: string
  custSkuCode: string
  skuName: string
  expectedQty: number
  actualQty?: number
  badQty?: number
}

export interface AsnItem {
  id: number
  asnNo: string
  custId: number
  custCode: string
  whId: number
  whCode: string
  asnType: string
  status: string
  custReferenceNo: string
  totalSkuQty: number
  totalPkgQty: number
  receiptMode: string
  remark: string
  createTime: string
  modifyTime: string
  skuList: AsnSkuItem[]
}

export interface AsnPageResult { records: AsnItem[]; total: number; current: number; size: number; pages: number }
export interface AsnPageParams { page: number; pageSize: number; asnNo?: string; custCode?: string; whCode?: string; status?: string }
export interface AsnForm {
  id?: number; asnNo: string; custId: number | null; custCode: string
  whId: number | null; whCode: string; asnType: string; status: string
  custReferenceNo: string; receiptMode: string; remark: string
  totalSkuQty: number | null; totalPkgQty: number | null
  skuList: AsnSkuItem[]
}

export function getAsnPageApi(params: AsnPageParams) { return request.get<any, AsnPageResult>('/wms/asn/page', { params }) }
export function getAsnByIdApi(id: number) { return request.get<any, AsnItem>(`/wms/asn/${id}`) }
export function createAsnApi(data: AsnForm) { return request.post('/wms/asn', data) }
export function updateAsnApi(data: AsnForm) { return request.put('/wms/asn', data) }
export function deleteAsnApi(id: number) { return request.delete(`/wms/asn/${id}`) }
export function submitAsnApi(id: number) { return request.put(`/wms/asn/${id}/submit`) }
