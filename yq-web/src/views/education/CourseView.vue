<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Plus, Refresh, Search, View } from '@element-plus/icons-vue'
import PageHeader from '../../components/PageHeader.vue'
import { createCourse, deleteCourse, getCourseDetail, getCourseDetailList, getCoursePage, updateCourse } from '../../api/course'

const loading = ref(false)
const rows = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogLoading = ref(false)
const selectedCourse = ref(null)
const detailRows = ref([])
const formVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const query = reactive({ pageNum: 1, pageSize: 10, keyword: '' })
const form = reactive({ courseName: '', courseDays: 1, teachingMode: 'ONLINE', materialPath: '' })
const rules = {
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  courseDays: [{ required: true, message: '请输入课程天数', trigger: 'blur' }],
  teachingMode: [{ required: true, message: '请选择上课方式', trigger: 'change' }],
  materialPath: [{ required: true, message: '请输入资料路径', trigger: 'blur' }],
}

/** 加载课程主表分页数据。 */
async function loadCourses() {
  loading.value = true
  try {
    const result = await getCoursePage(query)
    // 课程天数以课程主表接口返回的 courseDays 为准。
    rows.value = result.records || []
    total.value = result.total || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  Object.assign(query, { pageNum: 1, pageSize: 10, keyword: '' })
  loadCourses()
}

function resetForm() {
  Object.assign(form, { courseName: '', courseDays: 1, teachingMode: 'ONLINE', materialPath: '' })
}

function openCreate() {
  editingId.value = null
  resetForm()
  formVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

async function openEdit(row) {
  const course = await getCourseDetail(row.id)
  editingId.value = row.id
  Object.assign(form, course)
  formVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

async function submitCourse() {
  await formRef.value.validate()
  const payload = { ...form }
  if (editingId.value) {
    await updateCourse(editingId.value, payload)
    ElMessage.success('课程已更新')
  } else {
    await createCourse(payload)
    ElMessage.success('课程已创建')
  }
  formVisible.value = false
  loadCourses()
}

/** 课程下仍存在阶段详情时，后端会拒绝删除。 */
async function removeCourse(row) {
  await ElMessageBox.confirm(
    `确定删除课程“${row.courseName}”吗？请先删除该课程下的全部课程详情。`,
    '删除课程',
    { type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消' },
  )
  await deleteCourse(row.id)
  ElMessage.success('课程已删除')
  if (rows.value.length === 1 && query.pageNum > 1) query.pageNum -= 1
  loadCourses()
}

/** 同时读取课程主表和该课程的阶段详情，以便展示完整课程结构。 */
async function openDetails(row) {
  dialogVisible.value = true
  dialogLoading.value = true
  selectedCourse.value = null
  detailRows.value = []
  try {
    const [course, details] = await Promise.all([getCourseDetail(row.id), getCourseDetailList(row.id)])
    // 课程天数与课程主表接口返回值保持一致。
    selectedCourse.value = course
    detailRows.value = details || []
  } catch {
    ElMessage.error('课程详情加载失败')
  } finally {
    dialogLoading.value = false
  }
}

function formatTime(value) {
  if (!value) return '—'
  return new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date(value))
}

onMounted(loadCourses)
</script>

<template>
  <div class="page-shell">
    <PageHeader title="课程管理" description="维护课程基础信息、上课方式与阶段安排。"><el-button type="primary" :icon="Plus" @click="openCreate">新增课程</el-button></PageHeader>

    <section class="surface-card table-card">
      <el-form inline class="filter-form" @submit.prevent="query.pageNum = 1; loadCourses()">
        <el-form-item label="课程名称"><el-input v-model="query.keyword" clearable placeholder="搜索课程名称" :prefix-icon="Search" @clear="query.pageNum = 1; loadCourses()" /></el-form-item>
        <el-form-item><el-button type="primary" :icon="Search" @click="query.pageNum = 1; loadCourses()">查询</el-button><el-button :icon="Refresh" @click="resetQuery">重置</el-button></el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="rows" stripe>
        <el-table-column prop="courseName" label="课程名称" min-width="190"><template #default="scope"><div class="course-cell"><el-icon><Document /></el-icon><strong>{{ scope.row.courseName }}</strong></div></template></el-table-column>
        <el-table-column prop="courseDays" label="课程天数" width="115"><template #default="scope">{{ scope.row.courseDays }} 天</template></el-table-column>
        <el-table-column prop="teachingMode" label="上课方式" width="115"><template #default="scope"><el-tag :type="scope.row.teachingMode === 'ONLINE' ? 'primary' : 'success'" round>{{ scope.row.teachingMode === 'ONLINE' ? '线上' : '线下' }}</el-tag></template></el-table-column>
        <el-table-column prop="materialPath" label="资料路径" min-width="260" show-overflow-tooltip />
        <el-table-column prop="updatedAt" label="更新时间" width="130"><template #default="scope">{{ formatTime(scope.row.updatedAt) }}</template></el-table-column>
        <el-table-column label="操作" width="220" fixed="right"><template #default="scope"><el-button link type="primary" :icon="View" @click="openDetails(scope.row)">查看详情</el-button><el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button><el-button link type="danger" @click="removeCourse(scope.row)">删除</el-button></template></el-table-column>
        <template #empty><el-empty description="暂无课程数据" /></template>
      </el-table>
      <div class="pagination-wrap"><el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" layout="total, sizes, prev, pager, next" :page-sizes="[10, 20, 50, 100]" :total="total" background @current-change="loadCourses" @size-change="query.pageNum = 1; loadCourses()" /></div>
    </section>

    <el-dialog v-model="dialogVisible" :title="`${selectedCourse?.courseName || '课程'} · 阶段详情`" width="780px" destroy-on-close>
      <div v-loading="dialogLoading">
        <div v-if="selectedCourse" class="course-summary"><span>上课方式：<el-tag size="small" :type="selectedCourse.teachingMode === 'ONLINE' ? 'primary' : 'success'">{{ selectedCourse.teachingMode === 'ONLINE' ? '线上课程' : '线下课程' }}</el-tag></span><span>课程天数：<strong>{{ selectedCourse.courseDays }}</strong> 天</span></div>
        <el-table :data="detailRows" size="small" border>
          <el-table-column prop="stageName" label="阶段名称" min-width="180" />
          <el-table-column prop="dayNumber" label="第几天" width="100"><template #default="scope">{{ scope.row.dayNumber ?? '—' }}</template></el-table-column>
          <el-table-column prop="classContent" label="上课内容" min-width="250"><template #default="scope">{{ scope.row.classContent || '—' }}</template></el-table-column>
          <template #empty><el-empty description="该课程暂无阶段详情" :image-size="72" /></template>
        </el-table>
      </div>
    </el-dialog>

    <el-dialog v-model="formVisible" :title="editingId ? '编辑课程' : '新增课程'" width="560px" destroy-on-close>
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" placeholder="请输入课程名称" /></el-form-item>
        <div class="form-grid">
          <el-form-item label="上课方式" prop="teachingMode"><el-radio-group v-model="form.teachingMode"><el-radio-button value="ONLINE">线上</el-radio-button><el-radio-button value="OFFLINE">线下</el-radio-button></el-radio-group></el-form-item>
          <el-form-item label="课程天数" prop="courseDays"><el-input-number v-model="form.courseDays" :min="0" /><small>课程天数可由管理员手动维护</small></el-form-item>
        </div>
        <el-form-item label="资料路径" prop="materialPath"><el-input v-model="form.materialPath" placeholder="例如：/materials/java-basic" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="formVisible = false">取消</el-button><el-button type="primary" @click="submitCourse">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.course-cell { display: flex; align-items: center; gap: 8px; }
.course-cell .el-icon { color: var(--el-color-primary); }
.pagination-wrap { display: flex; justify-content: flex-end; margin-top: 20px; }
.course-summary { display: flex; gap: 22px; padding: 0 0 16px; color: var(--el-text-color-regular); }
.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.form-grid small { display: block; color: var(--el-text-color-secondary); margin-top: 6px; }
</style>
