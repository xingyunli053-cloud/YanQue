<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Connection, Key, Plus, Pointer, Refresh, Search, Tickets } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import {
  assignRolePermissions, createRole, deleteRole, getPermissionTree,
  getRoleDetail, getRolePage, updateRole,
} from '../../api/rbac'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const query = reactive({ pageNum: 1, pageSize: 10, roleCode: '', roleName: '', status: '' })
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const form = reactive({ roleCode: '', roleName: '', description: '', status: 'ACTIVE' })
const permissionVisible = ref(false)
const permissionLoading = ref(false)
const permissionTree = ref([])
const permissionNameMap = ref({})
const permissionTreeRef = ref()
const assigningRole = ref(null)
const selectedPermissionCount = ref(0)

const rules = {
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
}

async function loadRoles() {
  loading.value = true
  try {
    const [result, tree] = await Promise.all([getRolePage(query), getPermissionTree({})])
    rows.value = result.records || []
    total.value = result.total || 0
    permissionTree.value = tree || []
    permissionNameMap.value = Object.fromEntries(
      flattenPermissions(permissionTree.value).map((permission) => [permission.id, permission.permissionName]),
    )
  } finally {
    loading.value = false
  }
}

function flattenPermissions(nodes) {
  return nodes.flatMap((node) => [node, ...flattenPermissions(node.children || [])])
}

function getRolePermissionNames(role) {
  return (role.permissionIds || []).map((id) => permissionNameMap.value[id]).filter(Boolean)
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, roleCode: '', roleName: '', status: '' })
  loadRoles()
}

