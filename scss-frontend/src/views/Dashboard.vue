
<script setup>
import { ref, computed } from 'vue'
import StudentManagement from './StudentManagement.vue'
import CourseManagement from './CourseManagement.vue'
import AdminEnrollmentManagement from '../components/AdminEnrollmentManagement.vue'

const props = defineProps({
  user: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['logout'])
const currentPage = ref('首页')

const menuItems = [
  { name: '首页', icon: '⌂' },
  { name: '学生管理', icon: '♙' },
  { name: '课程管理', icon: '▤' },
  { name: '选课管理', icon: '▦' }
]

const pageDescription = computed(() => ({
  '首页': '查看系统概况，管理学生与课程数据',
  '学生管理': '管理学生资料及账户信息',
  '课程管理': '维护课程信息与课程容量',
  '选课管理': '查看和管理学生选课记录'
}[currentPage.value]))

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    emit('logout')
  }
}
</script>

<template>
  <div class="admin-dashboard">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-icon">学</div>
        <div class="brand-text">
          <strong>雪峰智慧选课</strong>
          <span>ADMIN CONSOLE</span>
        </div>
      </div>

      <div class="nav-caption">管理菜单</div>

      <div
          v-for="item in menuItems"
          :key="item.name"
          class="menu-item"
          :class="{ active: currentPage === item.name }"
          @click="currentPage = item.name"
      >
        <span class="menu-icon">{{ item.icon }}</span>
        <span>{{ item.name }}</span>
        <span v-if="currentPage === item.name" class="active-dot"></span>
      </div>

      <div class="sidebar-bottom">
        <div class="admin-tip">
          <div class="tip-icon">✦</div>
          <strong>系统管理中心</strong>
          <p>请规范管理学生、课程及选课数据。</p>
        </div>
        <div class="sidebar-footer">© 2026 雪峰智慧选课平台</div>
      </div>
    </aside>

    <section class="main-area">
      <header class="topbar">
        <div class="breadcrumb">
          <span>管理后台</span>
          <span class="separator">/</span>
          <strong>{{ currentPage }}</strong>
        </div>

        <div class="user-area">
          <div class="user-avatar">
            {{ (user.username || '管').charAt(0) }}
          </div>
          <div class="user-text">
            <strong>{{ user.username || '管理员' }}</strong>
            <span>{{ user.role || 'ADMIN' }}</span>
          </div>
          <button class="logout-btn" @click="handleLogout">
            退出登录
          </button>
        </div>
      </header>

      <main class="content">
        <div class="page-heading">
          <div>
            <div class="page-eyebrow">ADMIN WORKSPACE</div>
            <h1>{{ currentPage }}</h1>
            <p>{{ pageDescription }}</p>
          </div>
          <div class="heading-decoration">✦</div>
        </div>

        <section v-if="currentPage === '首页'" class="home-content">
          <div class="welcome-banner">
            <div>
              <span class="welcome-label">ADMINISTRATOR PORTAL</span>
              <h2>欢迎回来，{{ user.username || '管理员' }}！</h2>
              <p>这里是雪峰智慧选课平台管理中心。</p>
            </div>
            <div class="banner-decoration">管</div>
          </div>

          <div class="section-title">
            <h2>管理功能</h2>
            <p>选择模块，开始管理系统数据</p>
          </div>

          <div class="management-grid">
            <button class="management-card" @click="currentPage = '学生管理'">
              <div class="card-icon blue">♙</div>
              <strong>学生管理</strong>
              <span>管理学生基本信息与资料</span>
              <b>进入管理 →</b>
            </button>

            <button class="management-card" @click="currentPage = '课程管理'">
              <div class="card-icon purple">▤</div>
              <strong>课程管理</strong>
              <span>维护课程信息与开课状态</span>
              <b>进入管理 →</b>
            </button>

            <button class="management-card" @click="currentPage = '选课管理'">
              <div class="card-icon cyan">▦</div>
              <strong>选课管理</strong>
              <span>查看学生选课及相关记录</span>
              <b>进入管理 →</b>
            </button>
          </div>

          <div class="notice-card">
            <div class="notice-icon">i</div>
            <div>
              <strong>管理提示</strong>
              <p>修改学生或课程数据前，请核对相关信息，避免误操作影响正常选课。</p>
            </div>
          </div>
        </section>

        <section v-else-if="currentPage === '学生管理'">
          <StudentManagement />
        </section>

        <section v-else-if="currentPage === '课程管理'">
          <CourseManagement />
        </section>

        <section v-else-if="currentPage === '选课管理'">
          <AdminEnrollmentManagement />
        </section>
      </main>
    </section>
  </div>
