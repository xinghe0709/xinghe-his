/**
 * 用户状态管理（Pinia Store）
 *
 * 采用 Composition API 风格（Setup Store）：
 *   - token: 存储 JWT Token
 *   - user: 存储用户基本信息（realName, role）
 *
 * 登录流程：
 *   1. 调用 login() → 请求 /api/auth/login
 *   2. 将 token 和 user 信息存入 localStorage 实现持久化
 *   3. 路由守卫（router.beforeEach）从 localStorage 读取进行权限判断
 *
 * 为什么用 localStorage 而不是 Pinia 持久化插件？
 *   简单直接，路由守卫可以直接读取而无需引入额外依赖。
 *
 * 退出流程：清除内存状态和 localStorage，路由守卫会自动跳转到登录页。
 */
import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as loginApi } from '@/api/auth'

export const useUserStore = defineStore('user', () => {
  // 从 localStorage 恢复登录状态（页面刷新后保持登录）
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null'))

  /** 登录并持久化 */
  async function login(username, password) {
    const res = await loginApi({ username, password })
    token.value = res.data.token
    user.value = { realName: res.data.realName, role: res.data.role }
    // 持久化到 localStorage，刷新页面后仍保持登录状态
    localStorage.setItem('token', token.value)
    localStorage.setItem('user', JSON.stringify(user.value))
  }

  /** 退出登录并清除持久化数据 */
  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  return { token, user, login, logout }
})
