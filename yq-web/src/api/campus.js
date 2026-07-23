import http from './http'

/** 校区管理接口，与后端 CampusController 的路径保持一致。 */
export const getCampusPage = (params) => http.get('/api/campus', { params })
export const getCampusDetail = (id) => http.get(`/api/campus/${id}`)
export const createCampus = (data) => http.post('/api/campus', data)
export const updateCampus = (id, data) => http.put(`/api/campus/${id}`, data)
export const deleteCampus = (id) => http.delete(`/api/campus/${id}`)
