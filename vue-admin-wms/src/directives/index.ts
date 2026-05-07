import type { App } from 'vue'
import { useUserStore } from '@/store/user'

/**
 * v-permission 指令：根据用户权限标识控制元素显示/隐藏
 *
 * 用法：
 *   <el-button v-permission="'system:user:add'">新增用户</el-button>
 *   <el-button v-permission="['system:user:add', 'system:user:edit']">操作</el-button>
 */
function hasPermission(value: string | string[]): boolean {
  const userStore = useUserStore()
  const perms = userStore.permissions

  const permList = typeof value === 'string' ? [value] : value
  return permList.some(p => perms.includes(p))
}

export function setupGlobDirectives(app: App) {
  app.directive('permission', {
    mounted(el: HTMLElement, binding) {
      if (!hasPermission(binding.value)) {
        el.parentNode?.removeChild(el)
      }
    },
  })
}
