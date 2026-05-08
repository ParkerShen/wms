import request from '@/utils/request'

export interface WarehouseItem { /* ...same as before... */
  id: number; whCode: string; whName: string; whType: string
  countryCode: string; currencyCode: string; timeZone: string
  weightUnit: string; lengthUnit: string; status: string; remark: string
  shippingName: string; shippingTel: string; shippingEmail: string
  consigneeName: string; consigneeTel: string; consigneeEmail: string
  postalCode: string; province: string; city: string; address1: string
  contact: string; email: string; telNo: string
  bizType: string; serviceFunction: string
  storageArea: number; storageVolume: number; throughput: number
  handleCapacity: number; validDay: number; storageType: string
  coopPartner: string; wmsSystem: string
  createTime: string; modifyTime: string
}

export interface WarehouseSelectItem { id: number; whCode: string; whName: string }
export interface WarehousePageResult { records: WarehouseItem[]; total: number; current: number; size: number; pages: number }
export interface WarehousePageParams { page: number; pageSize: number; whCode?: string; whName?: string; whType?: string; countryCode?: string; status?: string }
export interface WarehouseForm {
  id?: number; whCode: string; whName: string; whType: string; countryCode: string
  currencyCode: string; timeZone: string; weightUnit: string; lengthUnit: string; status: string; remark: string
  shippingName: string; shippingTel: string; shippingEmail: string
  consigneeName: string; consigneeTel: string; consigneeEmail: string
  postalCode: string; province: string; city: string; address1: string; contact: string; email: string; telNo: string
  bizType: string; serviceFunction: string; storageArea: number | null; storageVolume: number | null
  throughput: number | null; handleCapacity: number | null; validDay: number | null
  storageType: string; coopPartner: string; wmsSystem: string
}

export function getWarehousePageApi(params: WarehousePageParams) { return request.get<any, WarehousePageResult>('/wms/warehouse/page', { params }) }
export function getWarehouseByIdApi(id: number) { return request.get<any, WarehouseItem>(`/wms/warehouse/${id}`) }
export function createWarehouseApi(data: WarehouseForm) { return request.post('/wms/warehouse', data) }
export function updateWarehouseApi(data: WarehouseForm) { return request.put('/wms/warehouse', data) }
export function deleteWarehouseApi(id: number) { return request.delete(`/wms/warehouse/${id}`) }
export function getWarehouseSelectApi() { return request.get<any, WarehouseSelectItem[]>('/wms/warehouse/list-select') }
