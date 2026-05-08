import request from '@/utils/request'

export interface ProductItem {
  id: number
  productCode: string
  productName: string
  productVersion: number
  bizType: string
  productType: string
  countryCode: string
  currencyCode: string
  channelRules: string
  billingRules: string
  billableWeightType: string
  dimWeightCoefficient: number
  freightCoefficient: number
  weightUnit: string
  lengthUnit: string
  onlineFlag: string
  status: string
  remark: string
  odaFlag: number
  podFlag: number
  insuranceFlag: number
  dangerousFlag: number
  returnLabelFlag: number
  endProviderCode: string
  zoneTemplateId: number
  weightTemplateId: number
  createTime: string
  modifyTime: string
}

export interface ProductPageResult {
  records: ProductItem[]
  total: number
  current: number
  size: number
  pages: number
}

export interface ProductPageParams {
  page: number
  pageSize: number
  productCode?: string
  productName?: string
  bizType?: string
  countryCode?: string
  status?: string
}

export interface ProductForm {
  id?: number
  productCode: string
  productName: string
  productVersion: number
  bizType: string
  productType: string
  countryCode: string
  currencyCode: string
  channelRules: string
  billingRules: string
  billableWeightType: string
  dimWeightCoefficient: number
  freightCoefficient: number
  weightUnit: string
  lengthUnit: string
  onlineFlag: string
  status: string
  remark: string
  odaFlag: number
  podFlag: number
  insuranceFlag: number
  dangerousFlag: number
  returnLabelFlag: number
  endProviderCode: string
  zoneTemplateId: number | null
  weightTemplateId: number | null
}

export function getProductPageApi(params: ProductPageParams) {
  return request.get<any, ProductPageResult>('/wms/product/page', { params })
}
export function getProductByIdApi(id: number) {
  return request.get<any, ProductItem>(`/wms/product/${id}`)
}
export function createProductApi(data: ProductForm) {
  return request.post('/wms/product', data)
}
export function updateProductApi(data: ProductForm) {
  return request.put('/wms/product', data)
}
export function deleteProductApi(id: number) {
  return request.delete(`/wms/product/${id}`)
}
