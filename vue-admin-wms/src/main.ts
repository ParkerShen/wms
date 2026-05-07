import { createApp } from 'vue'
import { createPinia } from 'pinia'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'
import './styles/index.scss'
import { ElNotification } from 'element-plus'
import { setupGlobDirectives } from './directives'

const app = createApp(App)

// 注册 Element Plus 图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 注册全局指令（如 v-permission）
setupGlobDirectives(app)

app.use(createPinia())
app.use(router)
app.mount('#app')

// ====== 全局错误/响应提示 ======

/** API 请求成功通知（已由 request 拦截器自动调用） */
export function showSuccess(msg: string) {
  ElNotification({ title: '成功', message: msg, type: 'success', position: 'top-right', duration: 3000 })
}

/** API 请求失败通知（已由 request 拦截器自动调用） */
export function showError(msg: string) {
  ElNotification({ title: '失败', message: msg, type: 'error', position: 'top-right', duration: 4000 })
}

/** 全局捕获未处理的 Promise 异常，避免控制台红字 */
window.addEventListener('unhandledrejection', (event) => {
  const msg = event.reason?.message || String(event.reason)
  // 如果是 API 错误（拦截器已弹通知），只阻止控制台报错
  if (msg) {
    event.preventDefault()
  }
})
