
<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const records = ref([])
const loading = ref(false)
const errorMessage = ref('')
const keyword = ref('')

const loadRecords = async () => {
  loading.value = true
  errorMessage.value = ''

  try {
    const token = localStorage.getItem('accessToken')

    const response = await axios.get(
        'http://localhost:8080/enrollment/admin/list',
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )

    if (response.data.code === 200) {
      records.value = response.data.data || []
    } else {
      errorMessage.value = response.data.message || '获取选课记录失败'
    }
  } catch (error) {
    errorMessage.value =
        error.response?.data?.message || '获取选课记录失败，请检查后端服务'
  } finally {
    loading.value = false
  }
}

// 根据关键词筛选记录
const filteredRecords = computed(() => {
  const value = keyword.value.trim().toLowerCase()

  if (!value) return records.value

  return records.value.filter(item => {
    return [
      item.studentName,
      item.studentNo,
      item.courseName,
      item.teacher,
      String(item.id)
    ].some(field => String(field ?? '').toLowerCase().includes(value))
  })
})

// 统计信息
const activeCount = computed(() =>
    records.value.filter(item => item.status === 1).length
)

const droppedCount = computed(() =>
    records.value.filter(item => item.status !== 1).length
)

// 格式化时间
const formatTime = (time) => {
  if (!time) return '--'
  return String(time).replace('T', ' ').slice(0, 19)
}

onMounted(loadRecords)
</script>

