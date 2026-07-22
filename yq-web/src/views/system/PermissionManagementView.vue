<script setup>
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import {
  createPermission, deletePermission, getPermissionDetail,
  getPermissionTree, updatePermission,
} from '../../api/rbac'

const loading = ref(false)
const treeRows = ref([])
const query = reactive({ keyword: '', permissionType: '', status: '' })
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const parentOptions = ref([])
const form = reactive({
  parentId: 0, permissionCode: '', permissionName: '', permissionType: 'MENU',
  apiPath: '', sortNum: 0, description: '', status: 'ACTIVE',
})
const rules = {
  permissionCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionType: [{ required: true, message: '请选择权限类型', trigger: 'change' }],
  apiPath: [{ validator: (_, value, callback) => {
    if (form.permissionType === 'API' && !value?.trim()) callback(new Error('API权限必须填写请求方法和路径'))
    else callback()
  }, trigger: 'blur' }],
}

async function loadTree() {
  loading.value = true
  try {
    treeRows.value = await getPermissionTree(query) || []
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { keyword: '', permissionType: '', status: '' })
  loadTree()
}

function resetForm() {
  Object.assign(form, {
    parentId: 0, permissionCode: '', permissionName: '', permissionType: 'MENU',
    apiPath: '', sortNum: 0, description: '', status: 'ACTIVE',
  })
}

function collectBlockedIds(nodes, targetId, blocked = new Set()) {
  for (const node of nodes) {
    if (node.id === targetId || blocked.has(node.parentId)) blocked.add(node.id)
    collectBlockedIds(node.children || [], targetId, blocked)
  }
  return blocked
}

function filterTree(nodes, blocked) {
  return nodes.filter((node) => !blocked.has(node.id)).map((node) => ({
    ...node, children: filterTree(node.children || [], blocked),
  }))
}

async function prepareParentOptions(currentId = null) {
  const all = await getPermissionTree({}) || []
  const blocked = currentId ? collectBlockedIds(all, currentId) : new Set()
  parentOptions.value = [{ id: 0, permissionName: '根权限', children: filterTree(all, blocked) }]
}

async function openCreate(parent = null) {
  editingId.value = null
  resetForm()
  if (parent?.id) form.parentId = parent.id
  await prepareParentOptions()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function openEdit(row) {
  const [detail] = await Promise.all([getPermissionDetail(row.id), prepareParentOptions(row.id)])
  editingId.value = row.id
  resetForm()
  Object.assign(form, detail)
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function submit() {
  await formRef.value.validate()
  const payload = { ...form, apiPath: form.permissionType === 'API' ? form.apiPath : '' }
  if (editingId.value) {
    await updatePermission(editingId.value, payload)
    ElMessage.success('权限已更新')
  } else {
    await createPermission(payload)
    ElMessage.success('权限已创建')
  }
  dialogVisible.value = false
  loadTree()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除权限“${row.permissionName}”吗？存在子权限时不能删除。`, '删除权限', {
    type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消',
  })
  await deletePermission(row.id)
  ElMessage.success('权限已删除')
  loadTree()
}

onMounted(loadTree)
</script>

<template>
  <div class="page-shell">
    <PageHeader title="权限管理" description="以树形结构维护菜单、接口和按钮权限，父子关系由 parent_id 建立。">
      <el-button type="primary" :icon="Plus" @click="openCreate()">新增根权限</el-button>
    </PageHeader>

    <section class="surface-card table-card">
      <el-form inline class="filter-form" @submit.prevent="loadTree">
        <el-form-item label="关键字"><el-input v-model="query.keyword" clearable placeholder="权限名称或编码" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="query.permissionType" clearable placeholder="全部类型" style="width: 140px">
            <el-option label="菜单 MENU" value="MENU" /><el-option label="接口 API" value="API" /><el-option label="按钮 BUTTON" value="BUTTON" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable placeholder="全部状态" style="width: 130px">
            <el-option label="启用" value="ACTIVE" /><el-option label="禁用" value="INACTIVE" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" :icon="Search" @click="loadTree">查询</el-button><el-button :icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="treeRows" row-key="id" default-expand-all :tree-props="{ children: 'children' }">
        <el-table-column prop="permissionName" label="权限名称" min-width="210" />
        <el-table-column prop="permissionCode" label="权限编码" min-width="220"><template #default="scope"><code class="code-chip">{{ scope.row.permissionCode }}</code></template></el-table-column>
        <el-table-column prop="permissionType" label="类型" width="105"><template #default="scope"><el-tag :type="scope.row.permissionType === 'API' ? 'warning' : scope.row.permissionType === 'BUTTON' ? 'info' : 'success'" effect="plain">{{ scope.row.permissionType }}</el-tag></template></el-table-column>
        <el-table-column prop="apiPath" label="接口路径" min-width="220"><template #default="scope"><code v-if="scope.row.apiPath" class="api-path">{{ scope.row.apiPath }}</code><span v-else>—</span></template></el-table-column>
        <el-table-column prop="sortNum" label="排序" width="75" />
        <el-table-column prop="status" label="状态" width="90"><template #default="scope"><el-tag :type="scope.row.status === 'ACTIVE' ? 'success' : 'info'" round>{{ scope.row.status === 'ACTIVE' ? '启用' : '禁用' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="210" fixed="right"><template #default="scope"><el-button link type="success" @click="openCreate(scope.row)">新增子项</el-button><el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button><el-button link type="danger" @click="remove(scope.row)">删除</el-button></template></el-table-column>
        <template #empty><el-empty description="暂无权限数据" /></template>
      </el-table>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑权限' : '新增权限'" width="680px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item label="父权限">
            <el-tree-select v-model="form.parentId" :data="parentOptions" node-key="id" :props="{ label: 'permissionName', children: 'children' }" check-strictly default-expand-all style="width: 100%" />
          </el-form-item>
          <el-form-item label="权限类型" prop="permissionType"><el-select v-model="form.permissionType" style="width: 100%"><el-option label="菜单 MENU" value="MENU" /><el-option label="接口 API" value="API" /><el-option label="按钮 BUTTON" value="BUTTON" /></el-select></el-form-item>
          <el-form-item label="权限编码" prop="permissionCode"><el-input v-model="form.permissionCode" placeholder="如 system:user:list" /></el-form-item>
          <el-form-item label="权限名称" prop="permissionName"><el-input v-model="form.permissionName" /></el-form-item>
          <el-form-item v-if="form.permissionType === 'API'" class="is-full" label="接口路径" prop="apiPath"><el-input v-model="form.apiPath" placeholder="例如 GET /api/sysRole" /></el-form-item>
          <el-form-item label="排序值"><el-input-number v-model="form.sortNum" :min="0" :max="9999" controls-position="right" style="width: 100%" /></el-form-item>
          <el-form-item label="状态"><el-radio-group v-model="form.status"><el-radio-button value="ACTIVE">启用</el-radio-button><el-radio-button value="INACTIVE">禁用</el-radio-button></el-radio-group></el-form-item>
          <el-form-item class="is-full" label="权限说明"><el-input v-model="form.description" type="textarea" :rows="3" maxlength="255" show-word-limit /></el-form-item>
        </div>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>
