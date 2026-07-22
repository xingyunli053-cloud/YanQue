import http from './http'

export const getRolePage = (params) => http.get('/api/sysRole', { params })
export const getRoleDetail = (id) => http.get(`/api/sysRole/${id}`)
export const createRole = (data) => http.post('/api/sysRole', data)
export const updateRole = (id, data) => http.put(`/api/sysRole/${id}`, data)
export const deleteRole = (id) => http.delete(`/api/sysRole/${id}`)
export const assignRolePermissions = (id, permissionIds) =>
  http.put(`/api/sysRole/${id}/permissions`, { permissionIds })

export const getPermissionPage = (params) => http.get('/api/sysPermission', { params })
export const getPermissionTree = (params) => http.get('/api/sysPermission/tree', { params })
export const getPermissionDetail = (id) => http.get(`/api/sysPermission/${id}`)
export const createPermission = (data) => http.post('/api/sysPermission', data)
export const updatePermission = (id, data) => http.put(`/api/sysPermission/${id}`, data)
export const deletePermission = (id) => http.delete(`/api/sysPermission/${id}`)

export const getUserRoleIds = (userId) => http.get(`/api/sysUserRole/user/${userId}/roles`)
export const assignUserRoles = (userId, roleIds) =>
  http.put(`/api/sysUserRole/user/${userId}/roles`, { roleIds })
