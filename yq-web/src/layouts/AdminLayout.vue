<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { SwitchButton } from '@element-plus/icons-vue'
import { clearSession } from '../utils/auth'
import { logout as logoutRequest } from '../api/user'

const route = useRoute()
const router = useRouter()
const collapsed = ref(false)
const activeMenu = computed(() => route.path)

async function logout() {
  await ElMessageBox.confirm('确定退出当前账号吗？', '退出登录', {
    confirmButtonText: '退出',
    cancelButtonText: '取消',
    type: 'warning',
  })
  try {
    await logoutRequest()
  } finally {
    // 无论请求是否成功，都清除浏览器中的凭证，避免本地残留登录态。
    clearSession()
    router.replace('/login')
  }
}
</script>

<template>
  <el-container class="admin-layout" :class="{ 'is-collapsed': collapsed }">
    <el-aside class="admin-sidebar" :width="collapsed ? '78px' : '248px'">
      <div class="brand" @click="router.push('/dashboard')">
        <div class="brand__mark">雁</div>
        <div v-show="!collapsed" class="brand__text">
          <strong>雁雀</strong>
          <span>YANQUE EDU</span>
        </div>
      </div>

      <el-menu :default-active="activeMenu" router :collapse="collapsed" :collapse-transition="false">
        <div v-show="!collapsed" class="menu-caption">工作台</div>
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon><template #title>运营总览</template></el-menu-item>
        <el-menu-item index="/users"><el-icon><User /></el-icon><template #title>用户管理</template></el-menu-item>
        <div v-show="!collapsed" class="menu-caption">权限中心</div>
        <el-menu-item index="/system/roles"><el-icon><Avatar /></el-icon><template #title>角色管理</template></el-menu-item>
        <el-menu-item index="/system/permissions"><el-icon><Key /></el-icon><template #title>权限管理</template></el-menu-item>
        <div v-show="!collapsed" class="menu-caption">教学管理</div>
        <el-menu-item index="/teaching/campuses"><el-icon><OfficeBuilding /></el-icon><template #title>校区管理</template></el-menu-item>
        <el-menu-item index="/teaching/courses"><el-icon><Reading /></el-icon><template #title>课程管理</template></el-menu-item>
        <el-menu-item index="/teaching/course-details"><el-icon><Notebook /></el-icon><template #title>课程详情管理</template></el-menu-item>
        <el-menu-item index="/teaching/classes"><el-icon><School /></el-icon><template #title>班级管理</template></el-menu-item>
      </el-menu>

      <div v-show="!collapsed" class="sidebar-foot">
        <span class="sidebar-foot__dot"></span>
        服务运行正常
      </div>
    </el-aside>

    <el-container class="admin-main">
      <el-header class="topbar">
        <div class="topbar__left">
          <el-button text class="collapse-button" @click="collapsed = !collapsed">
            <el-icon size="20"><Fold v-if="!collapsed" /><Expand v-else /></el-icon>
          </el-button>
          <div>
            <span class="topbar__date">教学运营中心</span>
            <strong>{{ route.meta.title }}</strong>
          </div>
        </div>
        <div class="topbar__right">
          <el-tooltip content="消息中心"><el-button circle text><el-icon><Bell /></el-icon></el-button></el-tooltip>
          <el-dropdown trigger="click">
            <div class="user-chip">
              <el-avatar :size="34">管</el-avatar>
              <div><strong>管理员</strong><span>系统管理</span></div>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="SwitchButton" @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="content-area">
        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in"><component :is="Component" /></transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>
