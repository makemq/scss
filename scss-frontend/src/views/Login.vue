<template>
  <div class="login_card">
    <div class="card-header">
      <!-- 修复1：去掉了尖括号，加上自闭合 / -->
      <img src="@/assets/ph2.png" />
      <span>统一身份认证</span>
    </div>
    
    <div class="tabs">
      <span :class="{ active: activeTab === 'password' }" @click="activeTab = 'password'">密码</span>
    </div>
    
    <form class="login-form">
      <div class="input-group">
        <i class="icon-user"></i>
        <input type="text" v-model="form.username" placeholder="请输入学号/工号" />
      </div>
      
      <div class="input-group">
        <i class="icon-lock"></i>
        <input :type="showPassword ? 'text' : 'password'" v-model="form.password" placeholder="请输入密码" />
        <i class="icon-eye" @click="showPassword = !showPassword"></i>
      </div>
      
      <div class="form-options"> 
        <label><input type="checkbox" v-model="form.rememberMe" /> 7天免登录</label>
        <!-- 修复2：去掉了多余的 = 号 -->
        <div class="links">
           <a href="#">注册账号</a> | <a href="#">忘记密码</a>
        </div>
      </div>
      
      <button type="button" @click="handleLogin">登 录</button>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'

const activeTab = ref('password')
const showPassword = ref(false)
const form = reactive({
  username: '',
  password: '',
  rememberMe: false
})

const handleLogin = () => {
  if (!form.username || !form.password) {
    alert('请输入账号和密码')
    return
  }
  console.log('提交的数据：', form)
} 
</script> <!-- 修复4：补上了 script 标签的闭合 -->

<style scoped>
/* 卡片外层 */
.login_card {
  width: 380px;
  background: rgba(255, 255, 255, 0.6);
  padding: 40px 30px;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px); 
  -webkit-backdrop-filter: blur(10px);
}

/* 顶部 Logo 区域 */
.card-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 30px;
  gap: 10px;
}
.card-header img {
  height: 40px; 
  object-fit: contain;
}
.card-header span {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

/* Tabs 标签页 */
.tabs {
  display: flex;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}
.tabs span {
  padding: 10px 15px;
  cursor: pointer;
  color: #666;
  font-size: 14px;
  position: relative;
}
.tabs span.active {
  color: #0056b3;
  font-weight: bold;
}
.tabs span.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: #0056b3;
}

/* 输入框组 */
.input-group {
  display: flex;
  align-items: center;
  border: 1px solid #ddd;
  border-radius: 4px;
  margin-bottom: 15px;
  padding: 0 10px;
  background: #fff;
}
.input-group i {
  color: #999;
  margin-right: 8px;
  font-size: 16px;
}
.input-group input {
  flex: 1;
  border: none;
  outline: none;
  padding: 12px 0;
  font-size: 14px;
}
.input-group .icon-eye {
  cursor: pointer;
  margin-right: 0;
  margin-left: 8px;
}

/* 底部选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  margin-bottom: 20px;
  color: #666;
}
.form-options label {
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 5px;
}
.links a {
  color: #0056b3;
  text-decoration: none;
}
.links a:hover {
  text-decoration: underline;
}

/* 登录按钮 */
button {
  width: 100%;
  padding: 12px;
  background-color: #0056b3;
  color: white;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  cursor: pointer;
  transition: background 0.3s;
}
button:hover {
  background-color: #004494;
}
</style>