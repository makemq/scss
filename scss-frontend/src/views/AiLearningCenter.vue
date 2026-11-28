
<script setup>
import { ref } from 'vue'

const activeFeature = ref('recommend')

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
            v-for="feature in features"
            :key="feature.key"
            class="feature-card"
            :class="{ active: activeFeature === feature.key }"
            @click="activeFeature = feature.key"
        >
          <div class="feature-icon">{{ feature.icon }}</div>
          <h3>{{ feature.title }}</h3>
          <p>{{ feature.description }}</p>
          <span class="feature-action">进入功能 →</span>
        </button>
      </div>
    </section>

    <section class="intro-card">
      <div>
        <h2>{{ features.find(item => item.key === activeFeature)?.title }}</h2>
        <p>{{ features.find(item => item.key === activeFeature)?.description }}</p>
        <div class="coming-soon">功能页面正在接入</div>
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

.intro-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  background: white;
  border: 1px solid #e9edf3;
  border-radius: 12px;
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
  .feature-grid {
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
}
</style>
