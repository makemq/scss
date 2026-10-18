<script setup>
import { ref } from 'vue'
import axios from 'axios'

const backendMessage = ref('')
const loading = ref(false)

const callBackend = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/test')
    backendMessage.value = response.data
  } catch (error) {
    console.error(error)
    backendMessage.value = '请求失败，请检查后端是否启动，或F12查看控制台报错'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div style="padding: 50px; text-align: center;">
    <h1>学生选课管理系统 - 联调测试</h1>
    <button
        @click="callBackend"
        :disabled="loading"
        style="padding: 10px 20px; font-size: 16px; cursor: pointer;"
    >
      {{ loading ? '请求中...' : '点击测试连接后端' }}
    </button>

    <p style="margin-top: 20px; font-size: 18px; color: green; font-weight: bold;">
      {{ backendMessage }}
    </p>
  </div>
</template>

