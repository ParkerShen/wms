import axios from 'axios'
import { ElNotification } from 'element-plus'
import { useUserStore } from '@/store/user'
import router from '@/router'

// 写操作的方法（Axios 的 method 是小写）
const WRITE_METHODS = ['post', 'put', 'delete']

/** API 基础地址：开发环境空 = 使用 Vite 代理；测试/生产 = 真实 API 地址 */
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api'

/** 右上角成功通知 */
export function notifySuccess(msg: string) {
  ElNotification({ title: '成功', message: msg, type: 'success', position: 'top-right', duration: 3000 })
}

/** 右上角错误通知 */
export function notifyError(msg: string) {
  ElNotification({ title: '失败', message: msg, type: 'error', position: 'top-right', duration: 4000 })
}

const request = axios.create({
  baseURL: API_BASE_URL,
  timeout: 30000,
})

// 请求拦截 - 带 Token
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截 - 统一处理错误 & 自动提示
request.interceptors.response.use(
  (res) => {
    const body = res.data
    if (body.code !== 200) {
      notifyError(body.msg || '请求失败')
      return Promise.reject(new Error(body.msg))
    }

    // 仅写操作（post/put/delete）显示成功通知
    if (WRITE_METHODS.includes(res.config.method?.toLowerCase() || '') && body.msg && body.msg !== '操作成功') {
      notifySuccess(body.msg)
    }
    return body.data
  },
  (error) => {
    if (error.response?.status === 401) {
      notifyError('登录已过期，请重新登录')
      const userStore = useUserStore()
      userStore.logout()
      router.push('/login')
    } else if (error.response?.status === 500) {
      notifyError('服务器繁忙')
    } else {
      notifyError(error.message || '网络异常')
    }
    return Promise.reject(error)
  },
)

export default request
