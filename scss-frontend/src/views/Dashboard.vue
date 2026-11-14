
<script setup>
import { defineProps } from 'vue'

defineProps({
  user: {
    type: Object,
    default: () => ({})
  }
})
</script>

<template>
  <div class="dashboard">
    <aside class="sidebar">
      <div class="system-title">
        学生选课管理系统
      </div>

      <div class="menu-item active">
        <span>首页</span>
      </div>

      <div class="menu-item">
        <span>学生管理</span>
      </div>

      <div class="menu-item">
        <span>课程管理</span>
      </div>

      <div class="menu-item">
        <span>选课管理</span>
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
        <h2>欢迎进入学生选课管理系统</h2>
        <p>请从左侧菜单选择需要使用的功能。</p>

        <div class="welcome-card">
          <h3>系统首页</h3>
          <p>当前登录用户：{{ user.username }}</p>
          <p>当前用户角色：{{ user.role }}</p>
        </div>
      </main>
    </section>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  width: 100%;
  height: 100%;
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
  padding: 30px;
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

