<script setup>
import LocalCrudPage from '../../components/LocalCrudPage.vue'

const columns = [
  { prop: 'code', label: '课程编码', width: 110 },
  { prop: 'name', label: '课程名称', minWidth: 170 },
  { prop: 'category', label: '课程分类', width: 110, type: 'tag', tagMap: { ART: { label: '艺术素养', type: 'warning' }, TECH: { label: '科技创新', type: 'primary' }, LANGUAGE: { label: '语言能力', type: 'success' } } },
  { prop: 'campus', label: '适用校区', minWidth: 130 },
  { prop: 'hours', label: '总课时', width: 90 },
  { prop: 'price', label: '课程价格', width: 110 },
  { prop: 'status', label: '状态', width: 90, type: 'tag', tagMap: { ON: { label: '已上架', type: 'success' }, OFF: { label: '已下架', type: 'info' } } },
]
const fields = [
  { prop: 'code', label: '课程编码', required: true },
  { prop: 'name', label: '课程名称', required: true },
  { prop: 'category', label: '课程分类', type: 'select', required: true, options: [{ label: '艺术素养', value: 'ART' }, { label: '科技创新', value: 'TECH' }, { label: '语言能力', value: 'LANGUAGE' }] },
  { prop: 'campus', label: '适用校区', required: true },
  { prop: 'hours', label: '总课时', type: 'number', min: 1, default: 24, required: true },
  { prop: 'price', label: '课程价格（元）', type: 'number', min: 0, default: 2999, required: true },
  { prop: 'status', label: '上架状态', type: 'select', default: 'ON', required: true, options: [{ label: '已上架', value: 'ON' }, { label: '已下架', value: 'OFF' }] },
]
const seedData = [
  { id: 1, code: 'ART-101', name: '少儿创意美术', category: 'ART', campus: '全部校区', hours: 32, price: 3680, status: 'ON' },
  { id: 2, code: 'TEC-202', name: '趣味编程基础', category: 'TECH', campus: '滨江、城西', hours: 24, price: 4280, status: 'ON' },
  { id: 3, code: 'LAN-106', name: '英语自然拼读', category: 'LANGUAGE', campus: '全部校区', hours: 36, price: 3980, status: 'ON' },
  { id: 4, code: 'TEC-305', name: '青少年机器人', category: 'TECH', campus: '未来城校区', hours: 40, price: 5980, status: 'ON' },
  { id: 5, code: 'ART-209', name: '儿童戏剧启蒙', category: 'ART', campus: '湖畔校区', hours: 20, price: 2680, status: 'OFF' },
]
</script>

<template>
  <LocalCrudPage title="课程管理" description="统一管理课程定价、课时、适用校区和上架状态。" resource-name="课程" storage-key="yanque_demo_courses" :seed-data="seedData" :columns="columns" :fields="fields" search-placeholder="搜索课程名称、编码或校区" />
</template>
