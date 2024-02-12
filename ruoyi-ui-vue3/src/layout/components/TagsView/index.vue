<!--<template>-->
<!--  <div id="tags-view-container" class="tags-view-container flex justify-around items-center">-->
<!--    <scroll-pane ref="scrollPaneRef" class="tags-view-wrapper" @scroll="handleScroll">-->
<!--      <router-link-->
<!--          v-for="tag in visitedViews"-->
<!--          :key="tag.path"-->
<!--          :class="isActive(tag) ? 'active' : ''"-->
<!--          :data-path="tag.path"-->
<!--          :style="activeStyle(tag)"-->
<!--          :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"-->
<!--          class="tags-view-item flex justify-start flex-nowrap items-center m-auto flex-row"-->
<!--          @click.middle="!isAffix(tag) ? closeSelectedTag(tag) : ''"-->
<!--          @contextmenu.prevent="openMenu(tag, $event)"-->
<!--      >-->
<!--        {{ tag.title }}-->
<!--        <span v-if="!isAffix(tag)" class="flex-1 justify-self-auto" @click.prevent.stop="closeSelectedTag(tag)">-->
<!--&lt;!&ndash;          <close class="el-icon-close" style="width: 1em; height: 1em;vertical-align: middle;"/>&ndash;&gt;-->
<!--          <Icon class="text-xs" icon="material-symbols:close"/>-->
<!--        </span>-->
<!--      </router-link>-->
<!--    </scroll-pane>-->
<!--    <ul v-show="visible" :style="{ left: left + 'px', top: top + 'px' }" class="contextmenu">-->
<!--      <li @click="refreshSelectedTag(selectedTag)">-->
<!--        <refresh-right style="width: 1em; height: 1em;"/>-->
<!--        刷新页面-->
<!--      </li>-->
<!--      <li v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">-->
<!--        <close style="width: 1em; height: 1em;"/>-->
<!--        关闭当前-->
<!--      </li>-->
<!--      <li @click="closeOthersTags">-->
<!--        <circle-close style="width: 1em; height: 1em;"/>-->
<!--        关闭其他-->
<!--      </li>-->
<!--      <li v-if="!isFirstView()" @click="closeLeftTags">-->
<!--        <back style="width: 1em; height: 1em;"/>-->
<!--        关闭左侧-->
<!--      </li>-->
<!--      <li v-if="!isLastView()" @click="closeRightTags">-->
<!--        <right style="width: 1em; height: 1em;"/>-->
<!--        关闭右侧-->
<!--      </li>-->
<!--      <li @click="closeAllTags(selectedTag)">-->
<!--        <circle-close style="width: 1em; height: 1em;"/>-->
<!--        全部关闭-->
<!--      </li>-->
<!--    </ul>-->
<!--  </div>-->
<!--</template>-->
<template>
  <div id="tags-view-container" class="tags-view-container flex items-center h-10 m-auto">
    <scroll-pane ref="scrollPaneRef" class="tags-view-wrapper m-auto flex-1 items-center">
      <router-link
          v-for="tag in visitedViews"
          :key="tag.path"
          :class="isActive(tag) ? 'active' : ''"
          :data-path="tag.path"
          :style="activeStyle(tag)"
          :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
          class="tags-view-item flex justify-start flex-nowrap items-center m-auto flex-row"
          @click.middle.self.prevent="!isAffix(tag) ? closeSelectedTag(tag) : ''"
          @click.prevent="openMenu(tag, $event)"
      >
        {{ tag.title }}
        <span v-if="!isAffix(tag)" class="flex-1 justify-self-auto" @click.prevent.stop="closeSelectedTag(tag)">
          <Icon class="text-xs" icon="material-symbols:close"/>
        </span>
      </router-link>
    </scroll-pane>
    <el-dropdown class="dropdown-wrapper flex-shrink-0 mr-10" trigger="hover">
      <span class="el-dropdown-link">
        <el-icon class="text-gray-500"><more-filled/></el-icon>
      </span>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="refreshSelectedTag(selectedTag)">刷新页面</el-dropdown-item>
          <el-dropdown-item v-if="!isAffix(selectedTag)" @click="closeSelectedTag(selectedTag)">关闭当前
          </el-dropdown-item>
          <el-dropdown-item @click="closeOthersTags">关闭其他</el-dropdown-item>
          <el-dropdown-item v-if="!isFirstView()" @click="closeLeftTags">关闭左侧</el-dropdown-item>
          <el-dropdown-item v-if="!isLastView()" @click="closeRightTags">关闭右侧</el-dropdown-item>
          <el-dropdown-item @click="closeAllTags(selectedTag)">全部关闭</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>


