import request from '@/utils/request'

export interface SoSkuItem {
  id?: number
  skuId?: number
  skuCode: string
  custSkuCode: string
  skuName: string
  qty: number
  pickedQty?: number
}

export interface SoItem {
  id: number; soNo: string; custId: number; custCode: string
  whId: number; whCode: string; status: string
  custReferenceNo: string; productId: number; productCode: string
  shippingType: string
  consigneeName: string; consigneePhone: string; consigneeAddress: string
  consigneeCity: string; consigneeState: string; consigneeZip: string; consigneeCountry: string
  totalSkuQty: number; totalPkgQty: number; remark: string
  createTime: string; modifyTime: string
  skuList: SoSkuItem[]
}

export interface SoPageResult { records: SoItem[]; total: number; current: number; size: number; pages: number }
export interface SoPageParams { page: number; pageSize: number; soNo?: string; custCode?: string; whCode?: string; status?: string }
export interface SoForm {
  id?: number; soNo: string; custId: number | null; custCode: string
  whId: number | null; whCode: string; status: string
  custReferenceNo: string; productId: number | null; productCode: string; shippingType: string
  consigneeName: string; consigneePhone: string; consigneeAddress: string
  consigneeCity: string; consigneeState: string; consigneeZip: string; consigneeCountry: string
  totalSkuQty: number | null; remark: string
  skuList: SoSkuItem[]
}

export function getSoPageApi(params: SoPageParams) { return request.get<any, SoPageResult>('/wms/so/page', { params }) }
export function getSoByIdApi(id: number) { return request.get<any, SoItem>(`/wms/so/${id}`) }
export function createSoApi(data: SoForm) { return request.post('/wms/so', data) }
export function updateSoApi(data: SoForm) { return request.put('/wms/so', data) }
export function deleteSoApi(id: number) { return request.delete(`/wms/so/${id}`) }
export function submitSoApi(id: number) { return request.put(`/wms/so/${id}/submit`) }
