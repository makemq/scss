
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
// 新增学生表单
const showAddForm = ref(false)
const newStudent = ref({
  name: '',
  studentNo: ''
})
// 修改学生表单
const showEditForm = ref(false)
const editStudent = ref({
  id: null,
  name: '',
  studentNo: ''
})

// 打开修改表单
const openEditForm = (student) => {
  editStudent.value = {
    id: student.id,
    name: student.name,
    studentNo: student.studentNo
  }
  showEditForm.value = true
}

// 修改学生
const updateStudent = async () => {
  if (!editStudent.value.name.trim() || !editStudent.value.studentNo.trim()) {
    alert('姓名和学号不能为空')
    return
  }

  try {
    const token = localStorage.getItem('token')

    const response = await axios.put(
        'http://localhost:8080/student/update',
        {
          id: editStudent.value.id,
          name: editStudent.value.name.trim(),
          studentNo: editStudent.value.studentNo.trim()
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )

    if (response.data.code === 200) {
      alert('学生信息修改成功')
      showEditForm.value = false
      loadStudents()
    } else {
      alert(response.data.message || '修改失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '修改学生失败')
  }
}
// 删除学生
const deleteStudent = async (id) => {
  if (!confirm('确定要删除这名学生吗？')) {
    return
  }

  try {
    const token = localStorage.getItem('token')

    const response = await axios.delete(
        `http://localhost:8080/student/${id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )

    if (response.data.code === 200) {
      alert('学生删除成功')
      loadStudents()
    } else {
      alert(response.data.message || '删除失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '删除学生失败')
  }
}
// 新增学生
const addStudent = async () => {
  if (!newStudent.value.name.trim() || !newStudent.value.studentNo.trim()) {
    alert('姓名和学号不能为空')
    return
  }

  try {
    const token = localStorage.getItem('token')

    const response = await axios.post(
        'http://localhost:8080/student/add',
        {
          name: newStudent.value.name.trim(),
          studentNo: newStudent.value.studentNo.trim()
        },
        {
          headers: {
            Authorization: `Bearer ${token}`
          }
        }
    )

    if (response.data.code === 200) {
      alert('学生新增成功')
      newStudent.value = { name: '', studentNo: '' }
      showAddForm.value = false
      pageNum.value = 1
      loadStudents()
    } else {
      alert(response.data.message || '新增失败')
    }
  } catch (error) {
    alert(error.response?.data?.message || '新增学生失败')
  }
}
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
    <button @click="showAddForm = !showAddForm">
      {{ showAddForm ? '取消新增' : '新增学生' }}
    </button>

    <div v-if="showAddForm" class="add-form">
      <input
          v-model="newStudent.name"
          placeholder="请输入学生姓名"
      />

      <input
          v-model="newStudent.studentNo"
          placeholder="请输入学生学号"
      />

      <button @click="addStudent">提交</button>
    </div>
    <!-- 修改学生表单 -->
    <div v-if="showEditForm" class="add-form">
      <input
          v-model="editStudent.name"
          placeholder="请输入学生姓名"
      />

      <input
          v-model="editStudent.studentNo"
          placeholder="请输入学生学号"
      />

      <button @click="updateStudent">保存修改</button>

      <button @click="showEditForm = false">取消</button>
    </div>
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
        <th>操作</th>
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
        <td>
          <button @click="openEditForm(student)">修改</button>
          <button @click="deleteStudent(student.id)">删除</button>
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
.add-form {
  display: flex;
  gap: 10px;
  margin: 15px 0;
}

.add-form input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
}
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
