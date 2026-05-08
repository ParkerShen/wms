import request from '@/utils/request'

export interface SkuItem {
  id: number
  custId: number
  custCode: string
  skuCode: string
  customerSkuCode: string
  barCode: string
  hsCode: string
  skuType: string
  skuNameZh: string
  skuNameEn: string
  skuNameFr: string
  declaredAmount: number
  declaredWeight: number
  declaredLength: number
  declaredWidth: number
  declaredHeight: number
  declaredVolume: number
  classifyId: number
  brand: string
  originCountry: string
  snType: string
  lotType: string
  magneticFlag: number
  dangerFlag: number
  chargedFlag: number
  liquidFlag: number
  status: string
  remark: string
  createTime: string
  modifyTime: string
}

export interface SkuPageResult {
  records: SkuItem[]
  total: number
  current: number
  size: number
  pages: number
}

export interface SkuPageParams {
  page: number
  pageSize: number
  skuCode?: string
  skuName?: string
  custCode?: string
  status?: string
}

export interface SkuForm {
  id?: number
  custId: number | null
  custCode: string
  skuCode: string
  customerSkuCode: string
  barCode: string
  hsCode: string
  skuType: string
  skuNameZh: string
  skuNameEn: string
  skuNameFr: string
  declaredAmount: number | null
  declaredWeight: number | null
  declaredLength: number | null
  declaredWidth: number | null
  declaredHeight: number | null
  declaredVolume: number | null
  classifyId: number | null
  brand: string
  originCountry: string
  snType: string
  lotType: string
  magneticFlag: number
  dangerFlag: number
  chargedFlag: number
  liquidFlag: number
  status: string
  remark: string
}

export function getSkuPageApi(params: SkuPageParams) {
  return request.get<any, SkuPageResult>('/wms/sku/page', { params })
}
export function getSkuByIdApi(id: number) {
  return request.get<any, SkuItem>(`/wms/sku/${id}`)
}
export function createSkuApi(data: SkuForm) {
  return request.post('/wms/sku', data)
}
export function updateSkuApi(data: SkuForm) {
  return request.put('/wms/sku', data)
}
export function deleteSkuApi(id: number) {
  return request.delete(`/wms/sku/${id}`)
}
