
<script setup>
import { ref, computed } from 'vue'
import EnrollmentManagement from '../components/EnrollmentManagement.vue'
import StudentSchedule from '../components/StudentSchedule.vue'
import AiLearningCenter from './AiLearningCenter.vue'

const props = defineProps({
  user: {
    type: Object,
    default: () => ({})
  }
})

const emit = defineEmits(['logout'])

const currentPage = ref('首页')

const menuItems = [
  { name: '首页', icon: '⌂', group: '学习中心' },
  { name: '选课管理', icon: '▤', group: '学习中心' },
  { name: '个人课表', icon: '▦', group: '学习中心' },
  { name: '雪峰AI选课助手', icon: '✦', group: '智能服务' },
  { name: '个人中心', icon: '◎', group: '账户管理' }
]

const currentDescription = computed(() => {
  const descriptions = {
    '首页': '查看系统功能，开始你的学习之旅',
    '选课管理': '浏览课程信息，管理你的选课记录',
    '个人课表': '查看本学期课程安排',
    '雪峰AI选课助手': '让 AI 帮助你发现适合自己的课程',
    '个人中心': '查看和管理你的个人信息'
  }
  return descriptions[currentPage.value]
})
</script>

<template>
  <div class="student-dashboard">
    <aside class="sidebar">
      <div class="brand">
        <div class="brand-icon">学</div>
        <div class="brand-text">
          <strong>雪峰智慧选课</strong>
          <span>STUDENT PORTAL</span>
        </div>
      </div>

      <div class="sidebar-caption">功能导航</div>

      <template v-for="group in ['学习中心', '智能服务', '账户管理']" :key="group">
        <div class="menu-group">{{ group }}</div>

        <div
            v-for="item in menuItems.filter(item => item.group === group)"
            :key="item.name"
            class="menu-item"
            :class="{ active: currentPage === item.name }"
            @click="currentPage = item.name"
        >
          <span class="menu-icon">{{ item.icon }}</span>
          <span>{{ item.name }}</span>
          <span v-if="currentPage === item.name" class="active-dot"></span>
        </div>
      </template>

      <div class="sidebar-bottom">
        <div class="sidebar-tip">
          <div class="tip-icon">✦</div>
          <strong>雪峰AI助手</strong>
          <p>让智能科技辅助你的学习规划。</p>
          <button @click="currentPage = '雪峰AI选课助手'">
            立即体验 →
          </button>
        </div>
        <div class="sidebar-footer">© 2026 雪峰智慧选课平台</div>
      </div>
    </aside>

    <section class="main-area">
      <header class="topbar">
        <div class="breadcrumb">
          <span>学生工作台</span>
          <span class="separator">/</span>
          <strong>{{ currentPage }}</strong>
        </div>

        <div class="user-area">
          <div class="user-avatar">
            {{ (user.name || user.username || '学').charAt(0) }}
          </div>
          <div class="user-text">
            <strong>{{ user.name || user.username || '学生用户' }}</strong>
            <span>学生账户</span>
          </div>
          <button class="logout-btn" @click="emit('logout')">
            退出登录
          </button>
        </div>
      </header>

      <main class="content">
        <div class="page-heading">
          <div>
            <div class="page-eyebrow">STUDENT WORKSPACE</div>
            <h1>{{ currentPage }}</h1>
            <p>{{ currentDescription }}</p>
          </div>
          <div class="heading-decoration">✦</div>
        </div>

        <section v-if="currentPage === '首页'" class="home-content">
          <div class="welcome-banner">
            <div>
              <span class="welcome-label">WELCOME BACK</span>
              <h2>你好，{{ user.name || user.username || '同学' }}！</h2>
              <p>欢迎回到雪峰智慧选课平台，开启今天的学习。</p>
              <button @click="currentPage = '选课管理'">
                浏览课程 <span>→</span>
              </button>
            </div>
            <div class="banner-decoration">学</div>
          </div>

          <div class="section-title">
            <div>
              <h2>快捷功能</h2>
              <p>选择功能，快速进入对应页面</p>
            </div>
          </div>

          <div class="quick-grid">
            <button class="quick-card" @click="currentPage = '选课管理'">
              <div class="quick-icon blue">▤</div>
              <strong>选课管理</strong>
              <span>浏览课程与管理选课</span>
              <b>进入功能 →</b>
            </button>

            <button class="quick-card" @click="currentPage = '个人课表'">
              <div class="quick-icon purple">▦</div>
              <strong>个人课表</strong>
              <span>查看每周课程安排</span>
              <b>查看课表 →</b>
            </button>

            <button class="quick-card" @click="currentPage = '雪峰AI选课助手'">
              <div class="quick-icon cyan">✦</div>
              <strong>雪峰AI选课助手</strong>
              <span>智能推荐与学习咨询</span>
              <b>体验 AI →</b>
            </button>

            <button class="quick-card" @click="currentPage = '个人中心'">
              <div class="quick-icon orange">◎</div>
              <strong>个人中心</strong>
              <span>查看个人账户资料</span>
              <b>查看资料 →</b>
            </button>
          </div>

          <div class="notice-card">
            <div class="notice-icon">i</div>
            <div>
              <strong>温馨提示</strong>
              <p>选课前请仔细查看课程时间、授课教师及课程容量，避免课程时间冲突。</p>
            </div>
          </div>
        </section>

        <section v-else-if="currentPage === '选课管理'">
          <EnrollmentManagement />
        </section>

        <section v-else-if="currentPage === '个人课表'">
          <StudentSchedule />
        </section>

        <section v-else-if="currentPage === '雪峰AI选课助手'">
          <AiLearningCenter />
        </section>

        <section v-else-if="currentPage === '个人中心'" class="profile-card">
          <div class="profile-heading">
            <div class="profile-avatar">
              {{ (user.name || user.username || '学').charAt(0) }}
            </div>
            <div>
              <h2>{{ user.name || user.username || '学生用户' }}</h2>
              <p>学生账户信息</p>
            </div>
          </div>

          <div class="profile-divider"></div>

          <div class="profile-item">
            <span>登录账号</span>
            <strong>{{ user.username || '暂无信息' }}</strong>
          </div>
          <div class="profile-item">
            <span>用户角色</span>
            <strong>{{ user.role || '暂无信息' }}</strong>
          </div>
          <div class="profile-item">
            <span>学生姓名</span>
            <strong>{{ user.name || '暂未绑定' }}</strong>
          </div>
          <div class="profile-item">
            <span>学号</span>
            <strong>{{ user.studentNo || '暂未绑定' }}</strong>
          </div>
          <div class="profile-item">
            <span>学生ID</span>
            <strong>{{ user.studentId || '暂未绑定' }}</strong>
          </div>
        </section>
      </main>
    </section>
  </div>
