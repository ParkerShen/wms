import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { LoginData, UserInfo } from '@/api/auth'
import { loginApi, logoutApi } from '@/api/auth'
import type { MenuItem } from '@/api/menu'
import { getMenuTreeApi as getAllMenuTree } from '@/api/menu'
import request from '@/utils/request'
import { resetRouter } from '@/router'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref<UserInfo | null>(getLocalUser())
  const menuTree = ref<MenuItem[]>([])

  /** 从 localStorage 安全读取用户信息 */
  function getLocalUser(): UserInfo | null {
    try {
      const raw = localStorage.getItem('userInfo')
      return raw ? JSON.parse(raw) : null
    } catch {
      localStorage.removeItem('userInfo')
      return null
    }
  }

  /** 登录 */
  async function login(loginData: LoginData) {
    const result = await loginApi(loginData)
    token.value = result.token
    userInfo.value = result.user
    localStorage.setItem('token', result.token)
    localStorage.setItem('userInfo', JSON.stringify(result.user))
  }

  /** 获取当前用户的菜单树 */
  async function fetchMenuTree() {
    try {
      const res = await request.get<any, any>('/system/menu/user-tree')
      menuTree.value = res as any
    } catch {
      menuTree.value = []
    }
  }

  /** 登出 */
  async function logout() {
    try {
      await logoutApi()
    } catch {
      // ignore
    }
    token.value = ''
    userInfo.value = null
    menuTree.value = []
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    resetRouter()
  }

  /** 是否已登录 */
  const isLogin = () => !!token.value

  return {
    token,
    userInfo,
    menuTree,
    login,
    fetchMenuTree,
    logout,
    isLogin,
  }
})
