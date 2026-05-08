import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { LoginData, UserInfo } from '@/api/auth'
import { loginApi, logoutApi } from '@/api/auth'
import type { MenuItem } from '@/api/menu'
import { getWarehouseSelectApi } from '@/api/warehouse'
import type { WarehouseSelectItem } from '@/api/warehouse'
import request from '@/utils/request'
import { resetRouter } from '@/router'

function extractPermissions(menus: MenuItem[]): string[] {
  const perms: string[] = []
  function walk(list: MenuItem[]) {
    for (const m of list) { if (m.permission) perms.push(m.permission); if (m.children?.length) walk(m.children) }
  }; walk(menus); return perms
}

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(getLocalUser())
  const menuTree = ref<MenuItem[]>([])
  const permissions = computed(() => extractPermissions(menuTree.value))

  // 仓库切换
  const warehouseList = ref<WarehouseSelectItem[]>([])
  const currentWarehouse = ref<WarehouseSelectItem | null>(getLocalWarehouse())

  function getLocalUser(): UserInfo | null {
    try { const raw = localStorage.getItem('userInfo'); return raw ? JSON.parse(raw) : null }
    catch { localStorage.removeItem('userInfo'); return null }
  }

  function getLocalWarehouse(): WarehouseSelectItem | null {
    try { const raw = localStorage.getItem('currentWarehouse'); return raw ? JSON.parse(raw) : null }
    catch { localStorage.removeItem('currentWarehouse'); return null }
  }

  async function login(loginData: LoginData) {
    const result = await loginApi(loginData)
    token.value = result.token; userInfo.value = result.user
    localStorage.setItem('token', result.token); localStorage.setItem('userInfo', JSON.stringify(result.user))
  }

  async function fetchMenuTree() {
    try { const res = await request.get<any, any>('/system/menu/user-tree'); menuTree.value = res as any }
    catch { menuTree.value = [] }
  }

  /** 加载仓库列表并设置默认仓库 */
  async function fetchWarehouseList() {
    try {
      warehouseList.value = await getWarehouseSelectApi()
      // 如果还没有选中仓库，自动选第一个
      if (!currentWarehouse.value && warehouseList.value.length > 0) {
        setCurrentWarehouse(warehouseList.value[0])
      }
    } catch { warehouseList.value = [] }
  }

  /** 切换仓库 */
  function setCurrentWarehouse(wh: WarehouseSelectItem) {
    currentWarehouse.value = wh
    localStorage.setItem('currentWarehouse', JSON.stringify(wh))
  }

  async function logout() {
    try { await logoutApi() } catch { }
    token.value = ''; userInfo.value = null; menuTree.value = []
    currentWarehouse.value = null; warehouseList.value = []
    localStorage.removeItem('token'); localStorage.removeItem('userInfo'); localStorage.removeItem('currentWarehouse')
    resetRouter()
  }

  const isLogin = () => !!token.value

  return {
    token, userInfo, menuTree, permissions,
    warehouseList, currentWarehouse,
    login, fetchMenuTree, fetchWarehouseList, setCurrentWarehouse, logout, isLogin,
  }
})
