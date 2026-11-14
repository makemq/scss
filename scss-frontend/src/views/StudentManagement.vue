
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

// 学生列表
const students = ref([])

// 分页信息
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索关键词
const keyword = ref('')

// 加载状态
const loading = ref(false)

// 查询学生
const loadStudents = async () => {
  loading.value = true

  try {
    const token = localStorage.getItem('token')

    const response = await axios.get(
        'http://localhost:8080/student/page',
        {
          params: {
            keyword: keyword.value,
            pageNum: pageNum.value,
            pageSize: pageSize.value
          },
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )

    if (response.data.code === 200) {
      const pageData = response.data.data
      students.value = pageData.records
      total.value = pageData.total
    } else {
      alert(response.data.message || '获取学生列表失败')
    }
  } catch (error) {
    alert(
        error.response?.data?.message ||
        '获取学生列表失败，请检查后端服务'
    )
  } finally {
    loading.value = false
  }
}

// 搜索
const searchStudents = () => {
  pageNum.value = 1
  loadStudents()
}

// 上一页
const previousPage = () => {
  if (pageNum.value > 1) {
    pageNum.value--
    loadStudents()
  }
}

// 下一页
const nextPage = () => {
  if (pageNum.value * pageSize.value < total.value) {
    pageNum.value++
    loadStudents()
  }
}

// 页面加载时查询学生
onMounted(() => {
  loadStudents()
})
</script>

<template>
  <div class="student-management">
    <div class="toolbar">
      <input
          v-model="keyword"
          placeholder="请输入学生姓名或学号"
          @keyup.enter="searchStudents"
      />
      <button @click="searchStudents">搜索</button>
    </div>

    <table>
      <thead>
      <tr>
        <th>ID</th>
        <th>姓名</th>
        <th>学号</th>
      </tr>
      </thead>

      <tbody>
      <tr v-if="loading">
        <td colspan="3">正在加载...</td>
      </tr>

      <tr v-else-if="students.length === 0">
        <td colspan="3">暂无学生数据</td>
      </tr>

      <tr v-for="student in students" :key="student.id">
        <td>{{ student.id }}</td>
        <td>{{ student.name }}</td>
        <td>{{ student.studentNo }}</td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button
          :disabled="pageNum <= 1 || loading"
          @click="previousPage"
      >
        上一页
      </button>

      <span>
        第 {{ pageNum }} 页，共 {{ total }} 条记录
      </span>

      <button
          :disabled="pageNum * pageSize >= total || loading"
          @click="nextPage"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<style scoped>
.student-management {
  width: 100%;
}

.toolbar {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.toolbar input {
  width: 280px;
  padding: 9px 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

button {
  padding: 8px 16px;
  border: none;
  border-radius: 5px;
  background: #28558b;
  color: white;
  cursor: pointer;
}

button:disabled {
  background: #aaa;
  cursor: not-allowed;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

th,
td {
  padding: 14px;
  border-bottom: 1px solid #eee;
  text-align: left;
}

th {
  background: #f5f7fa;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style>
