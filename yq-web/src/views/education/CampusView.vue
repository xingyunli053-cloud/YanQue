<script setup>
import LocalCrudPage from '../../components/LocalCrudPage.vue'

const columns = [
  { prop: 'code', label: '校区编码', width: 110 },
  { prop: 'name', label: '校区名称', minWidth: 150 },
  { prop: 'address', label: '校区地址', minWidth: 220 },
  { prop: 'principal', label: '负责人', width: 105 },
  { prop: 'phone', label: '联系电话', width: 135 },
  { prop: 'status', label: '状态', width: 90, type: 'tag', tagMap: { ACTIVE: { label: '运营中', type: 'success' }, INACTIVE: { label: '已停用', type: 'info' } } },
]
const fields = [
  { prop: 'code', label: '校区编码', required: true },
  { prop: 'name', label: '校区名称', required: true },
  { prop: 'principal', label: '负责人', required: true },
  { prop: 'phone', label: '联系电话', required: true },
  { prop: 'address', label: '详细地址', required: true, full: true },
  { prop: 'status', label: '运营状态', type: 'select', required: true, default: 'ACTIVE', options: [{ label: '运营中', value: 'ACTIVE' }, { label: '已停用', value: 'INACTIVE' }] },
]
const seedData = [
  { id: 1, code: 'BJ-001', name: '滨江校区', address: '滨江区江虹路 1750 号', principal: '林晓雨', phone: '138****6721', status: 'ACTIVE', createdAt: '2026/03/12' },
  { id: 2, code: 'CX-002', name: '城西校区', address: '西湖区文二西路 188 号', principal: '周远航', phone: '136****8392', status: 'ACTIVE', createdAt: '2026/04/08' },
  { id: 3, code: 'WL-003', name: '未来城校区', address: '余杭区景兴路 999 号', principal: '何云舒', phone: '159****1058', status: 'ACTIVE', createdAt: '2026/05/20' },
  { id: 4, code: 'HP-004', name: '湖畔校区', address: '拱墅区祥园路 108 号', principal: '陈嘉木', phone: '137****2860', status: 'ACTIVE', createdAt: '2026/06/02' },
]
</script>

<template>
  <LocalCrudPage title="校区管理" description="维护教学网点、负责人及日常运营状态。" resource-name="校区" storage-key="yanque_demo_campuses" :seed-data="seedData" :columns="columns" :fields="fields" search-placeholder="搜索校区名称、编码或负责人" />
</template>
