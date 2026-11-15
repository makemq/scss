
<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const courses = ref([])
const loading = ref(false)

const keyword = ref('')
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

const showAddForm = ref(false)
const showEditForm = ref(false)

const emptyCourse = () => ({
  courseName: '',
  teacher: '',
  credit: 2.0,
  capacity: 50,
  classDay: 1,
  startSection: 1,
  endSection: 2
})

const newCourse = ref(emptyCourse())
const editCourse = ref({ id: null, ...emptyCourse() })

const getHeaders = () => ({
  Authorization: `Bearer ${localStorage.getItem('token')}`
})

// 查询课程
const loadCourses = async () => {
  loading.value = true
  try {
    const response = await axios.get(
        'http://localhost:8080/course/page',
        {
          params: {
            keyword: keyword.value,
            pageNum: pageNum.value,
            pageSize: pageSize.value
          },
          headers: getHeaders()
        }
    )

    if (response.data.code === 200) {
      courses.value = response.data.data.records
      total.value = response.data.data.total
    } else {
      alert(response.data.message || '获取课程失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '获取课程失败')
  } finally {
    loading.value = false
  }
}

// 搜索课程
const searchCourses = () => {
  pageNum.value = 1
  loadCourses()
}

// 新增课程
const addCourse = async () => {
  const course = newCourse.value

  if (!course.courseName.trim() || !course.teacher.trim()) {
    alert('课程名称和教师不能为空')
    return
  }

  try {
    const response = await axios.post(
        'http://localhost:8080/course/add',
        course,
        { headers: getHeaders() }
    )

    if (response.data.code === 200) {
      alert('课程新增成功')
      showAddForm.value = false
      newCourse.value = emptyCourse()
      pageNum.value = 1
      loadCourses()
    } else {
      alert(response.data.message || '新增失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '新增课程失败')
  }
}

// 打开修改表单
const openEditForm = (course) => {
  editCourse.value = { ...course }
  showEditForm.value = true
}

// 修改课程
const updateCourse = async () => {
  const course = editCourse.value

  if (!course.courseName.trim() || !course.teacher.trim()) {
    alert('课程名称和教师不能为空')
    return
  }

  try {
    const response = await axios.put(
        'http://localhost:8080/course/update',
        course,
        { headers: getHeaders() }
    )

    if (response.data.code === 200) {
      alert('课程修改成功')
      showEditForm.value = false
      loadCourses()
    } else {
      alert(response.data.message || '修改失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '修改课程失败')
  }
}

// 删除课程
const deleteCourse = async (id) => {
  if (!confirm('确定要删除这门课程吗？')) return

  try {
    const response = await axios.delete(
        `http://localhost:8080/course/${id}`,
        { headers: getHeaders() }
    )

    if (response.data.code === 200) {
      alert('课程删除成功')
      loadCourses()
    } else {
      alert(response.data.message || '删除失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '删除课程失败')
  }
}

const previousPage = () => {
  if (pageNum.value > 1) {
    pageNum.value--
    loadCourses()
  }
}

const nextPage = () => {
  if (pageNum.value * pageSize.value < total.value) {
    pageNum.value++
    loadCourses()
  }
}

onMounted(loadCourses)
</script>

<template>
  <div class="course-management">
    <button @click="showAddForm = !showAddForm">
      {{ showAddForm ? '取消新增' : '新增课程' }}
    </button>

    <div v-if="showAddForm" class="course-form">
      <h3>新增课程</h3>
      <input v-model="newCourse.courseName" placeholder="课程名称" />
      <input v-model="newCourse.teacher" placeholder="任课教师" />
      <input v-model.number="newCourse.credit" type="number" min="0" step="0.5" placeholder="学分" />
      <input v-model.number="newCourse.capacity" type="number" min="1" placeholder="课程容量" />
      <label>上课星期</label>
      <select v-model.number="newCourse.classDay">
        <option v-for="day in 7" :key="day" :value="day">星期{{ day }}</option>
      </select>
      <input v-model.number="newCourse.startSection" type="number" min="1" placeholder="开始节次" />
      <input v-model.number="newCourse.endSection" type="number" min="1" placeholder="结束节次" />
      <button @click="addCourse">提交</button>
    </div>

    <div v-if="showEditForm" class="course-form">
      <h3>修改课程</h3>
      <input v-model="editCourse.courseName" placeholder="课程名称" />
      <input v-model="editCourse.teacher" placeholder="任课教师" />
      <input v-model.number="editCourse.credit" type="number" min="0" step="0.5" placeholder="学分" />
      <input v-model.number="editCourse.capacity" type="number" min="1" placeholder="课程容量" />
      <label>上课星期</label>
      <select v-model.number="editCourse.classDay">
        <option v-for="day in 7" :key="day" :value="day">星期{{ day }}</option>
      </select>
      <input v-model.number="editCourse.startSection" type="number" min="1" placeholder="开始节次" />
      <input v-model.number="editCourse.endSection" type="number" min="1" placeholder="结束节次" />
      <button @click="updateCourse">保存修改</button>
      <button @click="showEditForm = false">取消</button>
    </div>

    <div class="toolbar">
      <input
          v-model="keyword"
          placeholder="搜索课程名称或教师"
          @keyup.enter="searchCourses"
      />
      <button @click="searchCourses">搜索</button>
    </div>

    <table>
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
      <tr v-if="loading">
        <td colspan="8">正在加载...</td>
      </tr>
      <tr v-else-if="courses.length === 0">
        <td colspan="8">暂无课程数据</td>
      </tr>
      <tr v-for="course in courses" :key="course.id">
        <td>{{ course.id }}</td>
        <td>{{ course.courseName }}</td>
        <td>{{ course.teacher }}</td>
        <td>{{ course.credit }}</td>
        <td>{{ course.capacity }}</td>
        <td>{{ course.selectedCount }}</td>
        <td>星期{{ course.classDay }}，第{{ course.startSection }}-{{ course.endSection }}节</td>
        <td>
          <button @click="openEditForm(course)">修改</button>
          <button @click="deleteCourse(course.id)">删除</button>
        </td>
      </tr>
      </tbody>
    </table>

    <div class="pagination">
      <button :disabled="pageNum <= 1 || loading" @click="previousPage">
        上一页
      </button>
      <span>第 {{ pageNum }} 页，共 {{ total }} 条记录</span>
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
.course-management {
  width: 100%;
}

.course-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 10px;
  margin: 15px 0;
  padding: 18px;
  background: white;
  border-radius: 6px;
}

.course-form h3 {
  width: 100%;
}

input,
select {
  padding: 8px 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
}

.toolbar {
  display: flex;
  gap: 10px;
  margin: 20px 0;
}

.toolbar input {
  width: 280px;
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

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  margin-top: 20px;
}
</style>
