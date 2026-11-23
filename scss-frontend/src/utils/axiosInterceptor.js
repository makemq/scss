import axios from 'axios'
// 创建用于Token刷新的Axios
const refreshAxios = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 10000
})
//标记当前是否正在刷新Token
let isRefreshing = false
//保存等待刷新完成的请求
let requests = []
//安装拦截器
export function setupAxiosInterceptors() {
// 请求拦截器
    axios.interceptors.request.use(
        config => {
            const accessToken = localStorage.getItem('accessToken')
            if (accessToken) {
                //确保headers存在
                config.headers = config.headers || {}
                config.headers.Authorization = `Bearer ${accessToken}`
            }
            return config
        }
    )
    axios.interceptors.response.use(
        response => {
            return response
        }, error => {
            const originalRequest = error.config//获取刚才的请求配置
            //确保不会反复刷新 Token
            if (error.response?.status === 401 && originalRequest &&!originalRequest._retry) {
                originalRequest._retry = true//请求重式
                const refreshToken = localStorage.getItem('refreshToken')
                //请求不存在则清除登录信息
                if (!refreshToken) {
                    localStorage.removeItem('accessToken')
                    localStorage.removeItem('refreshToken')
                    window.location.reload()
                    return Promise.reject(error)
                }
                //如果已经有请求正在刷新 Token当前请求先等待
                if (isRefreshing) {
                    return new Promise((resolve, reject) => {
                        requests.push({
                            resolve,
                            reject,
                            originalRequest
                        })
                    })
                }
                //标记当前开始刷新 Token
                isRefreshing = true
                //使用刷新token,请求新的访问token
                return refreshAxios.post('/user/refresh', {
                    refreshToken: refreshToken
                }).then(response => {//重新发请求
                        //获取后端返回的新访问Token
                        const newAccessToken = response.data.data.accessToken
                        //保存新的访问Token
                        localStorage.setItem('accessToken', newAccessToken)
                        //修改原请求的请求头,加确保存在
                        originalRequest.headers = originalRequest.headers || {}
                        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`
                        //先保存再清空等待队列
                        const pendingRequests = requests
                        requests = []
                        //逐个发送
                        pendingRequests.forEach(request => {
                        request.originalRequest.headers = request.originalRequest.headers || {}
                            request.originalRequest.headers.Authorization =
                            `Bearer ${newAccessToken}`
                        request.resolve(axios(request.originalRequest))
                    })
                        // 重置刷新状态，允许下一次刷新
                        isRefreshing = false
                        //使用新Token重新发送刚才失败的请求
                        return axios(originalRequest)
                    }, error => {//处理刷新失败
                        // 通知所有等待中的请求：刷新失败
                        requests.forEach(request => {
                            request.reject(error)
                        })
                        // 清空等待队列
                        requests = []
                        // 重置刷新状态
                        isRefreshing = false
                        //刷新失败，清除登录信息
                        localStorage.removeItem('accessToken')
                        localStorage.removeItem('refreshToken')
                        //刷新页面，要求用户重新登录
                        window.location.reload()
                        return Promise.reject(error)
                    }
                )
            }
            return Promise.reject(error)
        }
    )
}