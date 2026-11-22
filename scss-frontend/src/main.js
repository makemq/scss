import { createApp } from 'vue'
import App from './App.vue'
import { setupAxiosInterceptors } from './utils/axiosInterceptor'
import './assets/main.css'
setupAxiosInterceptors()
createApp(App).mount('#app')
//注册拦截器






