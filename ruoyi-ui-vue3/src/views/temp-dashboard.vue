<template>
  <fullPage :options="state.options">
    <div class="section">
      <div class="home-container">
        <!-- 顶部导航栏 -->
        <el-header class="top-nav" style="padding: 0 20px;">
          <el-row>
            <el-col :span="4">
              <img :src="logo" alt="Logo" class="logo"/>
            </el-col>
            <el-col :span="20" class="nav-links">
              <el-menu mode="horizontal">
                <el-menu-item index="1">首页</el-menu-item>
                <el-menu-item index="2">数据汇聚</el-menu-item>
                <el-menu-item index="3">融合处理</el-menu-item>
                <el-menu-item index="4">查询检索</el-menu-item>
                <el-menu-item index="5">智能分析</el-menu-item>
                <el-menu-item index="6">关于我们</el-menu-item>
              </el-menu>
            </el-col>
          </el-row>
        </el-header>
        <el-row :gutter="20">
          <el-col :span="12">
            <!-- 轮播图 -->
            <el-carousel arrow="always" class="carousel">
              <el-carousel-item v-for="(item, index) in carouselList" :key="index">
                <el-image :alt="item.alt" :src="item.image" class="carousel-image"/>
                <div class="carousel-text">{{ item.text }}</div>
              </el-carousel-item>
            </el-carousel>
          </el-col>
          <el-col :span="12" class=" core-features-container">
            <!-- 核心功能介绍 -->
            <el-main class="core-features">
              <el-row :gutter="20">
                <el-col v-for="(feature, index) in features" :key="index" :span="12" class="my-10">
                  <el-card class="feature-card m-auto" shadow="hover">
                    <Icon :icon="feature.icon" class="text-9xl m-auto"/>
                    <div class="feature-title text-2xl text-center">{{ feature.title }}</div>
                    <div class="feature-description">{{ feature.description }}</div>
                  </el-card>
                </el-col>
              </el-row>
            </el-main>
          </el-col>
        </el-row>


        <!-- 数据安全和保密性 -->
        <el-main class="security-section">
          <el-row>
            <el-col :span="24">
              <h2>数据安全与保密</h2>
              <p>我们采用行业领先的加密技术和严格的访问控制，确保您的数据安全和隐私得到最高级别的保护。</p>
            </el-col>
          </el-row>
        </el-main>

        <!-- 用户体验 -->
        <el-main class="user-experience">
          <el-row>
            <el-col :span="24">
              <h2>优化的用户体验</h2>
              <p>我们的平台设计注重用户交互，提供直观的操作界面和流畅的工作流程，让司法数据处理变得简单高效。</p>
            </el-col>
          </el-row>
        </el-main>

        <!-- 底部信息 -->
        <el-footer class="bottom-info">
          <el-row>
            <el-col :span="12">
              <p>联系方式: info@example.com</p>
            </el-col>
            <el-col :span="12">
              <p>版权所有 © 2024 司法数据处理平台</p>
            </el-col>
          </el-row>
        </el-footer>
      </div>
    </div>
    <div class="section">
    </div>
  </fullPage>
</template>

<script setup>
import {reactive, ref} from 'vue';
import logo from '@/assets/logo/logo.png'
import redis from '@/assets/images/dashboard/redis.png'

import {Icon} from "@iconify/vue";


const state = reactive({
  options: {
    // licenseKey: 'OPEN-SOURCE-GPLV3-LICENSE',// 购买了key可以填，
    menu: '#menu',
    anchors: ['page1', 'page2', 'page3', 'page4', 'page5', 'page6'], // 每一个分页的id，页面滚动时可观察地址栏发生变化

    // 下面这两个配置是开始视差滚动效果的，但怎么试都没开启成功
    //parallax: true,
    //parallaxOptions: {
    //type: 'reveal',
    //percentage: 62,
    //property: 'translate'
    //}
  },
})

const carouselList = ref([
  // ...轮播图数据
  {
    image: redis,
    alt: '轮播图1',
    text: '数据汇聚与处理'
  },
  {
    image: 'https://example.com/carousel2.jpg',
    alt: '轮播图2',
    text: '数据查询与检索'
  },
  {
    image: 'https://example.com/carousel3.jpg',
    alt: '轮播图3',
    text: '智能分析与决策'
  }
]);

