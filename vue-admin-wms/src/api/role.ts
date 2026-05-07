import request from '@/utils/request'

export interface RoleItem {
  id: number
  roleName: string
  roleKey: string
  sortOrder: number
  status: number
  remark: string
  createTime: string
  menuIds: number[]
}

export interface RolePageResult {
  records: RoleItem[]
  total: number
  current: number
  size: number
  pages: number
}

export interface RolePageParams {
  page: number
  pageSize: number
  roleName?: string
  status?: number
}

export interface RoleForm {
  id?: number
  roleName: string
  roleKey: string
  sortOrder: number
  status: number
  remark: string
  menuIds: number[]
}

export function getRolePageApi(params: RolePageParams) {
  return request.get<any, RolePageResult>('/system/role/page', { params })
}

export function getRoleByIdApi(id: number) {
  return request.get<any, RoleItem>(`/system/role/${id}`)
}

export function createRoleApi(data: RoleForm) {
  return request.post('/system/role', data)
}

export function updateRoleApi(data: RoleForm) {
  return request.put('/system/role', data)
}

export function deleteRoleApi(id: number) {
  return request.delete(`/system/role/${id}`)
}

/** 分配菜单权限 */
export function assignRoleMenuApi(roleId: number, menuIds: number[]) {
  return request.put(`/system/role/${roleId}/menu`, menuIds)
}
