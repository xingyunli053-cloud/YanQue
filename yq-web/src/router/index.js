import { createRouter, createWebHistory } from 'vue-router'
import { isAuthenticated } from '../utils/auth'

const routes = [
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue'), meta: { public: true } },
  {
    path: '/',
    component: () => import('../layouts/AdminLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'dashboard', component: () => import('../views/DashboardView.vue'), meta: { title: '运营总览' } },
      { path: 'users', name: 'users', component: () => import('../views/users/UserManagementView.vue'), meta: { title: '用户管理' } },
      { path: 'system/roles', name: 'roles', component: () => import('../views/system/RoleManagementView.vue'), meta: { title: '角色管理' } },
      { path: 'system/permissions', name: 'permissions', component: () => import('../views/system/PermissionManagementView.vue'), meta: { title: '权限管理' } },
      { path: 'teaching/campuses', name: 'campuses', component: () => import('../views/education/CampusView.vue'), meta: { title: '校区管理' } },
      { path: 'teaching/courses', name: 'courses', component: () => import('../views/education/CourseView.vue'), meta: { title: '课程管理' } },
      { path: 'teaching/course-details', name: 'course-details', component: () => import('../views/education/CourseDetailView.vue'), meta: { title: '课程详情管理' } },
      { path: 'teaching/classes', name: 'classes', component: () => import('../views/education/ClassView.vue'), meta: { title: '班级管理' } },
    ],
  },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior: () => ({ top: 0 }),
})

router.beforeEach((to) => {
  if (!to.meta.public && !isAuthenticated()) return { name: 'login', query: { redirect: to.fullPath } }
  if (to.name === 'login' && isAuthenticated()) return { name: 'dashboard' }
  document.title = `${to.meta.title || '登录'} · 雁雀教学运营平台`
  return true
})

export default router
