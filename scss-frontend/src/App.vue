<script setup>
import Login from './views/Login.vue' // 路径根据你实际存放的位置调整
</script>
<template>
  <div class="app-container">
    <!-- 1. 顶部 Header -->
    <header class="header">
      <div class="logo-area">
        <img src="@/assets/ph3.png" alt="logo" class="logo-img" /> <!-- 换成你的logo图 -->
        <span class="title">吉首大学 | 统一身份认证</span>
      </div>
      <div class="right-area">
        <span>您正在登录 教务管理系统</span>
        <div class="lang-switch">简体中文</div>
      </div>
    </header>

    <!-- 2. 中间主体区域 (背景图 + 登录框) -->
    <main class="main-content">
      <!-- 这里直接引入你刚才写好的 Login 组件 -->
      <Login />
    </main>

    <!-- 3. 底部 Footer -->
    <footer class="footer">
      吉首大学 
    </footer>
  </div>
</template>
<style>
/* 注意：这里不要加 scoped，因为我们需要重置浏览器的默认边距 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', sans-serif;
}
</style>

<style scoped>
/* 1. 整体容器：Flex纵向排列，高度占满整个视口 (vh) */
.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden; /* 防止出现滚动条 */
}

/* 2. 顶部 Header */
.header {
  height: 60px;
  background-color: #0056b3; /* 参考图的深蓝色 */
  color: white;
  display: flex;
  justify-content: space-between; /* 左右两端对齐 */
  align-items: center;
  padding: 0 40px;
}

/* 3. 中间主体区域 */
.main-content {
  flex: 1; /* 自动撑满剩余高度 */
  /* 重点：设置背景图 */
  background-image: url('@/assets/bg.jpg'); /* 换成你的背景大图路径 */
  background-size: cover; /* 图片铺满不拉伸 */
  background-position: center; /* 图片居中 */
  background-repeat: no-repeat;
  
  /* 布局：让里面的 Login 组件靠右居中 */
  display: flex;
  justify-content: flex-end; /* 靠右 */
  align-items: center; /* 垂直居中 */
  padding-right: 12%; /* 距离右侧的边距，可微调 */
}

/* 4. 底部 Footer */
.footer {
  height: 40px;
  background-color: #0056b3;
  color: white;
  display: flex;
  justify-content: center; /* 文字居中 */
  align-items: center;
  font-size: 12px;
}
</style>