<script setup>
import ScrollPane from './ScrollPane'
import {getNormalPath} from '@/utils/ruoyi'
import useTagsViewStore from '@/store/modules/tagsView'
import useSettingsStore from '@/store/modules/settings'
import usePermissionStore from '@/store/modules/permission'
import {Icon} from "@iconify/vue";

const visible = ref(false);
const top = ref(0);
const left = ref(0);
const selectedTag = ref({});
const affixTags = ref([]);
const scrollPaneRef = ref(null);

const {proxy} = getCurrentInstance();
const route = useRoute();
const router = useRouter();

const visitedViews = computed(() => useTagsViewStore().visitedViews);
const routes = computed(() => usePermissionStore().routes);
const theme = computed(() => useSettingsStore().theme);

watch(route, () => {
  addTags()
  moveToCurrentTag()
})
watch(visible, (value) => {
  if (value) {
    document.body.addEventListener('click', closeMenu)
  } else {
    document.body.removeEventListener('click', closeMenu)
  }
})
onMounted(() => {
  initTags()
  addTags()
})

function isActive(r) {
  return r.path === route.path
}

function activeStyle(tag) {
  if (!isActive(tag)) return {};
  return {
    "background-color": theme.value,
    "border-color": theme.value
  };
}

function isAffix(tag) {
  return tag.meta && tag.meta.affix
}

function isFirstView() {
  try {
    return selectedTag.value.fullPath === '/index' || selectedTag.value.fullPath === visitedViews.value[1].fullPath
  } catch (err) {
    return false
  }
}

function isLastView() {
  try {
    return selectedTag.value.fullPath === visitedViews.value[visitedViews.value.length - 1].fullPath
  } catch (err) {
    return false
  }
}

function filterAffixTags(routes, basePath = '') {
  let tags = []
  routes.forEach(route => {
    if (route.meta && route.meta.affix) {
      const tagPath = getNormalPath(basePath + '/' + route.path)
      tags.push({
        fullPath: tagPath,
        path: tagPath,
        name: route.name,
        meta: {...route.meta}
      })
    }
    if (route.children) {
      const tempTags = filterAffixTags(route.children, route.path)
      if (tempTags.length >= 1) {
        tags = [...tags, ...tempTags]
      }
    }
  })
  return tags
}

function initTags() {
  const res = filterAffixTags(routes.value);
  affixTags.value = res;
  for (const tag of res) {
    // Must have tag name
    if (tag.name) {
      useTagsViewStore().addVisitedView(tag)
    }
  }
}

function addTags() {
  const {name} = route
  if (name) {
    useTagsViewStore().addView(route)
    if (route.meta.link) {
      useTagsViewStore().addIframeView(route);
    }
  }
  return false
}

function moveToCurrentTag() {
  nextTick(() => {
    for (const r of visitedViews.value) {
      if (r.path === route.path) {
        scrollPaneRef.value.moveToTarget(r);
        // when query is different then update
        if (r.fullPath !== route.fullPath) {
          useTagsViewStore().updateVisitedView(route)
        }
      }
    }
  })
}

function refreshSelectedTag(view) {
  console.log(view)
  proxy.$tab.refreshPage(view);
  if (route.meta.link) {
    useTagsViewStore().delIframeView(route);
  }
}

function closeSelectedTag(view) {
  console.log(view)
  proxy.$tab.closePage(view).then(({visitedViews}) => {
    console.log(view)

    if (isActive(view)) {
      toLastView(visitedViews, view)
    }
  })
}

