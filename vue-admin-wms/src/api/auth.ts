import request from '@/utils/request'

/** 登录参数 */
export interface LoginData {
  username: string
  password: string
}

/** 用户信息 */
export interface UserInfo {
  id: number
  username: string
  realName: string
  avatar: string
  email: string
}

/** 登录响应 */
export interface LoginResult {
  token: string
  tokenType: string
  user: UserInfo
}

/** 登录 */
export function loginApi(data: LoginData) {
  return request.post<any, LoginResult>('/auth/login', data)
}

/** 获取用户信息 */
export function getUserInfoApi() {
  return request.get<any, UserInfo>('/auth/me')
}

/** 登出 */
export function logoutApi() {
  return request.post('/auth/logout')
}
