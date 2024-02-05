<template>
  <router-view/>
</template>

<script setup>
import useSettingsStore from '@/store/modules/settings'
import {handleThemeStyle} from '@/utils/theme'

onMounted(() => {
  nextTick(() => {
    handleThemeStyle(useSettingsStore().theme)
  })
})
</script>
<style lang="scss">

/* 定义滚动条整体样式 */
::-webkit-scrollbar {
  width: 8px; /* 滚动条宽度 */
  height: 8px; /* 对于垂直滚动条的高度 */
}

/* 定义滚动条轨道（外层滑道）样式 */
::-webkit-scrollbar-track {
  background-color: rgba(0, 0, 0, 0.1); /* 轨道背景颜色 */
}

/* 定义滚动条滑块（内层可滚动部分）样式 */
::-webkit-scrollbar-thumb {
  background-color: rgba(0, 0, 0, 0.5); /* 滑块背景颜色 */
  border-radius: 6px; /* 圆角效果 */
}

/* 当鼠标悬停在滚动条上时的滑块样式 */
::-webkit-scrollbar-thumb:hover {
  background-color: rgba(0, 0, 0, 0.7); /* 鼠标悬停时滑块的颜色加深 */
}

</style>