const features = ref([
  {title: '数据汇聚', description: '高效整合多源司法数据', icon: 'eos-icons:big-data'},
  {title: '融合处理', description: '自动化处理与分析司法数据', icon: 'logos:bluemix'},
  {title: '查询检索', description: '快速准确的数据检索系统', icon: 'logos:elasticsearch'},
  {title: '智能分析', description: '利用人工智能技术进行深度数据分析', icon: 'arcticons:openai-chatgpt'},
  // ...更多功能特点
]);

// ...其他代码
</script>

<!--<style scoped>-->
<!--/* ...之前的样式 */-->
<!--.carousel {-->
<!--  height: 100%;-->
<!--}-->

<!--.carousel-image {-->
<!--  width: 100%;-->
<!--  height: 90%;-->
<!--}-->

<!--.core-features {-->
<!--  background-color: #f3f3f3;-->
<!--}-->

<!--.security-section, .user-experience {-->
<!--  background-color: #fff;-->
<!--  padding: 20px;-->
<!--  margin-bottom: 20px;-->
<!--}-->

<!--.security-section h2, .user-experience h2 {-->
<!--  color: #333;-->
<!--}-->

<!--.security-section p, .user-experience p {-->
<!--  color: #666;-->
<!--}-->

<!--.bottom-info {-->
<!--  background-color: #545c64;-->
<!--  color: white;-->
<!--  text-align: center;-->
<!--  padding: 20px 0;-->
<!--  font-size: 14px;-->
<!--}-->

<!--/* 添加一些科技风格的配色 */-->
<!--.top-nav {-->
<!--}-->

<!--.nav-links {-->
<!--  color: white;-->
<!--}-->

<!--.logo {-->
<!--  width: 64px;-->
<!--  height: auto;-->
<!--}-->

<!--.feature-card {-->
<!--  display: flex;-->
<!--  flex-direction: row;-->
<!--  align-items: center;-->
<!--  justify-content: center;-->
<!--  height: 340px;-->
<!--  width: 340px;-->
<!--  background-color: #0c0d0e;-->
<!--  background-image: linear-gradient(to right, #ebeef5, #d8e3f6);-->
<!--  border-color: #ebeef5;-->
<!--  color: #2d8cf0;-->
<!--}-->

<!--.feature-card:hover {-->
<!--  background-color: #d8e3f6;-->
<!--  border-color: #d8e3f6;-->
<!--}-->

<!--.icon {-->
<!--  font-size: 48px;-->
<!--  margin-bottom: 15px;-->
<!--}-->

<!--/* ...其他样式 */-->
<!--</style>-->
<style scoped>
/* 更新配色方案 */
:root {
  --primary-color: #0c0d0e;
  --secondary-color: #2d8cf0;
  --tertiary-color: #ebeef5;
  --quaternary-color: #d8e3f6;
  --text-color: #333;
  --text-color-light: #666;
}

/* 更新字体 */
body {
  font-family: 'Open Sans', sans-serif;
}

/* 顶部导航栏样式 */
.top-nav {
  background-color: var(--primary-color);
  color: white;
  padding: 10px 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 轮播图样式 */
.carousel-container {
  overflow: hidden;
  border-radius: 4px;
}

.carousel {
  transition: transform 0.5s ease-in-out;
}

.carousel-image {
  display: block;
  width: 100%;
  height: auto;
}

.carousel-text {
  position: absolute;
  bottom: 20px;
  left: 20px;
  background-color: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 1.2rem;
}

/* 核心功能样式 */
.core-features-container {
  background-color: var(--tertiary-color);
  padding: 40px 0;
}

.feature-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  border-radius: 4px;
  overflow: hidden;
  margin: 0 auto;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

/* 数据安全和保密性样式 */
.security-section, .user-experience {
  background-color: white;
  padding: 40px;
  margin-bottom: 40px;
}

/* 底部信息样式 */
.bottom-info {
  background-color: var(--primary-color);
  color: white;
  text-align: center;
  padding: 20px 0;
  font-size: 0.9rem;
}

/* 更新图标样式 */
.icon {
  font-size: 3rem;
  margin-bottom: 20px;
  color: var(--secondary-color);
}

/* 其他样式调整 */
/* ...其他样式... */
</style>
