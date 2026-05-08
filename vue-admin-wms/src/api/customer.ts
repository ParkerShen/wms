import request from '@/utils/request'

export interface CustomerItem {
  id: number
  custCode: string
  custName: string
  contact: string
  email: string
  telNo: string
  address: string
  apiAccount: string
  apiPassword: string
  status: string
  remark: string
  autoAuditSku: number
  autoSkuCode: number
  skuClassifyRequired: number
  servicePlatform: string
  skuWhSyncType: string
  expressAdvanceFlag: number
  supportCustProvider: number
  matchLabelFlag: number
  newFeeStructure: number
  orderSource: number
  shippingCode: string
  shippingAddress: string
  createTime: string
  modifyTime: string
}

export interface CustomerPageResult {
  records: CustomerItem[]
  total: number
  current: number
  size: number
  pages: number
}

export interface CustomerPageParams {
  page: number
  pageSize: number
  custCode?: string
  custName?: string
  status?: string
  servicePlatform?: string
}

export interface CustomerForm {
  id?: number
  custCode: string
  custName: string
  contact: string
  email: string
  telNo: string
  address: string
  apiAccount: string
  apiPassword: string
  status: string
  remark: string
  autoAuditSku: number
  autoSkuCode: number
  skuClassifyRequired: number
  servicePlatform: string
  skuWhSyncType: string
  expressAdvanceFlag: number
  supportCustProvider: number
  matchLabelFlag: number
  newFeeStructure: number
  orderSource: number
  shippingCode: string
  shippingAddress: string
}

export function getCustomerPageApi(params: CustomerPageParams) {
  return request.get<any, CustomerPageResult>('/wms/customer/page', { params })
}
export function getCustomerByIdApi(id: number) {
  return request.get<any, CustomerItem>(`/wms/customer/${id}`)
}
export function createCustomerApi(data: CustomerForm) {
  return request.post('/wms/customer', data)
}
export function updateCustomerApi(data: CustomerForm) {
  return request.put('/wms/customer', data)
}
export function deleteCustomerApi(id: number) {
  return request.delete(`/wms/customer/${id}`)
}
