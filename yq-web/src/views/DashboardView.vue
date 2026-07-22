<script setup>
import { Calendar } from '@element-plus/icons-vue'
import PageHeader from '../components/PageHeader.vue'
import StatCard from '../components/StatCard.vue'

const stats = [
  { label: '在营校区', value: 6, hint: '本月新增 1 个', icon: 'OfficeBuilding', tone: 'green' },
  { label: '上架课程', value: 28, hint: '8 个课程系列', icon: 'Reading', tone: 'blue' },
  { label: '进行中班级', value: 16, hint: '今日 9 节课', icon: 'School', tone: 'orange' },
  { label: '在读学员', value: 486, hint: '较上月 +12.8%', icon: 'UserFilled', tone: 'purple' },
]

const schedules = [
  { time: '09:00', course: '少儿创意美术', className: '春芽 A 班', campus: '滨江校区', teacher: '周老师', status: '授课中' },
  { time: '10:30', course: '趣味编程基础', className: '启航 2 班', campus: '城西校区', teacher: '陈老师', status: '待开课' },
  { time: '14:00', course: '英语自然拼读', className: '向阳 B 班', campus: '滨江校区', teacher: '林老师', status: '待开课' },
  { time: '16:20', course: '青少年机器人', className: '探索者班', campus: '未来城校区', teacher: '何老师', status: '待开课' },
]

const campusProgress = [
  { name: '滨江校区', value: 88, count: 168 },
  { name: '城西校区', value: 73, count: 126 },
  { name: '未来城校区', value: 64, count: 98 },
  { name: '湖畔校区', value: 51, count: 94 },
]
</script>

<template>
  <div class="page-shell dashboard-page">
    <PageHeader
      eyebrow="今日教学脉搏"
      title="上午好，管理员"
      description="今天有 9 节课程正在四个校区有序进行。"
    >
      <el-button :icon="Calendar">2026 年 7 月 22 日</el-button>
    </PageHeader>

    <div class="stats-grid">
      <StatCard v-for="item in stats" :key="item.label" v-bind="item" />
    </div>

    <div class="dashboard-grid">
      <section class="surface-card schedule-card">
        <div class="card-title-row">
          <div><span>今日课程</span><h3>教学日程</h3></div>
          <el-button link type="primary">查看全部</el-button>
        </div>
        <div class="schedule-list">
          <div v-for="item in schedules" :key="item.time + item.className" class="schedule-item">
            <div class="schedule-time">{{ item.time }}</div>
            <div class="schedule-line"><span></span></div>
            <div class="schedule-main"><strong>{{ item.course }}</strong><span>{{ item.className }} · {{ item.campus }}</span></div>
            <div class="schedule-teacher">{{ item.teacher }}</div>
            <el-tag :type="item.status === '授课中' ? 'success' : 'info'" effect="light" round>{{ item.status }}</el-tag>
          </div>
        </div>
      </section>

      <section class="surface-card campus-card">
        <div class="card-title-row">
          <div><span>校区表现</span><h3>学员分布</h3></div>
          <el-icon class="card-menu"><MoreFilled /></el-icon>
        </div>
        <div class="campus-progress-list">
          <div v-for="campus in campusProgress" :key="campus.name" class="campus-progress">
            <div><strong>{{ campus.name }}</strong><span>{{ campus.count }} 人</span></div>
            <el-progress :percentage="campus.value" :show-text="false" :stroke-width="9" />
          </div>
        </div>
        <div class="campus-summary">
          <div><span>本月到课率</span><strong>94.6%</strong></div>
          <div><span>班级满班率</span><strong>81.2%</strong></div>
        </div>
      </section>
    </div>
  </div>
</template>