</template>

<style scoped>
.student-dashboard {
  width: 100%;
  height: 100vh;
  display: flex;
  overflow: hidden;
  background: #f5f7fb;
  color: #26364a;
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
  padding: 5px 8px 27px;
}

.brand-icon {
  width: 42px;
  height: 42px;
  display: grid;
  place-items: center;
  border-radius: 12px;
  background: linear-gradient(135deg, #64b5ff, #2873d4);
  color: white;
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

.sidebar-caption {
  padding: 15px 12px 10px;
  color: #8198b6;
  font-size: 11px;
  letter-spacing: 1px;
}

.menu-group {
  margin: 18px 12px 8px;
  color: #8198b6;
  font-size: 11px;
}

.menu-item {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 13px 14px;
  margin: 3px 0;
  border-radius: 9px;
  color: #c3d1e4;
  font-size: 13px;
  cursor: pointer;
  transition: 0.2s;
}

.menu-item:hover {
  background: rgba(255,255,255,0.08);
  color: white;
}

.menu-item.active {
  color: white;
  background: linear-gradient(90deg, #2878d0, #2863a8);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.15);
}

.menu-icon {
  width: 19px;
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

.sidebar-tip {
  padding: 16px;
  border: 1px solid rgba(255,255,255,0.1);
  border-radius: 12px;
  background: rgba(255,255,255,0.06);
}

.tip-icon {
  margin-bottom: 10px;
  color: #73c8ff;
  font-size: 23px;
}

.sidebar-tip strong {
  color: white;
  font-size: 13px;
}

.sidebar-tip p {
  margin-top: 8px;
  color: #a8bbd3;
  font-size: 11px;
  line-height: 1.7;
}

.sidebar-tip button {
  margin-top: 12px;
  border: 0;
  color: #8ed0ff;
  background: transparent;
  cursor: pointer;
  font-size: 12px;
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
}

.topbar {
  height: 70px;
  flex-shrink: 0;
  padding: 0 34px;
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.user-avatar,
.profile-avatar {
  display: grid;
  place-items: center;
  flex-shrink: 0;
  border-radius: 50%;
  background: #e5f0ff;
  color: #276bb7;
  font-weight: bold;
}

.user-avatar {
  width: 37px;
  height: 37px;
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

.welcome-banner button {
  margin-top: 20px;
  padding: 10px 18px;
  border: 0;
  border-radius: 7px;
  color: #245b91;
  background: white;
  font-size: 12px;
  cursor: pointer;
}

.welcome-banner button span {
  margin-left: 8px;
}

.banner-decoration {
  position: absolute;
  right: 8%;
  color: rgba(255,255,255,0.10);
  font-size: 155px;
  font-weight: bold;
  transform: rotate(-10deg);
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

.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.quick-card {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-height: 185px;
  padding: 20px;
  border: 1px solid #edf0f5;
  border-radius: 12px;
  background: white;
  text-align: left;
  cursor: pointer;
  transition: 0.2s;
}

.quick-card:hover {
  transform: translateY(-3px);
  border-color: #b8d5f5;
  box-shadow: 0 8px 22px rgba(38, 90, 150, 0.08);
}

.quick-icon {
  width: 39px;
  height: 39px;
  display: grid;
  place-items: center;
  margin-bottom: 15px;
  border-radius: 10px;
  font-size: 21px;
}

.quick-icon.blue { color: #347bc5; background: #eaf3ff; }
.quick-icon.purple { color: #8063c5; background: #f1edff; }
.quick-icon.cyan { color: #2399a8; background: #e6f8f8; }
.quick-icon.orange { color: #d18a36; background: #fff3e4; }

.quick-card strong {
  color: #344258;
  font-size: 14px;
}

.quick-card span {
  margin-top: 8px;
  color: #9aa5b3;
  font-size: 11px;
}

.quick-card b {
  margin-top: auto;
  padding-top: 15px;
  color: #397dc0;
  font-size: 11px;
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

.profile-card {
  max-width: 760px;
  padding: 28px;
  border: 1px solid #edf0f5;
  border-radius: 14px;
  background: white;
}

.profile-heading {
  display: flex;
  align-items: center;
  gap: 17px;
  padding-bottom: 22px;
}

.profile-avatar {
  width: 60px;
  height: 60px;
  font-size: 24px;
}

.profile-heading h2 {
  color: #344258;
  font-size: 19px;
}

.profile-heading p {
  margin-top: 7px;
  color: #9aa5b3;
  font-size: 12px;
}

.profile-divider {
  height: 1px;
  margin-bottom: 8px;
  background: #edf0f5;
}

.profile-item {
  display: flex;
  justify-content: space-between;
  padding: 17px 4px;
  border-bottom: 1px solid #f0f2f5;
  font-size: 13px;
}

.profile-item span {
  color: #8995a5;
}

.profile-item strong {
  color: #35445a;
  font-weight: 500;
}

@media (max-width: 1100px) {
  .quick-grid {
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
  .sidebar-caption,
  .menu-group,
  .menu-item > span:not(.menu-icon),
  .sidebar-bottom {
    display: none;
  }

  .menu-item {
    justify-content: center;
    padding: 14px 5px;
  }

  .menu-icon {
    font-size: 21px;
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

  .page-heading h1 {
    font-size: 21px;
  }

  .heading-decoration {
    display: none;
  }

  .welcome-banner {
    min-height: 190px;
    padding: 24px;
  }

  .banner-decoration {
    right: -10px;
    font-size: 110px;
  }

  .welcome-banner h2 {
    font-size: 20px;
  }

  .quick-grid {
    grid-template-columns: 1fr;
    gap: 11px;
  }

  .quick-card {
    min-height: 155px;
  }

  .profile-card {
    padding: 19px;
  }
}
</style>
