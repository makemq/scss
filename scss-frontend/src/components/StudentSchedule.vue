<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
const courses = ref([])
const loading = ref(false)
const getHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem('accessToken')}`
})
// 获取当前学生的个人课表
const loadSchedule = async () => {
  loading.value = true
  try {
    const response = await axios.get(
        'http://localhost:8080/enrollment/my/schedule',
        { headers: getHeaders() }
    )

    if (response.data.code === 200) {
      courses.value = response.data.data
    } else {
      alert(response.data.message || '获取课表失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '获取课表失败')
  } finally {
    loading.value = false
  }
}
// 将星期数字转换为文字
const getDayName = (day) => {
  return ['星期一', '星期二', '星期三', '星期四',
    '星期五', '星期六', '星期日'][day - 1] || '未知'
}
onMounted(loadSchedule)
</script>
<template>
  <div class="schedule-container">
    <div class="schedule-header">
      <h3>我的个人课表</h3>
      <button @click="loadSchedule" :disabled="loading">
        刷新课表
      </button>
    </div>
    <div v-if="loading">正在加载课表...</div>
    <table v-else>
      <thead>
      <tr>
        <th>课程名称</th>
        <th>任课教师</th>
        <th>学分</th>
        <th>星期</th>
        <th>开始节次</th>
        <th>结束节次</th>
      </tr>
      </thead>
      <tbody>
      <tr v-if="courses.length === 0">
        <td colspan="6">当前暂无已选课程</td>
      </tr>
      <tr v-for="course in courses" :key="course.id">
        <td>{{ course.courseName }}</td>
        <td>{{ course.teacher }}</td>
        <td>{{ course.credit }}</td>
        <td>{{ getDayName(course.classDay) }}</td>
        <td>第{{ course.startSection }}节</td>
        <td>第{{ course.endSection }}节</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>
<style scoped>
.schedule-container {
  width: 100%;
  padding: 20px;
  background: white;
  border-radius: 8px;
}
.schedule-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
}
table {
  width: 100%;
  border-collapse: collapse;
}
th, td {
  padding: 13px;
  border-bottom: 1px solid #eee;
  text-align: left;
}
th {
  background: #f5f7fa;
}
button {
  padding: 8px 15px;
  border: none;
  border-radius: 5px;
  background: #28558b;
  color: white;
  cursor: pointer;
}
button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
