<script setup>
import { computed, nextTick, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import PageHeader from './PageHeader.vue'

const props = defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  resourceName: { type: String, required: true },
  storageKey: { type: String, required: true },
  seedData: { type: Array, default: () => [] },
  columns: { type: Array, required: true },
  fields: { type: Array, required: true },
  searchPlaceholder: { type: String, default: '输入关键词搜索' },
})

const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(8)
const dialogVisible = ref(false)
const editingId = ref(null)
const formRef = ref()
const form = reactive({})

function readRecords() {
  const stored = localStorage.getItem(props.storageKey)
  if (stored) {
    try { return JSON.parse(stored) } catch { /* 使用初始化数据 */ }
  }
  localStorage.setItem(props.storageKey, JSON.stringify(props.seedData))
  return JSON.parse(JSON.stringify(props.seedData))
}

const records = ref(readRecords())
const filteredRecords = computed(() => {
  const query = keyword.value.trim().toLowerCase()
  if (!query) return records.value
  return records.value.filter((item) =>
    props.columns.some((column) => String(item[column.prop] ?? '').toLowerCase().includes(query)),
  )
})
const pagedRecords = computed(() => {
  const start = (pageNum.value - 1) * pageSize.value
  return filteredRecords.value.slice(start, start + pageSize.value)
})
const rules = computed(() => Object.fromEntries(
  props.fields.filter((field) => field.required).map((field) => [
    field.prop,
    [{ required: true, message: `请填写${field.label}`, trigger: field.type === 'select' ? 'change' : 'blur' }],
  ]),
))

function persist() {
  localStorage.setItem(props.storageKey, JSON.stringify(records.value))
}

function resetForm(record = {}) {
  props.fields.forEach((field) => {
    form[field.prop] = record[field.prop] ?? field.default ?? ''
  })
}

function openCreate() {
  editingId.value = null
  resetForm()
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

function openEdit(record) {
  editingId.value = record.id
  resetForm(record)
  dialogVisible.value = true
  nextTick(() => formRef.value?.clearValidate())
}

async function submit() {
  await formRef.value.validate()
  const payload = Object.fromEntries(props.fields.map((field) => [field.prop, form[field.prop]]))
  if (editingId.value) {
    const index = records.value.findIndex((item) => item.id === editingId.value)
    records.value[index] = { ...records.value[index], ...payload, updatedAt: new Date().toLocaleDateString() }
    ElMessage.success(`${props.resourceName}已更新`)
  } else {
    records.value.unshift({ id: Date.now(), ...payload, createdAt: new Date().toLocaleDateString() })
    ElMessage.success(`${props.resourceName}已新增`)
  }
  persist()
  dialogVisible.value = false
}

async function remove(record) {
  await ElMessageBox.confirm(`确定删除“${record.name || record.title || record.code}”吗？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确认删除',
    cancelButtonText: '取消',
  })
  records.value = records.value.filter((item) => item.id !== record.id)
  persist()
  ElMessage.success('删除成功')
}

function tagType(column, value) {
  return column.tagMap?.[value]?.type || 'info'
}

function tagLabel(column, value) {
  return column.tagMap?.[value]?.label || value
}
</script>

<template>
  <div class="page-shell">
    <PageHeader :title="title" :description="description">
      <el-button type="primary" :icon="Plus" @click="openCreate">新增{{ resourceName }}</el-button>
    </PageHeader>

    <section class="surface-card table-card">
      <div class="table-toolbar">
        <el-input
          v-model="keyword"
          :placeholder="searchPlaceholder"
          :prefix-icon="Search"
          clearable
          class="search-input"
          @input="pageNum = 1"
        />
        <div class="table-toolbar__meta">共 {{ filteredRecords.length }} 条记录</div>
      </div>

      <el-table :data="pagedRecords" stripe>
        <el-table-column type="index" label="#" width="58" />
        <el-table-column
          v-for="column in columns"
          :key="column.prop"
          :prop="column.prop"
          :label="column.label"
          :min-width="column.minWidth || 110"
          :width="column.width"
          show-overflow-tooltip
        >
          <template v-if="column.type === 'tag'" #default="scope">
            <el-tag :type="tagType(column, scope.row[column.prop])" effect="light" round>
              {{ tagLabel(column, scope.row[column.prop]) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button link type="primary" @click="openEdit(scope.row)">编辑</el-button>
            <el-button link type="danger" @click="remove(scope.row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无匹配数据" />
        </template>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          layout="total, prev, pager, next"
          :total="filteredRecords.length"
          background
        />
      </div>
    </section>

    <el-dialog
      v-model="dialogVisible"
      :title="`${editingId ? '编辑' : '新增'}${resourceName}`"
      width="560px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-position="top">
        <div class="form-grid">
          <el-form-item
            v-for="field in fields"
            :key="field.prop"
            :label="field.label"
            :prop="field.prop"
            :class="{ 'is-full': field.full }"
          >
            <el-select v-if="field.type === 'select'" v-model="form[field.prop]" placeholder="请选择" style="width: 100%">
              <el-option v-for="option in field.options" :key="option.value" :label="option.label" :value="option.value" />
            </el-select>
            <el-input-number
              v-else-if="field.type === 'number'"
              v-model="form[field.prop]"
              :min="field.min || 0"
              :max="field.max || 99999"
              controls-position="right"
              style="width: 100%"
            />
            <el-date-picker
              v-else-if="field.type === 'date'"
              v-model="form[field.prop]"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
            <el-input
              v-else
              v-model="form[field.prop]"
              :type="field.type === 'textarea' ? 'textarea' : 'text'"
              :rows="field.rows || 3"
              :placeholder="field.placeholder || `请输入${field.label}`"
            />
          </el-form-item>
        </div>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
