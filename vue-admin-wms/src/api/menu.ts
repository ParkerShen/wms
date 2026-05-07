import request from '@/utils/request'

export interface MenuItem {
  id: number
  parentId: number
  menuName: string
  menuType: string
  path: string
  component: string
  permission: string
  icon: string
  sortOrder: number
  visible: number
  status: number
  createTime: string
  children: MenuItem[]
}

export interface MenuForm {
  id?: number
  parentId: number
  menuName: string
  menuType: string
  path: string
  component: string
  permission: string
  icon: string
  sortOrder: number
  visible: number
  status: number
}

/** 获取菜单树 */
export function getMenuTreeApi() {
  return request.get<any, MenuItem[]>('/system/menu/tree')
}

export function getMenuByIdApi(id: number) {
  return request.get<any, MenuItem>(`/system/menu/${id}`)
}

export function createMenuApi(data: MenuForm) {
  return request.post('/system/menu', data)
}

export function updateMenuApi(data: MenuForm) {
  return request.put('/system/menu', data)
}

export function deleteMenuApi(id: number) {
  return request.delete(`/system/menu/${id}`)
}

/** 获取所有菜单ID */
export function getAllMenuIdsApi() {
  return request.get<any, number[]>('/system/menu/ids')
}
