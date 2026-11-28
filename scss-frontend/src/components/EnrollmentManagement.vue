
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const courses = ref([])
const myEnrollments = ref([])
const loading = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const getHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`
})

// 查询课程和我的选课记录
const loadData = async () => {
  loading.value = true

  try {
    const [courseResponse, enrollmentResponse] = await Promise.all([
      axios.get('http://localhost:8080/course/page', {
        params: {
          pageNum: pageNum.value,
          pageSize: pageSize.value
        },
        headers: getHeaders()
      }),
      axios.get('http://localhost:8080/enrollment/my', {
        headers: getHeaders()
      })
    ])

    if (courseResponse.data.code === 200) {
      courses.value = courseResponse.data.data.records
      total.value = courseResponse.data.data.total
    }

    if (enrollmentResponse.data.code === 200) {
      myEnrollments.value = enrollmentResponse.data.data
    }
  } catch (error) {
    alert(error.response?.data?.message || '获取数据失败')
  } finally {
    loading.value = false
  }
}

// 判断当前课程是否已选
const isSelected = (courseId) => {
  return myEnrollments.value.some(
      item => item.courseId === courseId && item.status === 1
  )
}

// 判断课程是否已满
const isFull = (course) => {
  return course.selectedCount >= course.capacity
}

// 选课
const selectCourse = async (courseId) => {
  try {
    const response = await axios.post(
        `http://localhost:8080/enrollment/select/${courseId}`,
        {},
        { headers: getHeaders() }
    )

    if (response.data.code === 200) {
      alert('选课成功')
      await loadData()
    } else {
      alert(response.data.message || '选课失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '选课失败')
  }
}

