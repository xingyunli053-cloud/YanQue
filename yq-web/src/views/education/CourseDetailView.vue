<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Plus, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import PageHeader from '../../components/PageHeader.vue'
import { createCourseDetail, deleteCourseDetail, getCourseDetailList, getCoursePage, importCourseDetails, updateCourseDetail } from '../../api/course'

const courseLoading = ref(false)
const detailLoading = ref(false)
const courses = ref([])
const selectedCourseId = ref(null)
const details = ref([])
const formVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const form = reactive({ stageName: '', dayNumber: null, classContent: '' })
const selectedCourse = computed(() => courses.value.find((item) => item.id === selectedCourseId.value))
const isOffline = computed(() => selectedCourse.value?.teachingMode === 'OFFLINE')
const rules = {
  stageName: [{ required: true, message: '请输入阶段名称', trigger: 'blur' }],
  dayNumber: [{ validator: (_, value, callback) => (!isOffline.value || value ? callback() : callback(new Error('线下课程请输入第几天'))), trigger: 'change' }],
  classContent: [{ validator: (_, value, callback) => (!isOffline.value || value?.trim() ? callback() : callback(new Error('线下课程请输入上课内容'))), trigger: 'blur' }],
}

/** 加载可供选择的课程；课程详情接口按课程 ID 查询。 */
async function loadCourses() {
  courseLoading.value = true
  try {
    const result = await getCoursePage({ pageNum: 1, pageSize: 100 })
    courses.value = result.records || []
    if (!selectedCourseId.value && courses.value.length) {
      selectedCourseId.value = courses.value[0].id
      await loadDetails()
    }
  } finally {
    courseLoading.value = false
  }
}

async function loadDetails() {
  if (!selectedCourseId.value) {
    details.value = []
    return
  }
  detailLoading.value = true
  try {
    details.value = await getCourseDetailList(selectedCourseId.value) || []
  } finally {
    detailLoading.value = false
  }
}

function resetForm() {
  Object.assign(form, { stageName: '', dayNumber: null, classContent: '' })
}

function openCreate() {
  if (!selectedCourseId.value) return ElMessage.warning('请先选择课程')
  editingId.value = null
  resetForm()
  formVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

function openEdit(row) {
  editingId.value = row.id
  Object.assign(form, { stageName: row.stageName, dayNumber: row.dayNumber, classContent: row.classContent })
  formVisible.value = true
  requestAnimationFrame(() => formRef.value?.clearValidate())
}

async function submitDetail() {
  await formRef.value.validate()
  const payload = { ...form }
  if (editingId.value) {
    await updateCourseDetail(editingId.value, payload)
    ElMessage.success('阶段详情已更新')
  } else {
    await createCourseDetail(selectedCourseId.value, payload)
    ElMessage.success('阶段详情已创建')
  }
  formVisible.value = false
  loadDetails()
  loadCourses()
}

async function removeDetail(row) {
  await ElMessageBox.confirm(`确定删除阶段“${row.stageName}”吗？该操作无法撤销。`, '删除课程详情', {
    type: 'warning', confirmButtonText: '确认删除', cancelButtonText: '取消',
  })
  await deleteCourseDetail(row.id)
  ElMessage.success('课程详情已删除')
  loadDetails()
  loadCourses()
}

/** 覆盖式导入：后端会在所有行校验通过后替换当前课程的全部阶段详情。 */
async function uploadDetails({ file }) {
  if (!selectedCourseId.value) return ElMessage.warning('请先选择课程')
  const count = await importCourseDetails(selectedCourseId.value, file)
  ElMessage.success(`已导入 ${count} 条课程详情`)
  loadDetails()
}

function formatTime(value) {
  if (!value) return '—'
  return new Intl.DateTimeFormat('zh-CN', { year: 'numeric', month: '2-digit', day: '2-digit' }).format(new Date(value))
}

onMounted(loadCourses)
</script>

<template>
  <div class="page-shell">
    <PageHeader title="课程详情管理" description="按课程维护阶段安排；线上课程仅维护阶段名称。">
      <el-upload accept=".xlsx,.xls" :show-file-list="false" :http-request="uploadDetails">
        <el-button :disabled="!selectedCourseId">Excel 导入</el-button>
      </el-upload>
      <el-button type="primary" :icon="Plus" :disabled="!selectedCourseId" @click="openCreate">新增阶段</el-button>
    </PageHeader>

    <section class="surface-card table-card">
      <el-form inline class="filter-form">
        <el-form-item label="所属课程">
          <el-select v-model="selectedCourseId" v-loading="courseLoading" filterable placeholder="请选择课程" style="width: 280px" @change="loadDetails">
            <el-option v-for="course in courses" :key="course.id" :label="`${course.courseName}（${course.teachingMode === 'ONLINE' ? '线上' : '线下'}）`" :value="course.id" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button :icon="Refresh" @click="loadDetails">刷新</el-button></el-form-item>
      </el-form>

      <el-table v-loading="detailLoading" :data="details" stripe>
        <el-table-column prop="stageName" label="阶段名称" min-width="220" />
        <el-table-column prop="dayNumber" label="第几天" width="110"><template #default="scope">{{ scope.row.dayNumber ?? '—' }}</template></el-table-column>
        <el-table-column prop="classContent" label="上课内容" min-width="330"><template #default="scope">{{ scope.row.classContent || '—' }}</template></el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="130"><template #default="scope">{{ formatTime(scope.row.updatedAt) }}</template></el-table-column>
        <el-table-column label="操作" width="145" fixed="right"><template #default="scope"><el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button><el-button link type="danger" @click="removeDetail(scope.row)">删除</el-button></template></el-table-column>
        <template #empty><el-empty description="请先选择课程，或该课程暂无阶段详情" /></template>
      </el-table>
    </section>

    <el-dialog v-model="formVisible" :title="editingId ? '编辑阶段详情' : '新增阶段详情'" width="560px" destroy-on-close>
      <el-alert :title="isOffline ? '线下课程：阶段名称、第几天和上课内容均为必填项。' : '线上课程：仅维护阶段名称，天数和上课内容会自动置空。'" :type="isOffline ? 'warning' : 'info'" :closable="false" show-icon class="mode-tip" />
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <el-form-item label="阶段名称" prop="stageName"><el-input v-model="form.stageName" placeholder="请输入阶段名称" /></el-form-item>
        <el-form-item label="第几天" prop="dayNumber"><el-input-number v-model="form.dayNumber" :min="1" :disabled="!isOffline" /></el-form-item>
        <el-form-item label="上课内容" prop="classContent"><el-input v-model="form.classContent" type="textarea" :rows="4" :disabled="!isOffline" placeholder="请输入当天上课内容" /></el-form-item>
      </el-form>
      <template #footer><el-button @click="formVisible = false">取消</el-button><el-button type="primary" @click="submitDetail">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.mode-tip { margin-bottom: 18px; }
</style>
