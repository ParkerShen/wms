import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'

export const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { title: '登录' },
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/DashboardView.vue'),
        meta: { title: '工作台', icon: 'Odometer' },
      },
      {
        path: 'system/user',
        name: 'SystemUser',
        component: () => import('@/views/system/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' },
      },
      {
        path: 'system/role',
        name: 'SystemRole',
        component: () => import('@/views/system/role/index.vue'),
        meta: { title: '角色管理', icon: 'Avatar' },
      },
      {
        path: 'system/menu',
        name: 'SystemMenu',
        component: () => import('@/views/system/menu/index.vue'),
        meta: { title: '菜单管理', icon: 'Menu' },
      },
      // WMS 模块
      {
        path: 'wms/warehouse',
        name: 'WmsWarehouse',
        component: () => import('@/views/wms/warehouse/index.vue'),
        meta: { title: '仓库设置', icon: 'OfficeBuilding' },
      },
      {
        path: 'wms/customer',
        name: 'WmsCustomer',
        component: () => import('@/views/wms/customer/index.vue'),
        meta: { title: '客户管理', icon: 'UserFilled' },
      },
      {
        path: 'wms/sku',
        name: 'WmsSku',
        component: () => import('@/views/wms/sku/index.vue'),
        meta: { title: 'SKU管理', icon: 'Goods' },
      },
      {
        path: 'wms/product',
        name: 'WmsProduct',
        component: () => import('@/views/wms/product/index.vue'),
        meta: { title: '产品管理', icon: 'List' },
      },
      {
        path: 'wms/asn',
        name: 'WmsAsn',
        component: () => import('@/views/wms/asn/index.vue'),
        meta: { title: '入库管理', icon: 'Download' },
      },
      {
        path: 'wms/so',
        name: 'WmsSo',
        component: () => import('@/views/wms/so/index.vue'),
        meta: { title: '出库管理', icon: 'Upload' },
      },
      {
        path: 'wms/inventory',
        name: 'WmsInventory',
        component: () => import('@/views/wms/inventory/index.vue'),
        meta: { title: '库存管理', icon: 'Coin' },
      },
    ],
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

// 路由守卫 - 未登录跳转登录页
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next({ path: '/login', query: { redirect: to.fullPath } })
  } else {
    next()
  }
})

/** 重置路由（登出时用） */
export function resetRouter() {
  router.replace('/login')
}

export default router
