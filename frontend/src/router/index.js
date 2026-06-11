/**
 * Vue Router 路由配置
 *
 * 路由设计说明：
 *   - 使用 createWebHistory()（HTML5 History 模式），URL 更美观
 *   - 使用懒加载（() => import(...)）按需加载页面组件
 *   - meta.noAuth: 标记不需要登录的页面（如登录页）
 *   - meta.role: 限制页面只能由特定角色访问
 *
 * 路由守卫（beforeEach）：
 *   1. noAuth 页面 → 直接放行
 *   2. 无 token → 跳转登录页
 *   3. 有 token 但角色不匹配 → 跳转登录页（使用对应角色账号登录）
 */
import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/LoginView.vue'),
    meta: { noAuth: true }  // 不需要登录
  },
  {
    path: '/',
    redirect: '/patients'   // 根路径重定向到患者管理
  },
  {
    path: '/patients',
    name: 'Patients',
    component: () => import('@/views/PatientList.vue'),
    meta: { role: 'REGISTRAR' }  // 仅挂号员
  },
  {
    path: '/registrations',
    name: 'Registrations',
    component: () => import('@/views/RegistrationList.vue'),
    meta: { role: 'REGISTRAR' }  // 仅挂号员
  },
  {
    path: '/consultations',
    name: 'Consultations',
    component: () => import('@/views/ConsultationList.vue'),
    meta: { role: 'DOCTOR' }  // 仅医生
  },
  {
    path: '/prescriptions',
    name: 'Prescriptions',
    component: () => import('@/views/PrescriptionList.vue'),
    meta: { role: 'DOCTOR' }  // 仅医生
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

/**
 * 全局路由守卫 — 每次路由切换前执行
 * 实现前端权限控制：未登录跳转、角色不匹配跳转
 */
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 不需要认证的页面直接放行
  if (to.meta.noAuth) {
    next()
    return
  }

  // 未登录 → 跳转登录页
  if (!token) {
    next('/login')
    return
  }

  // 检查角色权限
  const user = JSON.parse(localStorage.getItem('user') || '{}')
  if (to.meta.role && user.role !== to.meta.role) {
    next('/login')  // 角色不匹配，重新登录
    return
  }

  next()
})

export default router
