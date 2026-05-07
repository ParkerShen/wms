import request from '@/utils/request'

export interface UserItem {
  id: number
  username: string
  realName: string
  email: string
  phone: string
  status: number
  createTime: string
  roleIds: number[]
}

export interface UserPageResult {
  records: UserItem[]
  total: number
  current: number
  size: number
  pages: number
}

export interface UserPageParams {
  page: number
  pageSize: number
  username?: string
  realName?: string
  status?: number
}

export interface UserForm {
  id?: number
  username: string
  password?: string
  realName: string
  email: string
  phone: string
  status: number
  roleIds: number[]
}

/** 分页查询 */
export function getUserPageApi(params: UserPageParams) {
  return request.get<any, UserPageResult>('/system/user/page', { params })
}

/** 查询单个 */
export function getUserByIdApi(id: number) {
  return request.get<any, UserItem>(`/system/user/${id}`)
}

/** 新增 */
export function createUserApi(data: UserForm) {
  return request.post('/system/user', data)
}

/** 修改 */
export function updateUserApi(data: UserForm) {
  return request.put('/system/user', data)
}

/** 删除 */
export function deleteUserApi(id: number) {
  return request.delete(`/system/user/${id}`)
}

/** 分配角色 */
export function assignUserRoleApi(userId: number, roleIds: number[]) {
  return request.put(`/system/user/${userId}/role`, roleIds)
}
