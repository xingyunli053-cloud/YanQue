import http from './http'

/** 课程及阶段详情查询接口。 */
export const getCoursePage = (params) => http.get('/api/course', { params })
export const getCourseDetail = (id) => http.get(`/api/course/${id}`)
export const getCourseDetailList = (courseId) => http.get(`/api/course/${courseId}/details`)
export const getCourseDetailItem = (id) => http.get(`/api/course/details/${id}`)
export const createCourse = (data) => http.post('/api/course', data)
export const updateCourse = (id, data) => http.put(`/api/course/${id}`, data)
export const createCourseDetail = (courseId, data) => http.post(`/api/course/${courseId}/details`, data)
export const updateCourseDetail = (id, data) => http.put(`/api/course/details/${id}`, data)
export const deleteCourse = (id) => http.delete(`/api/course/${id}`)
export const deleteCourseDetail = (id) => http.delete(`/api/course/details/${id}`)
export const importCourseDetails = (courseId, file) => {
  const formData = new FormData()
  formData.append('file', file)
  return http.post(`/api/course/${courseId}/details/import`, formData)
}
