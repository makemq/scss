
<script setup>
import { ref } from 'vue'
import axios from 'axios'

const activeFeature = ref('recommend')
const switchFeature = (key) => {
  activeFeature.value = key
  errorMessage.value = ''
  chatError.value = ''
}
const features = [
  {
    key: 'recommend',
    title: 'AI 智能课程推荐',
    description: '根据兴趣、学习目标和修读要求，为你推荐合适的选修课程。',
    icon: '🎯'
  },
  {
    key: 'chat',
    title: 'AI 对话助手',
    description: '随时询问课程、学分、选课规则和学习问题。',
    icon: '💬'
  },
  {
    key: 'progress',
    title: '修读进度分析',
    description: '查看各类课程学分、已完成课程和待出成绩课程。',
    icon: '📊'
  },
  {
    key: 'requirements',
    title: '修读要求检查',
    description: '检查社科、自然、艺体、民族学分及线下课程要求。',
    icon: '✅'
  }
]

const form = ref({
  interest: '',
  learningGoal: '',
  teacher: '',
  courseType: '',
  teachingMode: ''
})

const loading = ref(false)
const errorMessage = ref('')
const recommendResult = ref(null)

const recommendCourses = async () => {
  if (!form.value.interest.trim() && !form.value.learningGoal.trim()) {
    errorMessage.value = '请至少填写兴趣领域或学习目标'
    return
  }

  loading.value = true
  errorMessage.value = ''
  recommendResult.value = null

  try {
    const response = await axios.post(
        'http://localhost:8080/ai/course/recommend',
        {
          interest: form.value.interest.trim(),
          learningGoal: form.value.learningGoal.trim(),
          teacher: form.value.teacher.trim(),
          courseType: form.value.courseType,
          teachingMode: form.value.teachingMode
        }
    )

    if (response.data.code === 200) {
      recommendResult.value = response.data.data
    } else {
      errorMessage.value = response.data.message || '课程推荐失败'
    }
  } catch (error) {
    errorMessage.value =
        error.response?.data?.message || 'AI 推荐服务暂时不可用，请稍后重试'
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  form.value = {
    interest: '',
    learningGoal: '',
    teacher: '',
    courseType: '',
    teachingMode: ''
  }
  recommendResult.value = null
  errorMessage.value = ''
}

const formatDay = (day) => {
  if (!day) return '暂未安排'
  return `星期${['一', '二', '三', '四', '五', '六', '日'][day - 1] || day}`
}
// AI 对话状态
const chatInput = ref('')
const chatMessages = ref([])
const chatLoading = ref(false)
const chatError = ref('')

// 发送 AI 消息
const sendChatMessage = async () => {
  const message = chatInput.value.trim()

  if (!message) {
    chatError.value = '请输入你想咨询的问题'
    return
  }

  if (message.length > 1000) {
    chatError.value = '问题不能超过1000个字符'
    return
  }

  chatError.value = ''
  chatLoading.value = true

  // 添加学生消息
  chatMessages.value.push({
    role: 'user',
    content: message
  })

  chatInput.value = ''

  try {
    const response = await axios.post(
        'http://localhost:8080/ai/chat',
        {
          message: message
        }
    )

    if (response.data.code === 200) {
      chatMessages.value.push({
        role: 'assistant',
        content: response.data.data.answer
      })
    } else {
      chatError.value = response.data.message || 'AI 回复失败'
    }
  } catch (error) {
    chatError.value =
        error.response?.data?.message || 'AI 服务暂时不可用，请稍后重试'
  } finally {
    chatLoading.value = false
  }
}
</script>

<template>
  <div class="ai-center">
    <section class="hero">
      <div>
        <div class="eyebrow">XUEFENG AI ASSISTANT</div>
        <h1>雪峰AI选课助手</h1>
        <p>让 AI 帮助你了解课程、规划学习方向、掌握修读进度。</p>
      </div>
      <div class="hero-icon">✦</div>
    </section>

    <section class="feature-section">
      <div class="section-heading">
        <h2>智能学习服务</h2>
        <span>选择一个功能开始使用</span>
      </div>

      <div class="feature-grid">
        <button
            type="button"
            v-for="feature in features"
            :key="feature.key"
            class="feature-card"
            :class="{ active: activeFeature === feature.key }"
            @click.stop.prevent="switchFeature(feature.key)"
        >
          <div class="feature-icon">{{ feature.icon }}</div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
          <span class="feature-action">进入功能 →</span>
        </button>
      </div>
    </section>

    <!-- AI 课程推荐 -->
    <section v-if="activeFeature === 'recommend'" class="recommend-card">
      <div class="section-heading">
        <div>
          <h2>🎯 AI 智能课程推荐</h2>
          <p>告诉 AI 你的兴趣和目标，为你寻找合适的课程</p>
        </div>
      </div>

      <div class="form-grid">
        <div class="form-item">
          <label>兴趣领域</label>
          <input
              v-model="form.interest"
              type="text"
              placeholder="例如：人工智能、计算机技术、经济学"
          />
        </div>

        <div class="form-item">
          <label>学习目标</label>
          <input
              v-model="form.learningGoal"
              type="text"
              placeholder="例如：提高编程能力、拓展知识面"
          />
        </div>

        <div class="form-item">
          <label>指定教师（选填）</label>
          <input
              v-model="form.teacher"
              type="text"
              placeholder="输入教师姓名"
          />
        </div>

        <div class="form-item">
          <label>课程性质</label>
          <select v-model="form.courseType">
            <option value="">不限</option>
            <option value="社科">社科</option>
            <option value="自然">自然</option>
            <option value="艺体">艺体</option>
            <option value="民族">民族</option>
          </select>
        </div>

        <div class="form-item">
          <label>授课方式</label>
          <select v-model="form.teachingMode">
            <option value="">不限</option>
            <option value="OFFLINE">线下</option>
            <option value="ONLINE">线上</option>
          </select>
        </div>
      </div>

      <div class="form-actions">
        <button class="reset-button" @click="resetForm" :disabled="loading">
          重置
        </button>
        <button
            class="submit-button"
            @click="recommendCourses"
            :disabled="loading"
        >
          {{ loading ? 'AI 正在分析...' : '✦ 开始智能推荐' }}
        </button>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <strong>雪峰 AI 正在为你分析课程</strong>
        <span>正在结合你的学习进度和修读要求进行推荐，请稍候...</span>
      </div>

      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <div v-if="recommendResult" class="result-section">
        <div class="result-heading">
          <div>
            <span class="result-tag">AI ANALYSIS RESULT</span>
            <h3>推荐结果</h3>
          </div>
          <span class="result-count">
            {{ recommendResult.courses?.length || 0 }} 门推荐课程
          </span>
        </div>

        <div class="reason-card">
          <strong>✦ AI 推荐理由</strong>
          <p>{{ recommendResult.reason || '暂无推荐理由' }}</p>
        </div>

        <div
            v-if="!recommendResult.courses?.length"
            class="empty-result"
        >
          <div>📚</div>
          <strong>暂时没有合适的推荐课程</strong>
          <span>可以尝试调整兴趣、学习目标或筛选条件。</span>
        </div>

        <div v-else class="course-grid">
          <article
              v-for="course in recommendResult.courses"
              :key="course.id"
              class="course-card"
          >
            <div class="course-card-top">
              <span class="course-label">AI 推荐课程</span>
              <span class="course-credit">{{ course.credit }} 学分</span>
            </div>

            <h4>{{ course.courseName }}</h4>

            <div class="course-info">
              <div>
                <span>授课教师</span>
                <strong>{{ course.teacher || '暂无信息' }}</strong>
              </div>
              <div>
                <span>上课时间</span>
                <strong>
                  {{ formatDay(course.classDay) }}
                  {{ course.startSection }}-{{ course.endSection }} 节
                </strong>
              </div>
              <div>
                <span>课程类别</span>
                <strong>{{ course.courseType || '暂无信息' }}</strong>
              </div>
              <div>
                <span>授课方式</span>
                <strong>
                  {{ course.teachingMode === 'OFFLINE' ? '线下' :
                    course.teachingMode === 'ONLINE' ? '线上' :
                        course.teachingMode || '暂无信息' }}
                </strong>
              </div>
              <div>
                <span>课程容量</span>
                <strong>{{ course.selectedCount }} / {{ course.capacity }}</strong>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>

    <!-- AI 智能对话 -->
    <section v-else-if="activeFeature === 'chat'" class="chat-card">
      <div class="chat-header">
        <div>
          <h2>💬 雪峰 AI 对话助手</h2>
          <p>你可以向我咨询课程、学分、选课规则和学习规划</p>
        </div>
        <span class="ai-status">
      <span class="status-dot"></span>
      AI 在线
    </span>
      </div>

      <div class="chat-messages">
        <div v-if="chatMessages.length === 0" class="chat-welcome">
          <div class="welcome-icon">✦</div>
          <h3>你好，我是雪峰 AI！</h3>
          <p>有什么学习或选课问题，都可以问我。</p>

          <div class="quick-questions">
            <button
                @click="chatInput = '请帮我分析当前的学习进度'"
            >
              📊 分析我的学习进度
            </button>
            <button
                @click="chatInput = '请推荐适合我的课程'"
            >
              🎯 推荐适合我的课程
            </button>
            <button
                @click="chatInput = '选课时有哪些注意事项？'"
            >
              📚 选课注意事项
            </button>
          </div>
        </div>

        <div
            v-for="(message, index) in chatMessages"
            :key="index"
            class="chat-message"
            :class="message.role"
        >
          <div class="message-avatar">
            {{ message.role === 'user' ? '我' : '✦' }}
          </div>

          <div class="message-content">
            <div class="message-name">
              {{ message.role === 'user' ? '我' : '雪峰 AI' }}
            </div>
            <div class="message-bubble">
              {{ message.content }}
            </div>
          </div>
        </div>

        <div v-if="chatLoading" class="chat-message assistant">
          <div class="message-avatar">✦</div>
          <div class="message-content">
            <div class="message-name">雪峰 AI</div>
            <div class="message-bubble typing">
              正在思考，请稍候...
            </div>
          </div>
        </div>
      </div>

      <div v-if="chatError" class="chat-error">
        {{ chatError }}
      </div>

      <div class="chat-input-area">
    <textarea
        v-model="chatInput"
        placeholder="请输入你想咨询的问题..."
        maxlength="1000"
        rows="3"
        :disabled="chatLoading"
        @keydown.enter.exact.prevent="sendChatMessage"
    ></textarea>

        <div class="chat-input-bottom">
          <span>{{ chatInput.length }} / 1000 · Enter 发送</span>

          <button
              class="chat-send-button"
              @click="sendChatMessage"
              :disabled="chatLoading || !chatInput.trim()"
          >
            {{ chatLoading ? '发送中...' : '发送 ↗' }}
          </button>
        </div>
      </div>
    </section>

    <!-- 其他功能暂时保留入口 -->
    <section v-else class="intro-card">
      <div>
        <h2>{{ features.find(item => item.key === activeFeature)?.title }}</h2>
        <p>{{ features.find(item => item.key === activeFeature)?.description }}</p>
        <div class="coming-soon">该功能即将接入</div>
      </div>
      <div class="intro-decoration">AI</div>
    </section>
  </div>
</template>

<style scoped>
.ai-center {
  display: flex;
  flex-direction: column;
  gap: 26px;
}

.hero {
  min-height: 190px;
  padding: 34px 40px;
  border-radius: 16px;
  background: linear-gradient(120deg, #173f75, #3978bd);
  color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  overflow: hidden;
}

.eyebrow {
  font-size: 12px;
  letter-spacing: 2px;
  opacity: 0.75;
  margin-bottom: 14px;
}

.hero h1 {
  font-size: 28px;
  margin-bottom: 12px;
}

.hero p {
  color: rgba(255,255,255,0.85);
  font-size: 14px;
}

.hero-icon {
  font-size: 90px;
  opacity: 0.75;
  padding-right: 25px;
}

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.section-heading h2 {
  font-size: 20px;
}

.section-heading p {
  margin-top: 8px;
  color: #8993a3;
  font-size: 13px;
}

.section-heading span {
  color: #8993a3;
  font-size: 13px;
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.feature-card {
  text-align: left;
  padding: 23px;
  min-height: 190px;
  background: white;
  border: 1px solid #e9edf3;
  border-radius: 12px;
  cursor: pointer;
  transition: 0.2s;
}

.feature-card:hover,
.feature-card.active {
  border-color: #4385d0;
  box-shadow: 0 5px 20px rgba(45, 105, 175, 0.1);
  transform: translateY(-2px);
}

.feature-icon {
  font-size: 28px;
  margin-bottom: 13px;
}

.feature-card h3 {
  color: #26364a;
  font-size: 17px;
  margin-bottom: 10px;
}

.feature-card p {
  color: #7b8796;
  font-size: 13px;
  line-height: 1.7;
}

.feature-action {
  display: inline-block;
  margin-top: 15px;
  color: #3478bd;
  font-size: 13px;
}

.recommend-card,
.intro-card {
  padding: 25px 30px;
  background: white;
  border: 1px solid #e9edf3;
  border-radius: 12px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-item label {
  color: #42546c;
  font-size: 13px;
  font-weight: 600;
}

.form-item input,
.form-item select {
  width: 100%;
  height: 43px;
  padding: 0 12px;
  border: 1px solid #dfe6ef;
  border-radius: 8px;
  outline: none;
  color: #34465e;
  background: #fff;
  font-size: 13px;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-item input:focus,
.form-item select:focus {
  border-color: #4385d0;
  box-shadow: 0 0 0 3px rgba(67, 133, 208, 0.1);
}

.form-item input::placeholder {
  color: #b0bac7;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 23px;
}

.reset-button,
.submit-button {
  padding: 11px 19px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  transition: 0.2s;
}

.reset-button {
  color: #64748b;
  background: white;
  border: 1px solid #dfe6ef;
}

.submit-button {
  color: white;
  background: linear-gradient(110deg, #438be2, #3471c5);
  border: 0;
  box-shadow: 0 4px 10px rgba(54, 119, 201, 0.18);
}

.submit-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 6px 14px rgba(54, 119, 201, 0.25);
}

.reset-button:disabled,
.submit-button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 15px 12px;
  color: #6f829b;
  text-align: center;
}

.loading-spinner {
  width: 31px;
  height: 31px;
  margin-bottom: 14px;
  border: 3px solid #e5eef9;
  border-top-color: #4388df;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.loading-state strong {
  color: #3c5d83;
  font-size: 14px;
}

.loading-state span {
  margin-top: 8px;
  color: #9aa7b8;
  font-size: 12px;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.error-message {
  margin-top: 18px;
  padding: 13px 15px;
  color: #c14e4e;
  background: #fff2f2;
  border: 1px solid #f8dada;
  border-radius: 8px;
  font-size: 13px;
}

.result-section {
  margin-top: 28px;
  padding-top: 23px;
  border-top: 1px solid #edf0f5;
}

.result-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 17px;
}

.result-tag {
  color: #4787c9;
  font-size: 10px;
  letter-spacing: 1.5px;
}

.result-heading h3 {
  margin-top: 7px;
  color: #30445f;
  font-size: 19px;
}

.result-count {
  padding: 7px 12px;
  border-radius: 20px;
  color: #3478bd;
  background: #edf5ff;
  font-size: 12px;
}

.reason-card {
  padding: 17px 19px;
  margin-bottom: 18px;
  border: 1px solid #e4eefb;
  border-radius: 10px;
  background: #f4f8fe;
}

.reason-card strong {
  color: #3b6fa9;
  font-size: 13px;
}

.reason-card p {
  margin-top: 9px;
  color: #61758e;
  font-size: 13px;
  line-height: 1.8;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 15px;
}

.course-card {
  padding: 19px;
  border: 1px solid #e8edf4;
  border-radius: 11px;
  background: #fff;
  transition: 0.2s;
}

.course-card:hover {
  border-color: #b9d5f3;
  box-shadow: 0 5px 17px rgba(45, 105, 175, 0.07);
}

.course-card-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.course-label {
  color: #3478bd;
  font-size: 11px;
}

.course-credit {
  padding: 5px 9px;
  color: #3478bd;
  background: #edf5ff;
  border-radius: 6px;
  font-size: 11px;
}

.course-card h4 {
  margin: 15px 0;
  color: #2f435e;
  font-size: 17px;
}

.course-info {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.course-info div {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.course-info span {
  color: #9aa5b3;
  font-size: 11px;
}

.course-info strong {
  color: #566b84;
  font-size: 12px;
  font-weight: 600;
}

.empty-result {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 32px 15px;
  color: #9aa7b8;
  text-align: center;
}

.empty-result div {
  margin-bottom: 10px;
  font-size: 33px;
}

.empty-result strong {
  color: #60748e;
  font-size: 14px;
}

.empty-result span {
  margin-top: 8px;
  font-size: 12px;
}

.intro-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.intro-card h2 {
  font-size: 19px;
  margin-bottom: 10px;
}

.intro-card p {
  color: #7b8796;
  font-size: 13px;
}

.coming-soon {
  display: inline-block;
  margin-top: 17px;
  padding: 6px 12px;
  border-radius: 20px;
  background: #edf4fc;
  color: #3478bd;
  font-size: 12px;
}

.intro-decoration {
  font-size: 52px;
  font-weight: bold;
  color: #dce9f8;
}

@media (max-width: 700px) {
  .feature-grid,
  .course-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .hero {
    padding: 25px;
  }

  .hero-icon {
    display: none;
  }

  .section-heading {
    align-items: flex-start;
    gap: 8px;
    flex-direction: column;
  }

  .recommend-card,
  .intro-card {
    padding: 20px 16px;
  }

  .form-actions {
    flex-direction: column-reverse;
  }

  .form-actions button {
    width: 100%;
  }

  .result-heading {
    align-items: flex-start;
    gap: 12px;
    flex-direction: column;
  }
}
/* ==============================
   AI 智能对话界面
============================== */

.chat-card {
  padding: 26px 30px;
  background: #ffffff;
  border: 1px solid #e9edf3;
  border-radius: 14px;
  min-height: 560px;
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #edf0f5;
}

.chat-header h2 {
  color: #30445f;
  font-size: 20px;
}

.chat-header p {
  margin-top: 8px;
  color: #8993a3;
  font-size: 13px;
}

.ai-status {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 7px 12px;
  border-radius: 20px;
  color: #26966c;
  background: #eaf8f1;
  font-size: 12px;
  white-space: nowrap;
}

.status-dot {
  width: 7px;
  height: 7px;
  border-radius: 50%;
  background: #35b982;
}

.chat-messages {
  flex: 1;
  min-height: 320px;
  max-height: 520px;
  overflow-y: auto;
  padding: 25px 5px;
}

.chat-welcome {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 45px 15px 25px;
  text-align: center;
}

.welcome-icon {
  width: 58px;
  height: 58px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 17px;
  border-radius: 18px;
  color: white;
  background: linear-gradient(135deg, #438be2, #285ba5);
  font-size: 30px;
}

.chat-welcome h3 {
  color: #30445f;
  font-size: 19px;
}

.chat-welcome p {
  margin-top: 9px;
  color: #8993a3;
  font-size: 13px;
}

.quick-questions {
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 25px;
}

.quick-questions button {
  padding: 10px 14px;
  border: 1px solid #e1eaf5;
  border-radius: 9px;
  background: #f7faff;
  color: #4b6d94;
  font-size: 12px;
  cursor: pointer;
  transition: 0.2s;
}

.quick-questions button:hover {
  border-color: #4385d0;
  color: #3478bd;
  background: #edf5ff;
}

.chat-message {
  display: flex;
  align-items: flex-start;
  gap: 11px;
  margin-bottom: 23px;
}

.chat-message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 11px;
  color: white;
  background: linear-gradient(135deg, #438be2, #285ba5);
  font-size: 13px;
  font-weight: 600;
}

.chat-message.user .message-avatar {
  background: #e9eff8;
  color: #55769e;
}

.message-content {
  max-width: 78%;
}

.chat-message.user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-name {
  margin-bottom: 7px;
  color: #8993a3;
  font-size: 11px;
}

.message-bubble {
  padding: 12px 15px;
  border-radius: 4px 12px 12px 12px;
  background: #f2f6fc;
  color: #42546c;
  font-size: 13px;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

.chat-message.user .message-bubble {
  border-radius: 12px 4px 12px 12px;
  background: #eaf3ff;
  color: #315f94;
}

.message-bubble.typing {
  color: #8295ac;
}

.chat-error {
  margin-bottom: 12px;
  padding: 11px 14px;
  border-radius: 8px;
  color: #c14e4e;
  background: #fff2f2;
  font-size: 12px;
}

.chat-input-area {
  padding: 15px;
  border: 1px solid #dfe7f1;
  border-radius: 12px;
  background: #fafcff;
}

.chat-input-area textarea {
  width: 100%;
  resize: vertical;
  min-height: 65px;
  max-height: 150px;
  border: none;
  outline: none;
  background: transparent;
  color: #34465e;
  font-family: inherit;
  font-size: 13px;
  line-height: 1.7;
}

.chat-input-area textarea::placeholder {
  color: #aab5c3;
}

.chat-input-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.chat-input-bottom span {
  color: #a0aaba;
  font-size: 11px;
}

.chat-send-button {
  padding: 9px 20px;
  border: none;
  border-radius: 8px;
  color: white;
  background: linear-gradient(110deg, #438be2, #3471c5);
  font-size: 12px;
  cursor: pointer;
  transition: 0.2s;
}

.chat-send-button:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(54, 119, 201, 0.2);
}

.chat-send-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 手机端适配 */
@media (max-width: 700px) {
  .chat-card {
    padding: 20px 15px;
  }

  .chat-header {
    align-items: flex-start;
    gap: 12px;
  }

  .chat-header h2 {
    font-size: 17px;
  }

  .chat-header p {
    line-height: 1.6;
  }

  .message-content {
    max-width: 85%;
  }

  .quick-questions {
    flex-direction: column;
    width: 100%;
  }

  .quick-questions button {
    width: 100%;
  }
}
</style>