</template>

<style scoped>
.admin-dashboard {
  width: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  color: #26364a;
  background: #f5f7fb;
  font-family: "Microsoft YaHei", sans-serif;
}

.sidebar {
  width: 248px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  padding: 22px 15px 15px;
  color: #d9e5f5;
  background: linear-gradient(180deg, #142f55, #102541);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 5px 8px 30px;
}

.brand-icon {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  color: white;
  background: linear-gradient(135deg, #64b5ff, #2873d4);
  font-size: 22px;
  font-weight: bold;
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.brand-text strong {
  color: white;
  font-size: 16px;
}

.brand-text span {
  color: #91a9c8;
  font-size: 9px;
  letter-spacing: 1.5px;
}

.nav-caption {
  padding: 15px 12px;
  color: #8198b6;
  font-size: 11px;
  letter-spacing: 1px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 4px 0;
  padding: 14px;
  border-radius: 9px;
  color: #c3d1e4;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s;
}

.menu-item:hover {
  color: white;
  background: rgba(255,255,255,0.08);
}

.menu-item.active {
  color: white;
  background: linear-gradient(90deg, #2878d0, #2863a8);
  box-shadow: 0 5px 15px rgba(0,0,0,0.15);
}

.menu-icon {
  width: 20px;
  text-align: center;
  font-size: 19px;
}

.active-dot {
  width: 6px;
  height: 6px;
  margin-left: auto;
  border-radius: 50%;
  background: #8bd4ff;
}

.sidebar-bottom {
  margin-top: auto;
}

.admin-tip {
  padding: 17px;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
  background: rgba(255,255,255,0.06);
}

.tip-icon {
  margin-bottom: 10px;
  color: #73c8ff;
  font-size: 22px;
}

.admin-tip strong {
  color: white;
  font-size: 13px;
}

.admin-tip p {
  margin-top: 9px;
  color: #a8bbd3;
  font-size: 11px;
  line-height: 1.7;
}

.sidebar-footer {
  margin-top: 18px;
  color: #7188a5;
  text-align: center;
  font-size: 10px;
}

.main-area {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.topbar {
  height: 70px;
  flex-shrink: 0;
  padding: 0 34px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  border-bottom: 1px solid #edf0f5;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #8b98a9;
  font-size: 13px;
}

.breadcrumb strong {
  color: #26364a;
}

.separator {
  color: #c4cbd5;
}

.user-area {
  display: flex;
  align-items: center;
  gap: 11px;
}

.user-avatar {
  width: 37px;
  height: 37px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  color: #276bb7;
  background: #e5f0ff;
  font-weight: bold;
}

.user-text {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-text strong {
  color: #35445a;
  font-size: 12px;
}

.user-text span {
  color: #96a1b0;
  font-size: 10px;
}

.logout-btn {
  margin-left: 14px;
  padding: 8px 13px;
  border: 1px solid #e6eaf0;
  border-radius: 7px;
  color: #6d7888;
  background: white;
  cursor: pointer;
  font-size: 12px;
}

.logout-btn:hover {
  border-color: #e2a5a5;
  color: #d34d4d;
  background: #fff7f7;
}

.content {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  padding: 30px 34px 45px;
}

.page-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.page-eyebrow {
  margin-bottom: 8px;
  color: #4787c9;
  font-size: 10px;
  letter-spacing: 2px;
}

.page-heading h1 {
  color: #26364a;
  font-size: 25px;
}

.page-heading p {
  margin-top: 8px;
  color: #8b97a8;
  font-size: 13px;
}

.heading-decoration {
  padding-right: 25px;
  color: #d5e5f7;
  font-size: 55px;
}

.welcome-banner {
  position: relative;
  min-height: 205px;
  display: flex;
  align-items: center;
  overflow: hidden;
  padding: 30px 38px;
  border-radius: 16px;
  color: white;
  background: linear-gradient(110deg, #1d4f87, #367fc2);
}

.welcome-label {
  color: #b8dcff;
  font-size: 10px;
  letter-spacing: 2px;
}

.welcome-banner h2 {
  margin-top: 13px;
  font-size: 25px;
}

.welcome-banner p {
  margin-top: 12px;
  color: #d4e8fb;
  font-size: 13px;
}

.banner-decoration {
  position: absolute;
  right: 8%;
  color: rgba(255,255,255,0.1);
  font-size: 155px;
  font-weight: bold;
}

.section-title {
  margin: 29px 0 16px;
}

.section-title h2 {
  color: #2c3b50;
  font-size: 17px;
}

.section-title p {
  margin-top: 6px;
  color: #9aa5b3;
  font-size: 12px;
}

.management-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.management-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-height: 190px;
  padding: 22px;
  border: 1px solid #edf0f5;
  border-radius: 12px;
  background: white;
  text-align: left;
  cursor: pointer;
  transition: 0.2s;
}

.management-card:hover {
  transform: translateY(-3px);
  border-color: #b8d5f5;
  box-shadow: 0 8px 22px rgba(38,90,150,0.08);
}

.card-icon {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  margin-bottom: 16px;
  border-radius: 10px;
  font-size: 22px;
}

.card-icon.blue { color: #347bc5; background: #eaf3ff; }
.card-icon.purple { color: #8063c5; background: #f1edff; }
.card-icon.cyan { color: #2399a8; background: #e6f8f8; }

.management-card strong {
  color: #344258;
  font-size: 15px;
}

.management-card span {
  margin-top: 9px;
  color: #9aa5b3;
  font-size: 12px;
}

.management-card b {
  margin-top: auto;
  padding-top: 18px;
  color: #397dc0;
  font-size: 12px;
  font-weight: normal;
}

.notice-card {
  display: flex;
  align-items: flex-start;
  gap: 13px;
  margin-top: 22px;
  padding: 17px 20px;
  border: 1px solid #e5eef9;
  border-radius: 10px;
  background: #f0f6fd;
}

.notice-icon {
  width: 21px;
  height: 21px;
  display: grid;
  place-items: center;
  border-radius: 50%;
  color: white;
  background: #5b96d2;
  font-size: 12px;
}

.notice-card strong {
  color: #3d6288;
  font-size: 12px;
}

.notice-card p {
  margin-top: 6px;
  color: #7790a9;
  font-size: 11px;
  line-height: 1.7;
}

@media (max-width: 900px) {
  .management-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 700px) {
  .sidebar {
    width: 76px;
    padding: 15px 8px;
  }

  .brand {
    justify-content: center;
    padding: 5px 0 25px;
  }

  .brand-text,
  .nav-caption,
  .menu-item > span:not(.menu-icon),
  .sidebar-bottom {
    display: none;
  }

  .menu-item {
    justify-content: center;
    padding: 14px 5px;
  }

  .topbar {
    height: 60px;
    padding: 0 12px;
  }

  .user-text {
    display: none;
  }

  .logout-btn {
    margin-left: 2px;
    padding: 7px;
  }

  .content {
    padding: 22px 14px;
  }

  .heading-decoration {
    display: none;
  }

  .welcome-banner {
    padding: 24px;
  }

  .welcome-banner h2 {
    font-size: 20px;
  }

  .management-grid {
    grid-template-columns: 1fr;
  }
}
</style>
