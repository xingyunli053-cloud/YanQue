import http from './http'

export const login = (data) => http.post('/api/sysUser/login', data)
export const logout = () => http.post('/api/sysUser/logout')
export const getUserPage = (params) => http.get('/api/sysUser', { params })
export const getUserDetail = (id) => http.get(`/api/sysUser/${id}`)
export const createUser = (data) => http.post('/api/sysUser', data)
export const updateUser = (id, data) => http.put(`/api/sysUser/${id}`, data)
export const deleteUser = (id) => http.delete(`/api/sysUser/${id}`)
