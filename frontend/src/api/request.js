/**
 * Axios 请求封装 — 统一的 HTTP 客户端配置
 *
 * 核心功能：
 *   1. 自动添加 JWT Token 到请求头
 *   2. 自动解包后端返回的 Result 对象（response.data → 直接返回 {code, message, data}）
 *   3. 401/403 时自动跳转登录页
 *   4. 统一错误提示
 *
 * 所有 API 模块都通过这个 request 实例发送请求，
 * 不需要在每个模块中重复配置 Token 和错误处理。
 */
import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

const request = axios.create({
  baseURL: '/api',       // 开发时通过 Vite proxy 转发到 localhost:8080
  timeout: 10000         // 10秒超时
})

// 请求拦截器：每次请求前自动添加 JWT Token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    // Bearer 认证方式：Authorization: Bearer <token>
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截器：统一处理返回数据
request.interceptors.response.use(
  // 成功时直接返回 data 层（即 Result 对象 {code, message, data}）
  response => response.data,
  // 失败时统一处理错误
  error => {
    if (error.response) {
      // 401 未认证 / 403 无权限 → 清除登录状态，跳转登录页
      if (error.response.status === 401 || error.response.status === 403) {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
      }
      // Element Plus 提示错误信息
      ElMessage.error(error.response.data?.message || '请求失败')
    }
    return Promise.reject(error)
  }
)

export default request