function closeRightTags() {
  proxy.$tab.closeRightPage(selectedTag.value).then(visitedViews => {
    if (!visitedViews.find(i => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}

function closeLeftTags() {
  proxy.$tab.closeLeftPage(selectedTag.value).then(visitedViews => {
    if (!visitedViews.find(i => i.fullPath === route.fullPath)) {
      toLastView(visitedViews)
    }
  })
}

function closeOthersTags() {
  router.push(selectedTag.value).catch(() => {
  });
  proxy.$tab.closeOtherPage(selectedTag.value).then(() => {
    moveToCurrentTag()
  })
}

function closeAllTags(view) {
  proxy.$tab.closeAllPage().then(({visitedViews}) => {
    if (affixTags.value.some(tag => tag.path === route.path)) {
      return
    }
    toLastView(visitedViews, view)
  })
}

function toLastView(visitedViews, view) {
  const latestView = visitedViews.slice(-1)[0]
  if (latestView) {
    router.push(latestView.fullPath)
  } else {
    // now the default is to redirect to the home page if there is no tags-view,
    // you can adjust it according to your needs.
    if (view.name === 'Dashboard') {
      // to reload home page
      router.replace({path: '/redirect' + view.fullPath})
    } else {
      router.push('/')
    }
  }
}

function openMenu(tag, e) {
  const menuMinWidth = 105
  const offsetLeft = proxy.$el.getBoundingClientRect().left // container margin left
  const offsetWidth = proxy.$el.offsetWidth // container width
  const maxLeft = offsetWidth - menuMinWidth // left boundary
  const l = e.clientX - offsetLeft + 15 // 15: margin right

  if (l > maxLeft) {
    left.value = maxLeft
  } else {
    left.value = l
  }

  top.value = e.clientY
  visible.value = true
  selectedTag.value = tag
}

function closeMenu() {
  visible.value = false
}

function handleScroll() {
  closeMenu()
}
</script>

<!--<style lang='scss' scoped>-->
<!--.tags-view-container {-->
<!--  width: 100%;-->
<!--  background: #fff;-->
<!--  border-bottom: 1px solid #d8dce5;-->
<!--  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);-->

<!--  .tags-view-wrapper {-->
<!--    display: flex;-->
<!--    justify-content: space-between;-->

<!--    .tags-view-item {-->
<!--      display: inline-block;-->
<!--      position: relative;-->
<!--      cursor: pointer;-->
<!--      height: 26px;-->
<!--      line-height: 26px;-->
<!--      border: 1px solid #d8dce5;-->
<!--      color: #495060;-->
<!--      background: #fff;-->
<!--      padding: 0 8px;-->
<!--      font-size: 12px;-->
<!--      margin-left: 5px;-->
<!--      margin-top: 4px;-->

<!--      &:first-of-type {-->
<!--        margin-left: 15px;-->
<!--      }-->

<!--      &:last-of-type {-->
<!--        margin-right: 15px;-->
<!--      }-->

<!--      &.active {-->
<!--        background-color: #42b983;-->
<!--        color: #fff;-->
<!--        border-color: #42b983;-->

<!--        &::before {-->
<!--          content: "";-->
<!--          background: #fff;-->
<!--          display: inline-block;-->
<!--          width: 8px;-->
<!--          height: 8px;-->
<!--          border-radius: 50%;-->
<!--          position: relative;-->
<!--          margin-right: 5px;-->
<!--        }-->
<!--      }-->
<!--    }-->
<!--  }-->

<!--  .contextmenu {-->
<!--    margin: auto;-->
<!--    background: #fff;-->
<!--    z-index: 3000;-->
<!--    position: absolute;-->
<!--    list-style-type: none;-->
<!--    padding: 5px 0;-->
<!--    border-radius: 4px;-->
<!--    font-size: 12px;-->
<!--    font-weight: 400;-->
<!--    color: #333;-->
<!--    box-shadow: 2px 2px 3px 0 rgba(0, 0, 0, 0.3);-->

<!--    li {-->
<!--      margin: 0;-->
<!--      padding: 7px 16px;-->
<!--      cursor: pointer;-->

<!--      &:hover {-->
<!--        background: #eee;-->
<!--      }-->
<!--    }-->
<!--  }-->
<!--}-->
<!--</style>-->

<style lang="scss">
.tags-view-container {
  width: 100%;
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);

  .tags-view-wrapper {
    display: flex;
    overflow-x: auto; // 添加水平滚动
    -webkit-overflow-scrolling: touch; // 移动设备平滑滚动
    padding: 31px;

    .tags-view-item {
      display: inline-flex; // 使用inline-flex以保持水平布局
      position: relative;
      cursor: pointer;
      height: 26px;
      line-height: 26px;
      border: 1px solid #d8dce5;
      color: #495060;
      background: #fff;
      padding: 0 8px;
      font-size: 12px;
      margin-left: 5px;
      align-items: center; // 垂直居中

      &:first-of-type {
        margin-left: 15px;
      }

      &:last-of-type {
        margin-right: 15px;
      }

      &.active {
        background-color: #42b983;
        color: #fff;
        border-color: #42b983;

        &::before {
          content: "";
          background: #fff;
          display: inline-block;
          width: 8px;
          height: 8px;
          border-radius: 50%;
          position: relative;
          margin-right: 5px;
        }
      }
    }
  }

  .dropdown-wrapper {
    // 下拉条的样式
  }
}

// 其他样式...

</style>
<style lang="scss">
//reset element css of el-icon-close
.tags-view-wrapper {
  .tags-view-item {
    .el-icon-close {
      display: inline-block;

      width: 16px;
      height: 16px;
      vertical-align: 2px;
      border-radius: 50%;
      text-align: center;
      transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
      transform-origin: 100% 50%;

      &:before {
        transform: scale(0.6);
        display: inline-block;
        vertical-align: -3px;
      }

      &:hover {
        background-color: #b4bccc;
        color: #fff;
        width: 12px !important;
        height: 12px !important;
      }
    }
  }
}
</style>
