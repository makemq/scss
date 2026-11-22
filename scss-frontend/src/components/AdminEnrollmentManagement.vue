<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const records = ref([])
const loading = ref(false)
const errorMessage = ref('')
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

onMounted(loadRecords)
</script>

<template>
  <div class="enrollment-container">
    <div class="toolbar">
      <h3>全部选课记录</h3>
      <button @click="loadRecords">刷新</button>
    </div>

    <p v-if="loading">正在加载...</p>
    <p v-else-if="errorMessage" class="error">{{ errorMessage }}</p>

    <table v-else>
      <thead>
      <tr>
        <th>记录ID</th>
        <th>学生姓名</th>
        <th>学号</th>
        <th>课程名称</th>
        <th>任课教师</th>
        <th>选课时间</th>
        <th>状态</th>
      </tr>
      </thead>

      <tbody>
      <tr v-for="item in records" :key="item.id">
        <td>{{ item.id }}</td>
        <td>{{ item.studentName }}</td>
        <td>{{ item.studentNo }}</td>
        <td>{{ item.courseName }}</td>
        <td>{{ item.teacher }}</td>
        <td>{{ item.enrollTime }}</td>
        <td>
          {{ item.status === 1 ? '选课中' : '已退课' }}
        </td>
      </tr>

      <tr v-if="records.length === 0">
        <td colspan="7">暂无选课记录</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.enrollment-container {
  padding: 20px;
  background: white;
  border-radius: 8px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

button {
  padding: 7px 16px;
  border: none;
  border-radius: 4px;
  background: #2563a6;
  color: white;
  cursor: pointer;
}

table {
  width: 100%;
  border-collapse: collapse;
  text-align: center;
}

th, td {
  padding: 12px 8px;
  border: 1px solid #eee;
}

th {
  background: #f3f5f9;
}

.error {
  color: #dc3545;
}
</style>
