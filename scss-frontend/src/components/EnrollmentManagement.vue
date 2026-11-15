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
  Authorization: `Bearer ${localStorage.getItem('token')}`
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

onMounted(loadData)
</script>

<template>
  <div class="enrollment-management">
    <h2>学生选课</h2>

    <div v-if="loading">正在加载...</div>

    <table v-else>
      <thead>
      <tr>
        <th>ID</th>
        <th>课程名称</th>
        <th>教师</th>
        <th>学分</th>
        <th>容量</th>
        <th>已选人数</th>
        <th>上课时间</th>
        <th>操作</th>
      </tr>
      </thead>

      <tbody>
      <tr v-if="courses.length === 0">
        <td colspan="8">暂无课程数据</td>
      </tr>

      <tr v-for="course in courses" :key="course.id">
        <td>{{ course.id }}</td>
        <td>{{ course.courseName }}</td>
        <td>{{ course.teacher }}</td>
        <td>{{ course.credit }}</td>
        <td>{{ course.capacity }}</td>
        <td>{{ course.selectedCount }}</td>
        <td>
          星期{{ course.classDay }}，
          第{{ course.startSection }}-{{ course.endSection }}节
        </td>
        <td>
          <button
              v-if="isSelected(course.id)"
              @click="dropCourse(course.id)"
          >
            退选
          </button>

          <button
              v-else
              @click="selectCourse(course.id)"
          >
            选课
          </button>
        </td>
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

      <span>第 {{ pageNum }} 页，共 {{ total }} 条课程</span>

      <button
          :disabled="pageNum * pageSize >= total || loading"
          @click="nextPage"
      >
        下一页
      </button>
    </div>

    <h2>我的选课记录</h2>

    <table>
      <thead>
      <tr>
        <th>选课记录ID</th>
        <th>课程ID</th>
        <th>选课时间</th>
        <th>状态</th>
      </tr>
      </thead>

      <tbody>
      <tr v-if="myEnrollments.length === 0">
        <td colspan="4">暂无选课记录</td>
      </tr>

      <tr v-for="item in myEnrollments" :key="item.id">
        <td>{{ item.id }}</td>
        <td>{{ item.courseId }}</td>
        <td>{{ item.enrollTime }}</td>
        <td>{{ item.status === 1 ? '已选课' : '已退选' }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<style scoped>
.enrollment-management {
  width: 100%;
}

h2 {
  margin: 20px 0;
}

table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

th,
td {
  padding: 12px;
  border-bottom: 1px solid #eee;
  text-align: left;
}

th {
  background: #f5f7fa;
}

button {
  padding: 8px 14px;
  margin-right: 5px;
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

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style>



