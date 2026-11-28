
<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const courses = ref([])
const loading = ref(false)
const errorMessage = ref('')

const days = [
  { value: 1, label: '星期一', short: '周一' },
  { value: 2, label: '星期二', short: '周二' },
  { value: 3, label: '星期三', short: '周三' },
  { value: 4, label: '星期四', short: '周四' },
  { value: 5, label: '星期五', short: '周五' },
  { value: 6, label: '星期六', short: '周六' },
  { value: 7, label: '星期日', short: '周日' }
]

const getHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`
})

// 获取当前学生的个人课表
const loadSchedule = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const response = await axios.get(
        'http://localhost:8080/enrollment/my/schedule',
        { headers: getHeaders() }
    )

    if (response.data.code === 200) {
      courses.value = response.data.data || []
    } else {
      errorMessage.value = response.data.message || '获取课表失败'
    }
  } catch (error) {
    errorMessage.value = error.response?.data?.message || '获取课表失败'
  } finally {
    loading.value = false
  }
}

// 星期数字转换
const getDayName = (day) => {
  return days.find(item => item.value === day)?.label || '未知'
}

// 按星期分组课程，并按开始节次排序
const getCoursesByDay = (day) => {
  return courses.value
      .filter(course => Number(course.classDay) === day)
      .sort((a, b) => Number(a.startSection) - Number(b.startSection))
}

// 课程总数
const totalCourses = computed(() => courses.value.length)

// 总学分
const totalCredits = computed(() => {
  return courses.value.reduce(
      (sum, course) => sum + (Number(course.credit) || 0),
      0
  )
})

// 未识别星期的课程
const unknownDayCourses = computed(() =>
    courses.value.filter(course =>
        !days.some(day => day.value === Number(course.classDay))
    )
)

onMounted(loadSchedule)
</script>

<template>
  <div class="schedule-page">
    <!-- 页面标题 -->
    <section class="page-heading">
      <div>
        <div class="heading-tag">
          <span class="tag-dot"></span>
          MY CLASS SCHEDULE
        </div>
        <h2>我的个人课表</h2>
        <p>合理安排每一天的学习时间，让学习更加高效</p>
      </div>

      <div class="heading-icon">📅</div>
    </section>

    <!-- 统计卡片 -->
    <section class="statistics">
      <div class="stat-card">
        <div class="stat-icon blue">📚</div>
        <div>
          <span>已选课程</span>
          <strong>{{ totalCourses }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon green">🎓</div>
        <div>
          <span>总学分</span>
          <strong>{{ totalCredits }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orange">📆</div>
        <div>
          <span>有课日期</span>
          <strong>
            {{ days.filter(day => getCoursesByDay(day.value).length > 0).length }}
          </strong>
        </div>
      </div>
    </section>

    <!-- 课表主体 -->
    <section class="content-card">
      <div class="section-heading">
        <div>
          <h3>每周课程安排</h3>
          <p>按照星期查看你的课程和上课节次</p>
        </div>

        <button
            class="refresh-button"
            @click="loadSchedule"
            :disabled="loading"
        >
          <span>↻</span>
          {{ loading ? '加载中...' : '刷新课表' }}
        </button>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="state-container">
        <div class="loading-spinner"></div>
        <span>正在加载课表...</span>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="errorMessage" class="state-container error-state">
        <div class="state-icon">!</div>
        <strong>课表加载失败</strong>
        <span>{{ errorMessage }}</span>
        <button class="retry-button" @click="loadSchedule">
          重新加载
        </button>
      </div>

      <!-- 空课表 -->
      <div v-else-if="courses.length === 0" class="state-container">
        <div class="empty-icon">📚</div>
        <strong>当前暂无已选课程</strong>
        <span>完成选课后，你的课程安排会显示在这里</span>
      </div>

      <!-- 一周课表 -->
      <div v-else>
        <div class="week-wrapper">
          <div class="week-grid">
            <div
                v-for="day in days"
                :key="day.value"
                class="day-column"
            >
              <div class="day-header">
                <strong>{{ day.short }}</strong>
                <span>{{ getCoursesByDay(day.value).length }} 门课程</span>
              </div>

              <div class="day-content">
                <div
                    v-for="course in getCoursesByDay(day.value)"
                    :key="course.id"
                    class="course-card"
                >
                  <div class="course-card-top">
                    <span class="course-dot"></span>
                    <span class="course-credit">
                      {{ course.credit }} 学分
                    </span>
                  </div>

                  <strong class="course-title">
                    {{ course.courseName }}
                  </strong>

                  <div class="course-teacher">
                    <span>👤</span>
                    {{ course.teacher }}
                  </div>

                  <div class="course-sections">
                    第 {{ course.startSection }} - {{ course.endSection }} 节
                  </div>
                </div>

                <div
                    v-if="getCoursesByDay(day.value).length === 0"
                    class="no-course"
                >
                  <span>—</span>
                  <small>暂无课程</small>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 未识别星期的课程 -->
        <div v-if="unknownDayCourses.length > 0" class="unknown-courses">
          <h4>其他课程</h4>
          <div
              v-for="course in unknownDayCourses"
              :key="course.id"
              class="unknown-course-item"
          >
            <strong>{{ course.courseName }}</strong>
            <span>{{ getDayName(course.classDay) }}</span>
            <span>{{ course.teacher }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 课程明细 -->
    <section v-if="!loading && !errorMessage && courses.length > 0" class="content-card detail-card">
      <div class="section-heading">
        <div>
          <h3>课程明细</h3>
          <p>查看已选课程的完整信息</p>
        </div>
        <span class="record-count">{{ courses.length }} 门课程</span>
      </div>

      <div class="table-wrapper">
        <table class="schedule-table">
          <thead>
          <tr>
            <th>课程名称</th>
            <th>任课教师</th>
            <th>学分</th>
            <th>上课星期</th>
            <th>开始节次</th>
            <th>结束节次</th>
          </tr>
          </thead>

          <tbody>
          <tr v-for="course in courses" :key="course.id">
            <td class="course-name-cell">
              {{ course.courseName }}
            </td>
            <td>{{ course.teacher }}</td>
            <td>
              <span class="credit-tag">{{ course.credit }} 学分</span>
            </td>
            <td>{{ getDayName(course.classDay) }}</td>
            <td>第 {{ course.startSection }} 节</td>
            <td>第 {{ course.endSection }} 节</td>
          </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.schedule-page {
  width: 100%;
  padding-bottom: 24px;
  color: #263f5e;
}

.page-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 26px 30px;
  margin-bottom: 22px;
  border: 1px solid #e3edfc;
  border-radius: 18px;
  background: linear-gradient(115deg, #eff6ff, #f5f8ff 60%, #e9f2ff);
}

.heading-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #3975c9;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 1.4px;
}

.tag-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #4388e8;
}

.page-heading h2 {
  margin: 12px 0 7px;
  color: #19365f;
  font-size: 25px;
  font-weight: 750;
}

.page-heading p {
  margin: 0;
  color: #7b8da8;
  font-size: 13px;
}

.heading-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 66px;
  height: 66px;
  border-radius: 19px;
  background: rgba(255, 255, 255, 0.8);
  box-shadow: 0 8px 24px rgba(54, 105, 177, 0.1);
  font-size: 30px;
}

.statistics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
  margin-bottom: 22px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  border: 1px solid #edf1f7;
  border-radius: 15px;
  background: #fff;
  box-shadow: 0 4px 15px rgba(30, 60, 100, 0.035);
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  flex-shrink: 0;
  border-radius: 13px;
  font-size: 21px;
}

.stat-icon.blue {
  color: #3479d3;
  background: #eaf3ff;
}

.stat-icon.green {
  color: #19a477;
  background: #e8f8f1;
}

.stat-icon.orange {
  color: #df9a38;
  background: #fff5e6;
}

.stat-card span {
  display: block;
  margin-bottom: 6px;
  color: #8a99ad;
  font-size: 12px;
}

.stat-card strong {
  color: #263f63;
  font-size: 23px;
}

.content-card {
  padding: 23px;
  margin-bottom: 22px;
  border: 1px solid #edf1f7;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 4px 18px rgba(30, 60, 100, 0.035);
}

.detail-card {
  margin-bottom: 0;
}

.section-heading {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 20px;
}

.section-heading h3 {
  margin: 0 0 7px;
  color: #263f63;
  font-size: 17px;
  font-weight: 750;
}

.section-heading p {
  margin: 0;
  color: #96a2b3;
  font-size: 12px;
}

.refresh-button {
  flex-shrink: 0;
  padding: 9px 15px;
  color: #3979ca;
  background: #eef5ff;
  border: 1px solid #e0ecfc;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
}

.refresh-button span {
  margin-right: 5px;
  font-size: 16px;
}

.refresh-button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.week-wrapper {
  width: 100%;
  overflow-x: auto;
  padding-bottom: 8px;
}

.week-grid {
  display: grid;
  grid-template-columns: repeat(7, minmax(125px, 1fr));
  gap: 10px;
  min-width: 960px;
}

.day-column {
  min-height: 245px;
  overflow: hidden;
  border: 1px solid #e9eff7;
  border-radius: 11px;
  background: #fbfcfe;
}

.day-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  padding: 13px 6px;
  background: #f0f6ff;
  border-bottom: 1px solid #e5edf8;
}

.day-header strong {
  color: #376da9;
  font-size: 13px;
}

.day-header span {
  color: #91a4bc;
  font-size: 10px;
}

.day-content {
  display: flex;
  flex-direction: column;
  gap: 9px;
  padding: 10px;
}

.course-card {
  padding: 11px 9px;
  border: 1px solid #e5eefb;
  border-left: 3px solid #5b9ce8;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(50, 90, 140, 0.04);
}

.course-card-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 5px;
  margin-bottom: 9px;
}

.course-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #5797e4;
}

.course-credit {
  color: #4382c9;
  font-size: 10px;
}

.course-title {
  display: block;
  margin-bottom: 9px;
  color: #304865;
  font-size: 12px;
  line-height: 1.5;
  overflow-wrap: anywhere;
}

.course-teacher {
  margin-bottom: 8px;
  color: #8999ad;
  font-size: 10px;
  overflow-wrap: anywhere;
}

.course-sections {
  padding: 6px 5px;
  color: #3979ca;
  background: #edf5ff;
  border-radius: 5px;
  text-align: center;
  font-size: 10px;
}

.no-course {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 130px;
  color: #c0cad6;
}

.no-course span {
  font-size: 20px;
}

.no-course small {
  font-size: 10px;
}

.unknown-courses {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #f0e8d9;
  border-radius: 10px;
  background: #fffcf5;
}

.unknown-courses h4 {
  margin: 0 0 12px;
  color: #92713c;
  font-size: 13px;
}

.unknown-course-item {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  padding: 9px 0;
  border-top: 1px solid #f4eddf;
  color: #8d806b;
  font-size: 12px;
}

.unknown-course-item strong {
  color: #66543a;
}

.record-count {
  padding: 7px 11px;
  color: #3979ca;
  background: #edf5ff;
  border-radius: 7px;
  font-size: 11px;
}

.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

.schedule-table {
  width: 100%;
  min-width: 620px;
  border-collapse: separate;
  border-spacing: 0;
  text-align: left;
}

.schedule-table th {
  padding: 14px 13px;
  color: #8291a7;
  background: #f7f9fc;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.schedule-table th:first-child {
  border-radius: 9px 0 0 9px;
}

.schedule-table th:last-child {
  border-radius: 0 9px 9px 0;
}

.schedule-table td {
  padding: 15px 13px;
  color: #586b85;
  border-bottom: 1px solid #f0f3f7;
  font-size: 13px;
  white-space: nowrap;
}

.schedule-table tbody tr:hover {
  background: #f9fbff;
}

.course-name-cell {
  color: #304865 !important;
  font-weight: 600;
}

.credit-tag {
  padding: 5px 9px;
  color: #3677c8;
  background: #edf5ff;
  border-radius: 6px;
  font-size: 11px;
}

.state-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 230px;
  padding: 30px 15px;
  color: #9aa7b8;
  font-size: 12px;
  text-align: center;
}

.state-container strong {
  margin-bottom: 8px;
  color: #60748e;
  font-size: 14px;
}

.state-container > span {
  line-height: 1.6;
}

.loading-spinner {
  width: 28px;
  height: 28px;
  margin-bottom: 13px;
  border: 3px solid #e5eef9;
  border-top-color: #4388df;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.empty-icon {
  margin-bottom: 12px;
  font-size: 36px;
  opacity: 0.7;
}

.error-state .state-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  margin-bottom: 12px;
  border-radius: 50%;
  color: #d65e5e;
  background: #fff0f0;
  font-size: 20px;
  font-weight: 700;
}

.error-state strong {
  color: #bd5555;
}

.retry-button {
  margin-top: 15px;
  padding: 8px 15px;
  color: #3979ca;
  background: #eef5ff;
  border: none;
  border-radius: 7px;
  cursor: pointer;
  font-size: 12px;
}

@media (max-width: 900px) {
  .statistics {
    gap: 12px;
  }

  .stat-card {
    padding: 15px;
  }

  .content-card {
    padding: 18px;
  }
}

@media (max-width: 600px) {
  .page-heading {
    padding: 21px 18px;
  }

  .page-heading h2 {
    font-size: 21px;
  }

  .heading-icon {
    width: 52px;
    height: 52px;
    font-size: 24px;
  }

  .statistics {
    grid-template-columns: 1fr;
    gap: 10px;
  }

  .stat-card {
    padding: 13px 16px;
  }

  .stat-icon {
    width: 40px;
    height: 40px;
  }

  .stat-card strong {
    font-size: 20px;
  }

  .content-card {
    padding: 15px 12px;
    border-radius: 12px;
  }

  .section-heading {
    align-items: flex-start;
  }

  .week-grid {
    min-width: 960px;
  }
}
</style>