<template>
  <div class="admin-enrollment-page">
    <!-- 页面标题 -->
    <section class="page-heading">
      <div>
        <div class="heading-tag">
          <span class="tag-dot"></span>
          ENROLLMENT MANAGEMENT
        </div>
        <h2>选课记录管理</h2>
        <p>查看和管理全校学生的课程选择记录</p>
      </div>

      <div class="heading-icon">📋</div>
    </section>

    <!-- 统计卡片 -->
    <section class="statistics">
      <div class="stat-card">
        <div class="stat-icon blue">📚</div>
        <div>
          <span>选课记录总数</span>
          <strong>{{ records.length }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon green">✓</div>
        <div>
          <span>正在选课</span>
          <strong>{{ activeCount }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orange">↩</div>
        <div>
          <span>已退课记录</span>
          <strong>{{ droppedCount }}</strong>
        </div>
      </div>
    </section>

    <!-- 记录列表 -->
    <section class="content-card">
      <div class="section-heading">
        <div>
          <h3>全部选课记录</h3>
          <p>支持按照学生、学号、课程或教师进行搜索</p>
        </div>

        <button
            class="refresh-button"
            @click="loadRecords"
            :disabled="loading"
        >
          <span>↻</span>
          {{ loading ? '加载中...' : '刷新数据' }}
        </button>
      </div>

      <!-- 搜索区域 -->
      <div class="search-area">
        <div class="search-box">
          <span class="search-icon">⌕</span>
          <input
              v-model="keyword"
              type="text"
              placeholder="搜索学生姓名、学号、课程名称或教师..."
          />
          <button
              v-if="keyword"
              class="clear-button"
              @click="keyword = ''"
          >
            清除
          </button>
        </div>

        <div class="search-result">
          当前显示 <strong>{{ filteredRecords.length }}</strong> 条记录
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <span>正在加载选课记录...</span>
      </div>

      <!-- 错误提示 -->
      <div v-else-if="errorMessage" class="error-state">
        <div class="error-icon">!</div>
        <strong>数据加载失败</strong>
        <span>{{ errorMessage }}</span>
        <button @click="loadRecords">重新加载</button>
      </div>

      <!-- 数据表格 -->
      <div v-else-if="filteredRecords.length > 0" class="table-wrapper">
        <table class="enrollment-table">
          <thead>
          <tr>
            <th>记录编号</th>
            <th>学生信息</th>
            <th>课程信息</th>
            <th>任课教师</th>
            <th>选课时间</th>
            <th>选课状态</th>
          </tr>
          </thead>

          <tbody>
          <tr v-for="item in filteredRecords" :key="item.id">
            <td>
              <span class="record-id">#{{ item.id }}</span>
            </td>

            <td>
              <div class="student-info">
                <div class="student-avatar">
                  {{ item.studentName?.charAt(0) || '学' }}
                </div>
                <div>
                  <strong>{{ item.studentName || '--' }}</strong>
                  <span>{{ item.studentNo || '--' }}</span>
                </div>
              </div>
            </td>

            <td>
              <div class="course-info">
                <strong>{{ item.courseName || '--' }}</strong>
                <span>课程记录</span>
              </div>
            </td>

            <td>
                <span class="teacher-name">
                  {{ item.teacher || '--' }}
                </span>
            </td>

            <td>
                <span class="time-text">
                  {{ formatTime(item.enrollTime) }}
                </span>
            </td>

            <td>
                <span
                    class="status-tag"
                    :class="item.status === 1 ? 'status-active' : 'status-dropped'"
                >
                  <span class="status-dot"></span>
                  {{ item.status === 1 ? '选课中' : '已退课' }}
                </span>
            </td>
          </tr>
          </tbody>
        </table>
      </div>

      <!-- 空数据状态 -->
      <div v-else class="empty-state">
        <div class="empty-icon">📋</div>
        <strong>
          {{ keyword ? '没有找到匹配的记录' : '暂无选课记录' }}
        </strong>
        <span>
          {{ keyword ? '请尝试更换搜索关键词' : '目前还没有学生选课记录' }}
        </span>
        <button v-if="keyword" @click="keyword = ''">
          清除搜索条件
        </button>
      </div>

      <!-- 底部信息 -->
      <div class="table-footer">
        <span>数据来源：学生选课记录</span>
        <span>共 {{ records.length }} 条记录</span>
      </div>
    </section>
  </div>
</template>

<style scoped>
.admin-enrollment-page {
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
  flex-shrink: 0;
  width: 46px;
  height: 46px;
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
  border: 1px solid #edf1f7;
  border-radius: 16px;
  background: #fff;
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
  flex-shrink: 0;
  padding: 9px 15px;
  color: #3979ca;
  background: #eef5ff;
  border: 1px solid #e0ecfc;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s ease;
}

.refresh-button span {
  margin-right: 5px;
  font-size: 16px;
}

.refresh-button:hover:not(:disabled) {
  background: #e2efff;
}

.refresh-button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.search-area {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 15px;
  margin-bottom: 18px;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 9px;
  width: min(440px, 100%);
  padding: 0 12px;
  border: 1px solid #e7edf5;
  border-radius: 9px;
  background: #fafcff;
  transition: border-color 0.2s ease;
}

.search-box:focus-within {
  border-color: #7eafe9;
  background: #fff;
}

.search-icon {
  color: #8da0b9;
  font-size: 23px;
}

.search-box input {
  width: 100%;
  height: 40px;
  border: none;
  outline: none;
  background: transparent;
  color: #405875;
  font-size: 12px;
}

.search-box input::placeholder {
  color: #a8b3c1;
}

.clear-button {
  flex-shrink: 0;
  padding: 5px 7px;
  border: none;
  background: transparent;
  color: #7e91aa;
  cursor: pointer;
  font-size: 11px;
}

.search-result {
  color: #95a2b2;
  font-size: 12px;
  white-space: nowrap;
}

.search-result strong {
  color: #3979ca;
}

.table-wrapper {
  width: 100%;
  overflow-x: auto;
}

.enrollment-table {
  width: 100%;
  min-width: 790px;
  border-collapse: separate;
  border-spacing: 0;
  text-align: left;
}

.enrollment-table th {
  padding: 14px 13px;
  color: #8291a7;
  background: #f7f9fc;
  font-size: 12px;
  font-weight: 600;
  white-space: nowrap;
}

.enrollment-table th:first-child {
  border-radius: 9px 0 0 9px;
}

.enrollment-table th:last-child {
  border-radius: 0 9px 9px 0;
}

.enrollment-table td {
  padding: 15px 13px;
  border-bottom: 1px solid #f0f3f7;
  color: #586b85;
  font-size: 13px;
  white-space: nowrap;
}

.enrollment-table tbody tr {
  transition: background 0.2s ease;
}

.enrollment-table tbody tr:hover {
  background: #f9fbff;
}

.record-id {
  color: #8295ad;
  font-size: 12px;
  font-weight: 600;
}

.student-info {
  display: flex;
  align-items: center;
  gap: 11px;
}

.student-avatar {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  width: 38px;
  height: 38px;
  border-radius: 11px;
  color: #3979ca;
  background: #eaf3ff;
  font-size: 15px;
  font-weight: 700;
}

.student-info strong,
.course-info strong {
  display: block;
  margin-bottom: 5px;
  color: #304865;
  font-size: 13px;
}

.student-info span,
.course-info span {
  display: block;
  color: #a0acbb;
  font-size: 11px;
}

.teacher-name {
  color: #536983;
}

.time-text {
  color: #7b8da4;
  font-size: 12px;
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

.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 42px 15px;
  color: #9aa7b8;
  font-size: 12px;
}

.error-icon {
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
  margin-bottom: 8px;
  color: #bd5555;
  font-size: 14px;
}

.error-state button,
.empty-state button {
  margin-top: 15px;
  padding: 8px 15px;
  color: #3979ca;
  background: #eef5ff;
  border: none;
  border-radius: 7px;
  cursor: pointer;
  font-size: 12px;
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

.table-footer {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  padding-top: 17px;
  color: #a0acbb;
  font-size: 11px;
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

  .search-area {
    align-items: flex-start;
    flex-direction: column;
  }

  .search-box {
    width: 100%;
  }

  .table-footer {
    flex-direction: column;
    gap: 7px;
  }
}
</style>