function resetForm() {
  Object.assign(form, { roleCode: '', roleName: '', description: '', status: 'ACTIVE' })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function openEdit(row) {
  const detail = await getRoleDetail(row.id)
  editingId.value = row.id
  resetForm()
  Object.assign(form, detail)
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function submit() {
  await formRef.value.validate()
  if (editingId.value) {
    await updateRole(editingId.value, { ...form })
    ElMessage.success('角色已更新')
  } else {
    await createRole({ ...form })
    ElMessage.success('角色已创建')
  }
  dialogVisible.value = false
  loadRoles()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除角色“${row.roleName}”吗？相关的角色权限和用户角色数据也会清除。`, '删除角色', {
    type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消',
  })
  await deleteRole(row.id)
  ElMessage.success('角色已删除')
  if (rows.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadRoles()
}

async function openPermissionDialog(row) {
  permissionVisible.value = true
  permissionLoading.value = true
  assigningRole.value = row
  try {
    const [detail, tree] = await Promise.all([getRoleDetail(row.id), getPermissionTree({})])
    permissionTree.value = tree || []
    await nextTick()
    permissionTreeRef.value?.setCheckedKeys(detail.permissionIds || [])
    selectedPermissionCount.value = detail.permissionIds?.length || 0
  } finally {
    permissionLoading.value = false
  }
}

async function savePermissions() {
  const permissionIds = permissionTreeRef.value?.getCheckedKeys(false) || []
  await assignRolePermissions(assigningRole.value.id, permissionIds)
  // 先同步当前行并关闭弹窗，让用户立即看到保存操作已经完成。
  assigningRole.value.permissionIds = [...permissionIds]
  permissionVisible.value = false
  await nextTick()
  ElMessage.success('角色权限已更新')
  // 弹窗关闭后再刷新角色列表，保证展示数据与数据库中的授权结果一致。
  await loadRoles()
}

function collectPermissionIds(nodes) {
  return nodes.flatMap((node) => [node.id, ...collectPermissionIds(node.children || [])])
}

function updateSelectedPermissionCount() {
  selectedPermissionCount.value = permissionTreeRef.value?.getCheckedKeys(false).length || 0
}

function selectAllPermissions() {
  permissionTreeRef.value?.setCheckedKeys(collectPermissionIds(permissionTree.value))
  updateSelectedPermissionCount()
}

function clearPermissions() {
  permissionTreeRef.value?.setCheckedKeys([])
  updateSelectedPermissionCount()
}

onMounted(loadRoles)
</script>

<template>
  <div class="page-shell">
    <PageHeader title="角色管理" description="维护角色基础信息，并通过权限树为角色分配菜单、接口和按钮权限。">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增角色</el-button>
    </PageHeader>

    <section class="surface-card table-card">
      <el-form inline class="filter-form" @submit.prevent="query.pageNum = 1; loadRoles()">
        <el-form-item label="角色编码"><el-input v-model="query.roleCode" clearable placeholder="如 SYSTEM_ADMIN" /></el-form-item>
        <el-form-item label="角色名称"><el-input v-model="query.roleName" clearable placeholder="搜索角色名称" /></el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部状态" style="width: 140px">
            <el-option label="启用" value="ACTIVE" /><el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="query.pageNum = 1; loadRoles()">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="roleCode" label="角色编码" min-width="170"><template #default="scope"><code class="code-chip">{{ scope.row.roleCode }}</code></template></el-table-column>
        <el-table-column prop="roleName" label="角色名称" min-width="130" />
        <el-table-column prop="description" label="说明" min-width="220" show-overflow-tooltip />
        <el-table-column label="已分配权限" min-width="310">
          <template #default="scope">
            <div v-if="getRolePermissionNames(scope.row).length" class="role-permission-tags">
              <el-tag v-for="name in getRolePermissionNames(scope.row).slice(0, 3)" :key="name" size="small" effect="plain" round>{{ name }}</el-tag>
              <el-tooltip v-if="getRolePermissionNames(scope.row).length > 3" :content="getRolePermissionNames(scope.row).slice(3).join('、')">
                <span class="role-permission-more">+{{ getRolePermissionNames(scope.row).length - 3 }}</span>
              </el-tooltip>
            </div>
            <span v-else class="muted-text">暂未分配</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="95"><template #default="scope"><el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'" round>{{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="250" fixed="right">
          <template #default="scope">
            <el-button link type="success" :icon="Key" @click="openPermissionDialog(scope.row)">分配权限</el-button>
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty><el-empty description="暂无角色数据" /></template>
      </el-table>
      <div class="pagination-wrap"><el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :page-sizes="[10,20,50,100]" :total="total" layout="total, sizes, prev, pager, next" background @current-change="loadRoles" @size-change="query.pageNum = 1; loadRoles()" /></div>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑角色' : '新增角色'" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="角色编码" prop="roleCode"><el-input v-model="form.roleCode" placeholder="建议使用大写英文和下划线" /></el-form-item>
          <el-form-item label="角色名称" prop="roleName"><el-input v-model="form.roleName" /></el-form-item>
          <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio-button value="ACTIVE">启用</el-radio-button><el-radio-button value="INACTIVE">禁用</el-radio-button></el-radio-group></el-form-item>
          <el-form-item class="is-full" label="角色说明"><el-input v-model="form.description" type="textarea" :rows="3" maxlength="255" show-word-limit /></el-form-item>
        </div>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="permissionVisible" :title="`为“${assigningRole?.roleName || ''}”分配权限`" width="620px">
      <div v-loading="permissionLoading" class="permission-tree-panel">
        <div class="permission-tree-summary">
          <div><strong>权限范围</strong><span>父子权限可独立选择，保存后覆盖原有授权</span></div>
          <div class="permission-tree-summary__actions">
            <span>已选 <strong>{{ selectedPermissionCount }}</strong> 项</span>
            <el-button link type="primary" @click="selectAllPermissions">全选</el-button>
            <el-button link @click="clearPermissions">清空</el-button>
          </div>
        </div>
        <el-tree ref="permissionTreeRef" class="permission-tree" :data="permissionTree" node-key="id" show-checkbox check-strictly default-expand-all highlight-current :props="{ label: 'permissionName', children: 'children' }" @check="updateSelectedPermissionCount">
          <template #default="{ data }">
            <span class="permission-node">
              <span class="permission-node__icon" :class="`is-${data.permissionType?.toLowerCase()}`">
                <el-icon><Tickets v-if="data.permissionType === 'MENU'" /><Connection v-else-if="data.permissionType === 'API'" /><Pointer v-else /></el-icon>
              </span>
              <span class="permission-node__content"><strong>{{ data.permissionName }}</strong><code>{{ data.permissionCode }}</code></span>
            </span>
          </template>
        </el-tree>
      </div>
      <template #footer><el-button @click="permissionVisible = false">取消</el-button><el-button type="primary" :loading="permissionLoading" @click="savePermissions">保存授权</el-button></template>
    </el-dialog>
  </div>
</template>
