<template>
  <div class="flex flex-col min-h-[100vh]">
    <header class="px-4 lg:px-6 h-14 flex items-center">
      <RouterLink class="flex items-center justify-center" to="#">
        <!--        <MountainIcon class="h-6 w-6"/>-->
        <svg
          class="w-6 h-6"
          fill="none"
          stroke="currentColor"
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth="2"
          viewBox="0 0 24 24"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path d="M8 3l4 8 5-5 5 15H2L8 3z"/>
        </svg>
        <span class="sr-only">Acme Inc</span>
      </RouterLink>
      <nav class="ml-auto flex gap-4 sm:gap-6">
        <RouterLink class="text-sm font-medium hover:underline" to="/features">Features</RouterLink>
        <RouterLink class="text-sm font-medium hover:underline" to="/pricing">Pricing</RouterLink>
        <RouterLink class="text-sm font-medium hover:underline" to="/about">About</RouterLink>
        <RouterLink class="text-sm font-medium hover:underline" to="/contact">Contact</RouterLink>
      </nav>
    </header>
    <main class="flex-1">
      <!-- Hero Section -->
      <section class="w-full py-12 md:py-24 lg:py-32 xl:py-48">
        <div class="container px-4 md:px-6">
          <div class="flex flex-col items-center space-y-4 text-center">
            <div class="space-y-2">
              <h1 class="text-3xl font-bold tracking-tighter sm:text-4xl md:text-5xl lg:text-6xl">Data Fusion
                Platform</h1>
              <p class="mx-auto max-w-[700px] text-gray-500 md:text-xl lg:text-base">Multi-source heterogeneous data
                aggregation and fusion processing.</p>
            </div>
            <div class="space-x-4">
              <el-button class="inline-flex h-9 items-center justify-center rounded-md bg-blue-500 text-white shadow">
                Get Started
              </el-button>
              <el-button
                class="inline-flex h-9 items-center justify-center rounded-md border border-gray-200 bg-white text-blue-600 shadow-sm hover:bg-blue-100 hover:text-blue-800">
                Learn more
              </el-button>
            </div>
          </div>
        </div>
      </section>
      <!-- About Section -->
      <div class="w-full py-12 md:py-24 lg:py-32 bg-black ">
        <div class="container grid items-center gap-6 px-4 md:px-6">
          <div class="flex flex-col justify-center space-y-4">
            <h2 class="text-3xl font-bold tracking-tighter">About Us</h2>
            <p class="text-gray-500">We are a team of passionate individuals working to make data processing easier and
              more efficient.</p>
          </div>
          <div class="grid grid-cols-2 gap-4">
            <div>
              <h3 class="text-lg font-bold">Our Mission</h3>
              <p class="text-sm">To provide innovative solutions for data aggregation and fusion, empowering businesses
                to make better decisions.</p>
            </div>
            <div>
              <h3 class="text-lg font-bold">Our Vision</h3>
              <p class="text-sm">A world where data is seamlessly integrated and readily available for analysis, driving
                progress and growth.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Services Section -->
      <div class="w-full py-12 md:py-24">
        <div class="container grid items-center gap-6 px-4 md:px-6">
          <h2 class="text-3xl font-bold tracking-tighter mb-6">Our Services</h2>
          <div class="grid grid-cols-3 gap-4">
            <div class="p-4 border border-gray-200 dark:border-gray-800 rounded-md">
              <h3 class="text-lg font-bold">Data Aggregation</h3>
              <p class="text-sm">We gather data from multiple sources and formats, providing a unified view for
                analysis.</p>
            </div>
            <div class="p-4 border border-gray-200 dark:border-gray-800 rounded-md">
              <h3 class="text-lg font-bold">Data Fusion</h3>
              <p class="text-sm">We combine data from various sources to create a comprehensive dataset for your
                business needs.</p>
            </div>
            <div class="p-4 border border-gray-200 dark:border-gray-800 rounded-md">
              <h3 class="text-lg font-bold">Real-time Processing</h3>
              <p class="text-sm">We offer real-time data processing capabilities to ensure you have the latest insights
                at your fingertips.</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Testimonials Section -->
      <div class="w-full py-12 md:py-24 bg-black-200 dark:bg-black-900">
        <div class="container grid items-center gap-6 px-4 md:px-6">
          <h2 class="text-3xl font-bold tracking-tighter mb-6">What Our Clients Say</h2>
          <div class="grid grid-cols-2 gap-4">
            <div class="border border-gray-200 dark:border-gray-800 p-4 rounded-md">
              <el-card class="text-center">
                <el-avatar src="https://via.placeholder.com/100"></el-avatar>
                <h4 class="text-lg font-bold mt-2">John Doe</h4>
                <p class="text-sm text-gray-500">"Their data fusion platform has been a game-changer for our analytics
                  team."</p>
              </el-card>
            </div>
            <div class="border border-gray-200 dark:border-gray-800 p-4 rounded-md">
              <el-card class="text-center">
                <el-avatar src="https://via.placeholder.com/100"></el-avatar>
                <h4 class="text-lg font-bold mt-2">Jane Smith</h4>
                <p class="text-sm text-gray-500">"The real-time processing feature has significantly improved our
                  decision-making process."</p>
              </el-card>
            </div>
          </div>
        </div>
      </div>

      <!-- Call to Action Section -->
      <div class="w-full py-12 md:py-24 border-t">
        <div class="container grid items-center justify-center gap-4 px-4 text-center md:px-6">
          <div class="space-y-3">
            <h2 class="text-3xl font-bold tracking-tighter md:text-4xl/tight">
              Ready to Transform Your Data Workflow?
            </h2>
            <el-form :model="formData" class="flex space-x-2">
              <el-input v-model="formData.email" class="max-w-lg flex-1" placeholder="Enter your email"></el-input>
              <el-button type="primary" @click="submitForm">Sign Up</el-button>
            </el-form>
            <p class="text-xs text-gray-500 dark:text-gray-400">
              Sign up to get notified when we launch.
              <RouterLink class="underline underline-offset-2" to="#">Terms & Conditions</RouterLink>
            </p>
          </div>
        </div>
      </div>
    </main>
    <footer
      class="flex flex-col gap-2 sm:flex-row py-6 w-full shrink-0 items-center px-4 md:px-6 border-t border-gray-200">
      <p class="text-xs text-gray-500">© 2024 Acme Inc. All rights reserved.</p>
      <nav class="sm:ml-auto flex gap-4 sm:gap-6">
        <RouterLink class="text-xs hover:underline" to="/terms">Terms of Service</RouterLink>
        <RouterLink class="text-xs hover:underline" to="/privacy">Privacy</RouterLink>
      </nav>
    </footer>
  </div>
</template>

<script setup>
import {RouterLink} from 'vue-router'
import {ElButton, ElInput} from 'element-plus'
// import MountainIcon from '@/components/ui/MountainIcon'

// 表单数据和提交方法
import {ref} from 'vue'

const formData = ref({})
const email = ref('')

const submitForm = () => {
  // 处理表单提交逻辑
  console.log('Submitting form with email:', email.value)
}
</script>

<style scoped>
/* 你的样式 */
</style>