// 退课
const dropCourse = async (courseId) => {
  if (!confirm('确定要退选这门课程吗？')) return

  try {
    const response = await axios.delete(
        `http://localhost:8080/enrollment/drop/${courseId}`,
        { headers: getHeaders() }
    )

    if (response.data.code === 200) {
      alert('退课成功')
      await loadData()
    } else {
      alert(response.data.message || '退课失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '退课失败')
  }
}

// 分页
const previousPage = () => {
  if (pageNum.value > 1) {
    pageNum.value--
    loadData()
  }
}

const nextPage = () => {
  if (pageNum.value * pageSize.value < total.value) {
    pageNum.value++
    loadData()
  }
}

const formatTime = (time) => {
  if (!time) return '--'
  return String(time).replace('T', ' ').slice(0, 16)
}

onMounted(loadData)
</script>

<template>
  <div class="enrollment-page">
    <!-- 页面标题 -->
    <section class="page-heading">
      <div>
        <div class="heading-tag">
          <span class="tag-dot"></span>
          COURSE CENTER
        </div>
        <h2>学生选课</h2>
        <p>探索感兴趣的课程，合理安排你的学习计划</p>
      </div>

      <div class="heading-icon">
        <span>📚</span>
      </div>
    </section>

    <!-- 统计信息 -->
    <section class="statistics">
      <div class="stat-card">
        <div class="stat-icon blue">📖</div>
        <div>
          <span class="stat-label">当前课程总数</span>
          <strong>{{ total }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon green">✓</div>
        <div>
          <span class="stat-label">已选课程</span>
          <strong>
            {{ myEnrollments.filter(item => item.status === 1).length }}
          </strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orange">📝</div>
        <div>
          <span class="stat-label">选课记录</span>
          <strong>{{ myEnrollments.length }}</strong>
        </div>
      </div>
    </section>

    <!-- 课程列表 -->
    <section class="content-card">
      <div class="section-heading">
        <div>
          <h3>可选课程</h3>
          <p>选择适合自己的课程，开启新的学习旅程</p>
        </div>
        <button class="refresh-button" @click="loadData" :disabled="loading">
          <span>↻</span> 刷新
        </button>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>正在加载课程数据...</span>
      </div>

      <div v-else-if="courses.length === 0" class="empty-state">
        <div class="empty-icon">📚</div>
        <strong>暂时没有课程</strong>
        <span>当前没有可展示的课程数据</span>
      </div>

      <div v-else class="table-wrapper">
        <table class="course-table">
          <thead>
          <tr>
            <th>课程信息</th>
            <th>授课教师</th>
            <th>学分</th>
            <th>课程容量</th>
            <th>上课时间</th>
            <th>选课操作</th>
          </tr>
          </thead>

          <tbody>
          <tr v-for="course in courses" :key="course.id">
            <td>
              <div class="course-name">
                <div class="course-avatar">
                  {{ course.courseName?.charAt(0) || '课' }}
                </div>
                <div>
                  <strong>{{ course.courseName }}</strong>
                  <span>课程编号：{{ course.id }}</span>
                </div>
              </div>
            </td>

            <td>
              <span class="teacher-name">{{ course.teacher }}</span>
            </td>

            <td>
              <span class="credit-tag">{{ course.credit }} 学分</span>
            </td>

            <td>
              <div class="capacity-info">
                <span>{{ course.selectedCount }} / {{ course.capacity }}</span>
                <div class="capacity-bar">
                  <div
                      class="capacity-progress"
                      :class="{ full: isFull(course) }"
                      :style="{
                        width: course.capacity
                          ? Math.min(course.selectedCount / course.capacity * 100, 100) + '%'
                          : '0%'
                      }"
                  ></div>
                </div>
              </div>
            </td>

            <td>
              <div class="class-time">
                <span>星期 {{ course.classDay }}</span>
                <small>
                  第 {{ course.startSection }} - {{ course.endSection }} 节
                </small>
              </div>
            </td>

            <td>
              <button
                  v-if="isSelected(course.id)"
                  class="action-button drop-button"
                  @click="dropCourse(course.id)"
              >
                退选课程
              </button>

              <button
                  v-else
                  class="action-button select-button"
                  :disabled="isFull(course)"
                  @click="selectCourse(course.id)"
              >
                {{ isFull(course) ? '课程已满' : '立即选课' }}
              </button>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <span class="page-info">
          共 {{ total }} 门课程，当前第 {{ pageNum }} 页
        </span>

        <div class="page-controls">
          <button
              class="page-button"
              :disabled="pageNum <= 1 || loading"
              @click="previousPage"
          >
            上一页
          </button>

          <span class="current-page">{{ pageNum }}</span>

          <button
              class="page-button"
              :disabled="pageNum * pageSize >= total || loading"
              @click="nextPage"
          >
            下一页
          </button>
        </div>
      </div>
    </section>

    <!-- 我的选课记录 -->
    <section class="content-card records-card">
      <div class="section-heading">
        <div>
          <h3>我的选课记录</h3>
          <p>查看自己的课程选择情况和历史记录</p>
        </div>
        <div class="record-count">
          {{ myEnrollments.length }} 条记录
        </div>
      </div>

      <div v-if="myEnrollments.length === 0" class="empty-state record-empty">
        <div class="empty-icon">📋</div>
        <strong>暂无选课记录</strong>
        <span>选择课程后，记录会显示在这里</span>
      </div>

      <div v-else class="table-wrapper">
        <table class="record-table">
          <thead>
          <tr>
            <th>记录编号</th>
            <th>课程编号</th>
            <th>选课时间</th>
            <th>选课状态</th>
          </tr>
          </thead>

          <tbody>
          <tr v-for="item in myEnrollments" :key="item.id">
            <td>#{{ item.id }}</td>
            <td>{{ item.courseId }}</td>
            <td>{{ formatTime(item.enrollTime) }}</td>
            <td>
                <span
                    class="status-tag"
                    :class="item.status === 1 ? 'status-active' : 'status-dropped'"
                >
                  <span class="status-dot"></span>
                  {{ item.status === 1 ? '已选课' : '已退选' }}
                </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.enrollment-page {
  width: 100%;
  color: #24344d;
  padding-bottom: 24px;
}

.page-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 26px 30px;
  margin-bottom: 22px;
  border-radius: 18px;
  background: linear-gradient(115deg, #eff6ff, #f5f8ff 60%, #e9f2ff);
  border: 1px solid #e3edfc;
}

.heading-tag {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #3975c9;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 1.5px;
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
  width: 66px;
  height: 66px;
  display: flex;
  align-items: center;
  justify-content: center;
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
  background: #fff;
  border: 1px solid #edf1f7;
  border-radius: 15px;
  box-shadow: 0 4px 15px rgba(30, 60, 100, 0.035);
}

.stat-icon {
  width: 46px;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 13px;
  font-size: 21px;
  font-weight: 700;
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

.stat-label {
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
  background: #fff;
  border: 1px solid #edf1f7;
  border-radius: 16px;
  box-shadow: 0 4px 18px rgba(30, 60, 100, 0.035);
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
  padding: 8px 14px;
  color: #3979ca;
  background: #eef5ff;
  border: 1px solid #e0ecfc;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
}

.refresh-button span {
  margin-right: 4px;
  font-size: 16px;
}

.refresh-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

.course-table,
.record-table {
  width: 100%;
  min-width: 760px;
  border-collapse: separate;
  border-spacing: 0;
  text-align: left;
}

.course-table th,
.record-table th {
  padding: 14px 13px;
  color: #8291a7;
  background: #f7f9fc;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.course-table th:first-child,
.record-table th:first-child {
  border-radius: 9px 0 0 9px;
}

.course-table th:last-child,
.record-table th:last-child {
  border-radius: 0 9px 9px 0;
}

.course-table td,
.record-table td {
  padding: 15px 13px;
  color: #586b85;
  border-bottom: 1px solid #f0f3f7;
  font-size: 13px;
  white-space: nowrap;
}

.course-table tbody tr:hover,
.record-table tbody tr:hover {
  background: #f9fbff;
}

.course-name {
  display: flex;
  align-items: center;
  gap: 11px;
}

.course-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: 11px;
  color: #3979ca;
  background: #eaf3ff;
  font-size: 16px;
  font-weight: 700;
}

.course-name strong {
  display: block;
  margin-bottom: 5px;
  color: #304865;
  font-size: 13px;
}

.course-name span {
  color: #a0acbb;
  font-size: 11px;
}

.teacher-name {
  color: #536983;
}

.credit-tag {
  padding: 5px 9px;
  color: #3677c8;
  background: #edf5ff;
  border-radius: 6px;
  font-size: 11px;
}

.capacity-info {
  width: 95px;
}

.capacity-info > span {
  display: block;
  margin-bottom: 6px;
  color: #657993;
  font-size: 11px;
}

.capacity-bar {
  height: 5px;
  overflow: hidden;
  border-radius: 5px;
  background: #edf1f6;
}

.capacity-progress {
  height: 100%;
  border-radius: 5px;
  background: linear-gradient(90deg, #69a7f5, #3479d3);
}

.capacity-progress.full {
  background: #e99b69;
}

.class-time span,
.class-time small {
  display: block;
}

.class-time span {
  color: #536983;
  margin-bottom: 5px;
}

.class-time small {
  color: #9aa7b8;
  font-size: 11px;
}

.action-button {
  min-width: 78px;
  padding: 8px 12px;
  border: none;
  border-radius: 7px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.select-button {
  color: #fff;
  background: linear-gradient(110deg, #438be2, #3471c5);
  box-shadow: 0 3px 8px rgba(54, 119, 201, 0.16);
}

.select-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 5px 12px rgba(54, 119, 201, 0.23);
}

.drop-button {
  color: #d66b6b;
  background: #fff0f0;
}

.drop-button:hover {
  background: #ffe4e4;
}

.action-button:disabled {
  color: #a3acb9;
  background: #f0f2f5;
  box-shadow: none;
  cursor: not-allowed;
  transform: none;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding-top: 19px;
}

.page-info {
  color: #98a5b5;
  font-size: 12px;
}

.page-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-button {
  padding: 8px 13px;
  color: #59718f;
  background: #fff;
  border: 1px solid #e5eaf1;
  border-radius: 7px;
  cursor: pointer;
  font-size: 12px;
}

.page-button:hover:not(:disabled) {
  color: #3479d3;
  border-color: #8db7eb;
}

.page-button:disabled {
  color: #b7c0cb;
  background: #f7f8fa;
  cursor: not-allowed;
}

.current-page {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  color: #fff;
  background: #3979ca;
  border-radius: 7px;
  font-size: 12px;
}

.records-card {
  margin-bottom: 0;
}

.record-count {
  padding: 7px 11px;
  color: #3979ca;
  background: #edf5ff;
  border-radius: 7px;
  font-size: 11px;
}

.record-table {
  min-width: 550px;
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  border-radius: 20px;
  font-size: 11px;
}

.status-active {
  color: #15966e;
  background: #e8f8f1;
}

.status-dropped {
  color: #8995a5;
  background: #f0f2f5;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: currentColor;
}

.loading-state,
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 48px 15px;
  color: #9aa7b8;
  font-size: 12px;
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
  font-size: 34px;
  opacity: 0.7;
}

.empty-state strong {
  margin-bottom: 8px;
  color: #60748e;
  font-size: 14px;
}

.empty-state span {
  color: #a1adbb;
}

.record-empty {
  padding: 35px 15px;
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

  .pagination {
    align-items: flex-start;
    flex-direction: column;
  }

  .page-controls {
    align-self: flex-end;
  }

  .refresh-button {
    flex-shrink: 0;
  }
}
</style>
