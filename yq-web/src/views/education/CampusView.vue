<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Location, Plus, Refresh, Search } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { createCampus, deleteCampus, getCampusDetail, getCampusPage, updateCampus } from '../../api/campus'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({ campusLocation: '', managerName: '', managerPhone: '' })
const rules = {
  campusLocation: [{ required: true, message: '请输入校区地点', trigger: 'blur' }],
  managerName: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  managerPhone: [
    { required: true, message: '请输入负责人电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入11位中国大陆手机号', trigger: 'blur' },
  ],
}

/** 向后端加载当前分页的校区数据。 */
async function loadCampuses() {
  loading.value = true
  try {
    const result = await getCampusPage(query)
    rows.value = result.records || []
    total.value = result.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '' })
  loadCampuses()
}

function resetForm() {
  Object.assign(form, { campusLocation: '', managerName: '', managerPhone: '' })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

async function openEdit(row) {
  const detail = await getCampusDetail(row.id)
  editingId.value = row.id
  Object.assign(form, detail)
  dialogVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

async function submit() {
  await formRef.value.validate()
  const payload = { ...form }
  if (editingId.value) {
    await updateCampus(editingId.value, payload)
    ElMessage.success('校区信息已更新')
  } else {
    await createCampus(payload)
    ElMessage.success('校区创建成功')
  }
  dialogVisible.value = false
  loadCampuses()
}

async function remove(row) {
  await ElMessageBox.confirm(`确定删除校区“${row.campusLocation}”吗？若校区下已有班级，系统会拒绝删除。`, '删除校区', {
    type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消',
  })
  await deleteCampus(row.id)
  ElMessage.success('校区已删除')
  if (rows.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadCampuses()
}

function formatTime(value) {
  if (!value) return '—'
  return new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date(value))
}

onMounted(loadCampuses)
</script>

<template>
  <div class="page-shell">
    <PageHeader title="校区管理" description="维护教学网点及其负责人信息。">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增校区</el-button>
    </PageHeader>

    <section class="surface-card table-card">
      <el-form inline class="filter-form" @submit.prevent="query.pageNum = 1; loadCampuses()">
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="地点、负责人或电话" clearable :prefix-icon="Search" @clear="query.pageNum = 1; loadCampuses()" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="query.pageNum = 1; loadCampuses()">查询</el-button>
          <el-button :icon="Refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="campusLocation" label="校区地点" min-width="240">
          <template #default="scope"><div class="location-cell"><el-icon><Location /></el-icon><strong>{{ scope.row.campusLocation }}</strong></div></template>
        </el-table-column>
        <el-table-column prop="managerName" label="负责人" min-width="130" />
        <el-table-column prop="managerPhone" label="负责人电话" min-width="160" />
        <el-table-column prop="createdAt" label="创建日期" width="130"><template #default="scope">{{ formatTime(scope.row.createdAt) }}</template></el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="scope"><el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button><el-button link type="danger" @click="remove(scope.row)">删除</el-button></template>
        </el-table-column>
        <template #empty><el-empty description="暂无校区数据" /></template>
      </el-table>

      <div class="pagination-wrap"><el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" layout="total, sizes, prev, pager, next" :page-sizes="[10, 20, 50, 100]" :total="total" background @current-change="loadCampuses" @size-change="query.pageNum = 1; loadCampuses()" /></div>
    </section>

    <el-dialog v-model="dialogVisible" :title="editingId ? '编辑校区' : '新增校区'" width="520px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="校区地点" prop="campusLocation"><el-input v-model="form.campusLocation" placeholder="例如：滨江区江虹路 1750 号" /></el-form-item>
        <el-form-item label="负责人" prop="managerName"><el-input v-model="form.managerName" placeholder="请输入负责人姓名" /></el-form-item>
        <el-form-item label="负责人电话" prop="managerPhone"><el-input v-model="form.managerPhone" maxlength="11" inputmode="numeric" placeholder="请输入11位中国大陆手机号" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="submit">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.location-cell { display: flex; align-items: center; gap: 8px; color: var(--el-text-color-primary); }
.location-cell .el-icon { color: var(--el-color-primary); }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; }
</style>
