<script setup>
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock, User } from '@element-plus/icons-vue'
import { login } from '../api/user'
import { saveSession } from '../utils/auth'

const route = useRoute()
const router = useRouter()
const formRef = ref()
const loading = ref(false)
const showPassword = ref(false)
const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function submit() {
  await formRef.value.validate()
  loading.value = true
  try {
    const session = await login(form)
    saveSession(session)
    ElMessage.success('欢迎回来')
    router.replace(route.query.redirect || '/dashboard')
  } catch (error) {
    if (!error.notified && !error.response) ElMessage.error(error.message || '登录失败，请检查账号信息')
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <main class="login-page">
    <section class="login-story">
      <div class="login-story__top">
        <div class="login-brand"><span>雁</span><strong>雁雀教育</strong></div>
        <span class="login-story__tag">教学运营管理平台</span>
      </div>
      <div class="login-story__content">
        <p class="login-kicker">MAKE LEARNING VISIBLE</p>
        <h1>让每一间教室，<br />都有清晰的成长轨迹。</h1>
        <p>连接校区、课程与班级，把日常教学运营沉淀成可执行、可追踪的数据。</p>
        <div class="story-metrics">
          <div><strong>4</strong><span>核心教学模块</span></div>
          <div><strong>24h</strong><span>运营数据在线</span></div>
          <div><strong>1</strong><span>统一管理入口</span></div>
        </div>
      </div>
      <div class="orbit orbit--one"></div>
      <div class="orbit orbit--two"></div>
    </section>

    <section class="login-panel">
      <div class="login-card">
        <div class="login-card__header">
          <span class="login-card__eyebrow">WELCOME BACK</span>
          <h2>登录管理端</h2>
          <p>使用管理员账号进入雁雀教学运营中心</p>
        </div>
        <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large" @keyup.enter="submit">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" autocomplete="username" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input
              v-model="form.password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              autocomplete="current-password"
            >
              <template #suffix>
                <el-icon class="password-eye" @click="showPassword = !showPassword">
                  <View v-if="!showPassword" /><Hide v-else />
                </el-icon>
              </template>
            </el-input>
          </el-form-item>
          <div class="login-options">
            <el-checkbox>记住登录状态</el-checkbox>
            <span>需要帮助？</span>
          </div>
          <el-button type="primary" size="large" class="login-submit" :loading="loading" @click="submit">
            进入管理平台
          </el-button>
        </el-form>
        <div class="login-card__foot"><span></span>安全连接已启用<span></span></div>
      </div>
      <p class="login-copyright">© 2026 YanQue Education · 专注每一次教学发生</p>
    </section>
  </main>
</template>
