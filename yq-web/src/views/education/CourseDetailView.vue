<script setup>
import LocalCrudPage from '../../components/LocalCrudPage.vue'

const columns = [
  { prop: 'courseName', label: '所属课程', minWidth: 170 },
  { prop: 'chapterNo', label: '章节', width: 80 },
  { prop: 'title', label: '内容标题', minWidth: 190 },
  { prop: 'contentType', label: '内容类型', width: 110, type: 'tag', tagMap: { VIDEO: { label: '视频', type: 'primary' }, TEXT: { label: '图文', type: 'success' }, PRACTICE: { label: '实践', type: 'warning' } } },
  { prop: 'duration', label: '时长/分钟', width: 105 },
  { prop: 'status', label: '状态', width: 90, type: 'tag', tagMap: { READY: { label: '已发布', type: 'success' }, DRAFT: { label: '草稿', type: 'info' } } },
]
const fields = [
  { prop: 'courseName', label: '所属课程', type: 'select', required: true, options: [{ label: '少儿创意美术', value: '少儿创意美术' }, { label: '趣味编程基础', value: '趣味编程基础' }, { label: '英语自然拼读', value: '英语自然拼读' }, { label: '青少年机器人', value: '青少年机器人' }] },
  { prop: 'chapterNo', label: '章节序号', type: 'number', min: 1, default: 1, required: true },
  { prop: 'title', label: '内容标题', required: true, full: true },
  { prop: 'contentType', label: '内容类型', type: 'select', required: true, options: [{ label: '视频', value: 'VIDEO' }, { label: '图文', value: 'TEXT' }, { label: '实践活动', value: 'PRACTICE' }] },
  { prop: 'duration', label: '预计时长（分钟）', type: 'number', min: 1, default: 45, required: true },
  { prop: 'summary', label: '内容摘要', type: 'textarea', rows: 4, full: true },
  { prop: 'status', label: '发布状态', type: 'select', default: 'DRAFT', required: true, options: [{ label: '已发布', value: 'READY' }, { label: '草稿', value: 'DRAFT' }] },
]
const seedData = [
  { id: 1, courseName: '少儿创意美术', chapterNo: 1, title: '认识色彩与情绪', contentType: 'VIDEO', duration: 35, summary: '通过生活中的色彩建立基础感知。', status: 'READY' },
  { id: 2, courseName: '少儿创意美术', chapterNo: 2, title: '我的第一幅拼贴画', contentType: 'PRACTICE', duration: 50, summary: '使用多种材料完成主题拼贴。', status: 'READY' },
  { id: 3, courseName: '趣味编程基础', chapterNo: 1, title: '程序和指令是什么', contentType: 'TEXT', duration: 30, summary: '理解顺序执行与基础指令。', status: 'READY' },
  { id: 4, courseName: '青少年机器人', chapterNo: 3, title: '传感器挑战任务', contentType: 'PRACTICE', duration: 60, summary: '搭建传感器并完成循迹挑战。', status: 'DRAFT' },
]
</script>

<template>
  <LocalCrudPage title="课程详情管理" description="编排章节内容、学习材料和实践任务。" resource-name="课程内容" storage-key="yanque_demo_course_details" :seed-data="seedData" :columns="columns" :fields="fields" search-placeholder="搜索课程或内容标题" />
</template>
