<script setup>
import { ref } from 'vue'
import EnrollmentManagement from '../components/EnrollmentManagement.vue'
import StudentSchedule from '../components/StudentSchedule.vue'
const props = defineProps({
  user: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['logout'])

const currentPage = ref('首页')
</script>

<template>
  <div class="student-dashboard">
    <header class="header">
      <div class="logo">学生选课管理系统</div>
      <div class="user-info">
        <span>欢迎，{{ user.username }}</span>
        <button @click="emit('logout')">退出登录</button>
      </div>
    </header>

    <div class="main">
      <aside class="sidebar">
        <div
            v-for="item in ['首页', '选课管理', '个人课表', '个人中心']"
            :key="item"
            :class="['menu-item', { active: currentPage === item }]"
            @click="currentPage = item"
        >
          {{ item }}
        </div>
      </aside>

      <main class="content">
        <h2>{{ currentPage }}</h2>

        <div v-if="currentPage === '首页'" class="welcome">
          <h3>欢迎使用学生选课管理系统！</h3>
          <p>在这里可以浏览课程、选择课程和管理已选课程。</p>
        </div>

        <div v-else-if="currentPage === '选课管理'">
          <EnrollmentManagement />
        </div>
        <div v-else-if="currentPage === '个人课表'">
          <StudentSchedule />
        </div>

        <div v-else-if="currentPage === '个人中心'" class="profile-card">
          <h3>个人信息</h3>


          <div class="profile-item">
            <span>登录账号：</span>
            <span>{{ user.username }}</span>
          </div>

          <div class="profile-item">
            <span>用户角色：</span>
            <span>{{ user.role }}</span>
          </div>

          <div class="profile-item">
            <span>学生姓名：</span>
            <span>{{ user.name || '暂未绑定' }}</span>
          </div>

          <div class="profile-item">
            <span>学号：</span>
            <span>{{ user.studentNo || '暂未绑定' }}</span>
          </div>

          <div class="profile-item">
            <span>学生ID：</span>
            <span>{{ user.studentId || '暂未绑定' }}</span>
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<style scoped>
.student-dashboard {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f4f6f9;
}

.header {
  height: 60px;
  padding: 0 25px;
  background: #0056b3;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.logo {
  font-size: 20px;
  font-weight: bold;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

button {
  padding: 8px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.main {
  flex: 1;
  display: flex;
}

.sidebar {
  width: 200px;
  background: white;
  padding-top: 15px;
}

.menu-item {
  padding: 16px 25px;
  cursor: pointer;
  color: #333;
}

.menu-item:hover,
.menu-item.active {
  color: #0056b3;
  background: #e8f2ff;
}

.content {
  flex: 1;
  padding: 30px;
}

.welcome {
  margin-top: 25px;
  padding: 25px;
  background: white;
  border-radius: 8px;
}

.welcome p {
  margin-top: 15px;
  color: #666;
}
.profile-card {
  background: white;
  padding: 24px;
  border-radius: 10px;
}

.profile-card h3 {
  margin-bottom: 20px;
}

.profile-item {
  display: flex;
  padding: 14px 0;
  border-bottom: 1px solid #eee;
}

.profile-item span:first-child {
  width: 110px;
  color: #666;
}

.profile-item span:last-child {
  color: #333;
  font-weight: 500;
}
</style>
