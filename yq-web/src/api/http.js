import axios from 'axios'
import { ElMessage } from 'element-plus'
import { clearSession, getSignSecret, getToken } from '../utils/auth'
import { createNonce, hmacSha256Hex, serializeParams } from '../utils/signature'

const API_PREFIX = '/yq-admin'

const http = axios.create({
  baseURL: API_PREFIX,
  timeout: 15000,
  paramsSerializer: { serialize: serializeParams },
})

http.interceptors.request.use(async (config) => {
  const token = getToken()
  const signSecret = getSignSecret()
  const isLogin = config.url === '/api/sysUser/login'

  if (token) config.headers.Authorization = `Bearer ${token}`

  if (!isLogin && token && signSecret) {
    const method = (config.method || 'GET').toUpperCase()
    const uri = `${API_PREFIX}${config.url}`
    const query = serializeParams(config.params)
    const timestamp = String(Date.now())
    const nonce = createNonce()
    const source = [method, uri, query, timestamp, nonce].join('\n')
    const sign = await hmacSha256Hex(source, signSecret)

    config.headers['X-Timestamp'] = timestamp
    config.headers['X-Nonce'] = nonce
    config.headers['X-Sign'] = sign
  }

  return config
})

http.interceptors.response.use(
  (response) => {
    const payload = response.data
    if (payload?.code !== undefined && payload.code !== 200) {
      const error = new Error(payload.message || '请求处理失败')
      error.notified = true
      ElMessage.error(error.message)
      return Promise.reject(error)
    }
    return payload?.data
  },
  (error) => {
    const status = error.response?.status
    const message = error.response?.data?.message || error.message || '网络请求失败'
    if (status === 401) {
      clearSession()
      if (location.pathname !== '/login') location.replace('/login')
    }
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

export default http
