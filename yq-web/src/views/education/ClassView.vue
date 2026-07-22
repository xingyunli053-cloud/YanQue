<script setup>
import LocalCrudPage from '../../components/LocalCrudPage.vue'

const columns = [
  { prop: 'code', label: '班级编码', width: 115 },
  { prop: 'name', label: '班级名称', minWidth: 145 },
  { prop: 'courseName', label: '课程', minWidth: 160 },
  { prop: 'campus', label: '校区', width: 120 },
  { prop: 'teacher', label: '授课老师', width: 105 },
  { prop: 'startDate', label: '开班日期', width: 115 },
  { prop: 'capacity', label: '班额', width: 75 },
  { prop: 'status', label: '状态', width: 90, type: 'tag', tagMap: { RUNNING: { label: '进行中', type: 'success' }, PENDING: { label: '待开班', type: 'warning' }, FINISHED: { label: '已结课', type: 'info' } } },
]
const fields = [
  { prop: 'code', label: '班级编码', required: true },
  { prop: 'name', label: '班级名称', required: true },
  { prop: 'courseName', label: '关联课程', type: 'select', required: true, options: [{ label: '少儿创意美术', value: '少儿创意美术' }, { label: '趣味编程基础', value: '趣味编程基础' }, { label: '英语自然拼读', value: '英语自然拼读' }, { label: '青少年机器人', value: '青少年机器人' }] },
  { prop: 'campus', label: '开课校区', type: 'select', required: true, options: [{ label: '滨江校区', value: '滨江校区' }, { label: '城西校区', value: '城西校区' }, { label: '未来城校区', value: '未来城校区' }, { label: '湖畔校区', value: '湖畔校区' }] },
  { prop: 'teacher', label: '授课老师', required: true },
  { prop: 'startDate', label: '开班日期', type: 'date', required: true },
  { prop: 'capacity', label: '班级容量', type: 'number', min: 1, max: 100, default: 20, required: true },
  { prop: 'status', label: '班级状态', type: 'select', default: 'PENDING', required: true, options: [{ label: '待开班', value: 'PENDING' }, { label: '进行中', value: 'RUNNING' }, { label: '已结课', value: 'FINISHED' }] },
]
const seedData = [
  { id: 1, code: 'BJ-ART-A01', name: '春芽 A 班', courseName: '少儿创意美术', campus: '滨江校区', teacher: '周老师', startDate: '2026-06-10', capacity: 20, status: 'RUNNING' },
  { id: 2, code: 'CX-TEC-B02', name: '启航 2 班', courseName: '趣味编程基础', campus: '城西校区', teacher: '陈老师', startDate: '2026-07-01', capacity: 18, status: 'RUNNING' },
  { id: 3, code: 'BJ-LAN-B03', name: '向阳 B 班', courseName: '英语自然拼读', campus: '滨江校区', teacher: '林老师', startDate: '2026-07-15', capacity: 22, status: 'RUNNING' },
  { id: 4, code: 'WL-TEC-C01', name: '探索者班', courseName: '青少年机器人', campus: '未来城校区', teacher: '何老师', startDate: '2026-08-03', capacity: 16, status: 'PENDING' },
]
</script>

<template>
  <LocalCrudPage title="班级管理" description="管理课程开班、授课教师、班额和教学进度。" resource-name="班级" storage-key="yanque_demo_classes" :seed-data="seedData" :columns="columns" :fields="fields" search-placeholder="搜索班级、课程、校区或教师" />
</template>
