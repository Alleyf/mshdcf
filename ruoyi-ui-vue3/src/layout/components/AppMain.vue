<script setup>
import iframeToggle from "./IframeToggle/index"
import useTagsViewStore from '@/store/modules/tagsView'
import {computed} from "vue";
import {useRoute} from "vue-router";

const route = useRoute();

const key = computed(() => {
  return route.path + Math.random();
});
const tagsViewStore = useTagsViewStore()

// 显示组件时，替换组件 name，解决同个组件路由不一致问题

</script>


<template>
  <section class="app-main">
    <router-view v-slot="{ Component, route }">
      <transition mode="out-in" name="fade-transform">
        <keep-alive :include="tagsViewStore.cachedViews">
          <component :is="Component" v-if="!route.meta.link" :key="route.path"/>
        </keep-alive>
      </transition>
    </router-view>
    <iframe-toggle/>
  </section>
</template>


<style lang="scss" scoped>
.app-main {
  /* 50= navbar  50  */
  min-height: calc(100vh - 50px);
  width: 100%;
  position: relative;
  overflow: hidden;
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>
