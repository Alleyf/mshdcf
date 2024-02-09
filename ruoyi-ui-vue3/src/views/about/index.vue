<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :lg="12" :sm="24" style="padding-left: 20px">
        <el-carousel :interval="5000" arrow="never" indicator-position="outside">
          <el-carousel-item v-for="item in imageLs" :key="item">
            <el-image :src="item" fit="fill"/>
            <!--            <h3 justify="center" text="2xl">{{ item }}</h3>-->
          </el-carousel-item>
        </el-carousel>
        <p>
          <b>当前版本:</b> <span>v{{ version }}</span>
        </p>
        <el-card class="update-log">
          <div slot="header" class="clearfix">
            <span>联系信息</span>
          </div>
          <div class="body">
            <p>
              <i class="el-icon-s-promotion"></i> 官网：
              <el-link
                href="https://github.com/Alleyf/MSHDCF"
                target="_blank"
              >https://github.com/Alleyf/MSHDCF
              </el-link
              >
            </p>
          </div>
        </el-card>
      </el-col>

      <el-col :lg="12" :sm="24" style="padding-left: 50px">
        <p>
          <el-timeline>
            <el-timeline-item
              v-for="(log, index) in logs"
              :key="index"
              :color="log.color"
              :hollow="log.hollow"
              :icon="log.icon"
              :size="log.size"
              :timestamp="log.timestamp"
              :type="log.type"
              placement="top"
            >
              <el-card>
                <el-link :href="log.url" target="_blank" type="default">
                  <el-tag :type="log.type">
                    {{ log.content }}
                  </el-tag>
                </el-link>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </p>
      </el-col>
    </el-row>

  </div>
</template>

<script name="Index" setup>
import {onMounted, reactive, ref} from "vue";
import ChinaMap from "@/views/chinaMap.vue";

const version = ref('1.8.1')
const screenRef = ref(null)

function getScale(_w = 1440, _h = 1080) {
  const ww = window.innerWidth / _w;
  const wh = window.innerHeight / _h;
  // ww > wh 什么情况下是true？当屏幕宽度大于高度的时候，我们取高度的比例，反之取宽度的比例
  return ww > wh ? wh : ww;
}

/*
 * 缩放屏幕
 * 注意：如果你的设计稿不是1920*1080，你需要传入你的设计稿的宽度和高度，否则会出现黑边
 * 先缩放，再平移到屏幕中心
 */
function scale(w = 1440, h = 1080) {
  const scale = getScale(w, h);
  if (screenRef.value) {
    screenRef.value.style.transform = `scale( ${scale}) translate(-50%, -50%)`;
    screenRef.value.style.transition = 'all 1s linear';
  }
}


const now = () => {
  return new Date().getFullYear() + '-' + (new Date().getMonth() + 1) + ' ' + new Date().getDate() + '-' + new Date().getHours() + ':' + new Date().getMinutes()
}
const imageLs = ref([
  'https://qnpicmap.fcsluck.top/pics/202402040033310.png',
  'https://qnpicmap.fcsluck.top/pics/202402040034979.png',
  'https://qnpicmap.fcsluck.top/pics/202402040035166.png'
])

const logs = ref([
  {
    content: '添加采集模块，完成crawlab的node节点管理（增删改查）接口功能',
    timestamp: now(),
    color: '#409EFF',
    icon: 'el-icon-s-promotion',
    size: 'large',
    hollow: false,
    type: 'primary',
    url: 'https://github.com/Alleyf/MSHDCF'
  },
  {
    content: '完成爬虫模块的爬虫数据源基本管理和数据检索模块的司法案例和法律法规基本管理业务功能',
    timestamp: now(),
    color: '#67C23A',
    icon: 'el-icon-s-promotion',
    size: 'large',
    hollow: false,
    type: 'success',
    url: 'https://github.com/Alleyf/MSHDCF'
  },
  {
    content: '调整数据源管理前端样式和全局标签缓存机制（默认全部缓存）',
    timestamp: now(),
    color: '#E6A23C',
    icon: 'el-icon-s-promotion',
    size: 'large',
    hollow: false,
    type: 'warning',
    url: 'https://github.com/Alleyf/MSHDCF'
  },
  {
    content: '完成es检索模块后端对司法案例的基本检索和分页功能',
    timestamp: now(),
    color: '#F56C6C',
    icon: 'el-icon-s-promotion',
    size: 'large',
    hollow: false,
    type: 'danger',
    url: 'https://github.com/Alleyf/MSHDCF'
  },
  {
    content: '完成es检索模块后端对法律法规的基本检索、分页以及数据全量同步功能',
    timestamp: now(),
    color: '#e7f611',
    icon: 'el-icon-s-promotion',
    size: 'large',
    hollow: false,
    type: 'primary',
    url: 'https://github.com/Alleyf/MSHDCF'
  },
  {
    content: '添加采集模块，完成crawlab的node节点管理（增删改查）接口功能',
    timestamp: now(),
    color: '#9950d5',
    icon: 'MoreFilled',
    size: 'large',
    hollow: false,
    type: 'success',
    url: 'https://github.com/Alleyf/MSHDCF'
  }
])

onMounted(() => {
  // scale();

})


window.addEventListener('resize', () => scale());
</script>

<style lang="scss" scoped>

.container-wrapper {
  width: 1386px;
  height: 1080px;
}

.screen {
  position: fixed;
  overflow: hidden;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  transform-origin: center;
  background-color: pink;
}

.el-carousel__item h3 {
  display: flex;
  color: #475669;
  opacity: 0.75;
  line-height: 300px;
  margin: 0;
}

.el-carousel__item:nth-child(2n) {
  background-color: #99a9bf;
}

.el-carousel__item:nth-child(2n + 1) {
  background-color: #d3dce6;
}

.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }

  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }

  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>

