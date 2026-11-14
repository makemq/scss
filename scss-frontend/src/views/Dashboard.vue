
<script setup>
import { ref } from 'vue'
import StudentManagement from './StudentManagement.vue'
defineProps({
  user: {
    type: Object,
    default: () => ({})
  }
})

// 当前选中的菜单
const currentPage = ref('首页')

// 点击菜单时切换页面
const changePage = (page) => {
  currentPage.value = page
}
</script>

<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="system-title">
        学生选课管理系统
      </div>
      <div
          class="menu-item"
          :class="{ active: currentPage === '首页' }"
          @click="changePage('首页')"
      >
        首页
      </div>

      <div
          class="menu-item"
          :class="{ active: currentPage === '学生管理' }"
          @click="changePage('学生管理')"
      >
        学生管理
      </div>

      <div
          class="menu-item"
          :class="{ active: currentPage === '课程管理' }"
          @click="changePage('课程管理')"
      >
        课程管理
      </div>

      <div
          class="menu-item"
          :class="{ active: currentPage === '选课管理' }"
          @click="changePage('选课管理')"
      >
        选课管理
      </div>

    </aside>

    <section class="main-area">
      <header class="topbar">
        <span>管理后台</span>

        <div class="user-info">
          {{ user.username || '用户' }}
          <span class="role">
            {{ user.role || '未知角色' }}
          </span>
        </div>
      </header>

      <main class="content">
        <h2>{{ currentPage }}</h2>

        <div v-if="currentPage === '首页'" class="welcome-card">
          <h3>系统首页</h3>
          <p>欢迎进入学生选课管理系统！</p>
          <p>当前登录用户：{{ user.username }}</p>
          <p>当前用户角色：{{ user.role }}</p>
        </div>

        <StudentManagement
            v-else-if="currentPage === '学生管理'"
        />

        <div v-else class="welcome-card">
          <h3>{{ currentPage }}</h3>
          <p>{{ currentPage }}功能正在建设中，敬请期待。</p>
        </div>
      </main>
    </section>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  background: #f3f5f9;
  color: #333;
}
.sidebar {
  width: 220px;
  flex-shrink: 0;
  background: #183153;
  color: white;
}

.system-title {
  padding: 25px 15px;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid rgba(255,255,255,0.15);
}

.menu-item {
  padding: 17px 30px;
  cursor: pointer;
  color: #d5deeb;
}

.menu-item:hover,
.menu-item.active {
  background: #28558b;
  color: white;
}

.main-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.topbar {
  height: 60px;
  flex-shrink: 0;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 30px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.role {
  padding: 4px 10px;
  background: #e8f1ff;
  color: #2563a6;
  border-radius: 4px;
  font-size: 12px;
}

.content {
  flex: 1;
  min-height: 0;
  padding: 30px;
  overflow-y: auto;
}
.content h2 {
  margin-bottom: 12px;
}

.content p {
  color: #777;
}

.welcome-card {
  margin-top: 25px;
  padding: 25px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
}

.welcome-card h3 {
  margin-bottom: 18px;
}

.welcome-card p {
  margin-top: 10px;
}
</style>

