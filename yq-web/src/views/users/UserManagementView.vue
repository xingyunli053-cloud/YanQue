<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search, UserFilled } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { createUser, deleteUser, getUserDetail, getUserPage, updateUser } from '../../api/user'
import { assignUserRoles, getRolePage, getUserRoleIds } from '../../api/rbac'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const roleDialogVisible = ref(false)
const roleLoading = ref(false)
const assigningUser = ref(null)
const availableRoles = ref([])
const selectedRoleIds = ref([])
const query = reactive({ pageNum: 1, pageSize: 10, username: '', status: '' })
const form = reactive({
  username: '', password: '', nickname: '', realName: '', phone: '', email: '', unionId: '', status: 'ACTIVE',
})
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ validator: (_, value, callback) => {
    if (!editingId.value && !value) callback(new Error('请输入密码'))
    else if (value && value.length < 6) callback(new Error('密码至少 6 个字符'))
    else callback()
  }, trigger: 'blur' }],
  email: [{ type: 'email', message: '邮箱格式不正确', trigger: 'blur' }],
}

async function loadUsers() {
  loading.value = true
  try {
    const result = await getUserPage(query)
    rows.value = result.records || []
    total.value = result.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, username: '', status: '' })
  loadUsers()
}

function resetForm() {
  Object.assign(form, {
    username: '', password: '', nickname: '', realName: '', phone: '', email: '', unionId: '', status: 'ACTIVE',
  })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

async function openEdit(row) {
  const detail = await getUserDetail(row.id)
  editingId.value = row.id
  resetForm()
  Object.assign(form, detail, { password: '' })
  dialogVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

async function submit() {
  await formRef.value.validate()
  const payload = { ...form }
  if (editingId.value && !payload.password) delete payload.password
  if (editingId.value) {
    await updateUser(editingId.value, payload)
    ElMessage.success('用户信息已更新')
  } else {
    await createUser(payload)
    ElMessage.success('用户创建成功')
  }
  dialogVisible.value = false
  loadUsers()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除用户“${row.username}”吗？该操作无法撤销。`, '删除用户', {
    type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消',
  })
  await deleteUser(row.id)
  ElMessage.success('用户已删除')
  if (rows.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadUsers()
}

async function openRoleDialog(row) {
  assigningUser.value = row
  roleDialogVisible.value = true
  roleLoading.value = true
  try {
    const [roleResult, roleIds] = await Promise.all([
      getRolePage({ pageNum: 1, pageSize: 100, status: 'ACTIVE' }),
      getUserRoleIds(row.id),
    ])
    availableRoles.value = roleResult.records || []
    selectedRoleIds.value = roleIds || []
  } finally {
    roleLoading.value = false
  }
}

async function saveUserRoles() {
  await assignUserRoles(assigningUser.value.id, selectedRoleIds.value)
  ElMessage.success('用户角色已更新')
  roleDialogVisible.value = false
}

function formatTime(value) {
  if (!value) return '—'
  return new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date(value))
}

onMounted(loadUsers)
</script>

<template>
  <div class="page-shell">
    <PageHeader title="用户管理" description="管理系统账号、联系信息与启用状态。">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增用户</el-button>
    </PageHeader>

    <section class="surface-card table-card">
      <el-form inline class="filter-form" @submit.prevent="query.pageNum = 1; loadUsers()">
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="搜索用户名" clearable :prefix-icon="Search" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width: 150px">
            <el-option label="启用" value="ACTIVE" />
            <el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="query.pageNum = 1; loadUsers()">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="username" label="用户名" min-width="130">
          <template #default="scope">
            <div class="user-cell"><el-avatar :size="34">{{ scope.row.username?.slice(0, 1).toUpperCase() }}</el-avatar><strong>{{ scope.row.username }}</strong></div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="昵称" min-width="110" show-overflow-tooltip />
        <el-table-column prop="realName" label="真实姓名" min-width="110" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" min-width="135" />
        <el-table-column prop="email" label="邮箱" min-width="190" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="95">
          <template #default="scope"><el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'" round>{{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建日期" width="120">
          <template #default="scope">{{ formatTime(scope.row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="230" fixed="right">
          <template #default="scope">
            <el-button link type="success" :icon="UserFilled" @click="openRoleDialog(scope.row)">分配角色</el-button>
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无用户数据" /></template>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.pageNum"
          v-model:page-size="query.pageSize"
          layout="total, sizes, prev, pager, next"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          background
          @current-change="loadUsers"
          @size-change="query.pageNum = 1; loadUsers()"
        />
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑用户' : '新增用户'" width="640px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="用户名" prop="username"><el-input v-model="form.username" placeholder="用于登录系统" /></el-form-item>
          <el-form-item :label="editingId ? '重置密码' : '登录密码'" prop="password"><el-input v-model="form.password" type="password" show-password :placeholder="editingId ? '留空表示不修改' : '至少 6 个字符'" /></el-form-item>
          <el-form-item label="昵称"><el-input v-model="form.nickname" /></el-form-item>
          <el-form-item label="真实姓名"><el-input v-model="form.realName" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="邮箱" prop="email"><el-input v-model="form.email" /></el-form-item>
          <el-form-item label="飞书 Union ID"><el-input v-model="form.unionId" /></el-form-item>
          <el-form-item label="账号状态"><el-radio-group v-model="form.status"><el-radio-button value="ACTIVE">启用</el-radio-button><el-radio-button value="INACTIVE">禁用</el-radio-button></el-radio-group></el-form-item>
        </div>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="roleDialogVisible" :title="`为“${assigningUser?.username || ''}”分配角色`" width="620px">
      <div v-loading="roleLoading" class="role-assignment-panel">
        <div class="role-assignment-summary">
          <div><strong>账号角色</strong><span>一个用户可以同时拥有多个角色</span></div>
          <span>已选择 <strong>{{ selectedRoleIds.length }}</strong> 个角色</span>
        </div>
        <el-checkbox-group v-model="selectedRoleIds" class="role-option-grid">
          <el-checkbox v-for="role in availableRoles" :key="role.id" :value="role.id" class="role-option">
            <span class="role-option__icon"><el-icon><UserFilled /></el-icon></span>
            <span class="role-option__content"><strong>{{ role.roleName }}</strong><code>{{ role.roleCode }}</code><small>{{ role.description || '暂无角色说明' }}</small></span>
          </el-checkbox>
        </el-checkbox-group>
        <el-empty v-if="!roleLoading && !availableRoles.length" description="暂无可分配的启用角色" />
      </div>
      <template #footer><el-button @click="roleDialogVisible = false">取消</el-button><el-button type="primary" :loading="roleLoading" @click="saveUserRoles">保存角色</el-button></template>
    </el-dialog>
  </div>
</template>
