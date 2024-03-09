<script setup>
import {getCurrentInstance, ref, onMounted} from 'vue';
import {useRoute} from 'vue-router'
import {Discount, Link} from "@element-plus/icons-vue";
import {getRegulation, getRegulationWorldCloud} from "@/api/retrieve/regulation";
import {Icon} from "@iconify/vue";

const {proxy} = getCurrentInstance();
const {
  crawler_source,
  crawl_common_status,
  law_type
} = proxy.useDict('crawler_source', 'crawl_common_status', 'law_type')

const route = useRoute();
const query = route.query;

const lawItem = ref({
  extra: {},
});

// 定义图标映射
const icons = {
  releaseOrganization: 'material-symbols:gavel',
  field: 'arcticons:field-trip',
  type: 'lucide:book-type',
  structure: 'ph:tag',
  reviseNum: 'material-symbols:reviews-outline',
  releaseDate: 'material-symbols:edit-calendar-outline',
  executeDate: 'material-symbols:event-available-outline',
  isValidity: 'material-symbols:check-circle-outline',
  url: 'mdi:web'
};
const text = ref("")
const textLs = ref([]);
const displayedText = ref('');
const typeSpeed = 10; // 字符间隔时间，单位：毫秒
const typewriter = ref(null);
const wordCloud = ref(null);
const worldCloudFlag = ref(false);


let index = 0;

onMounted(() => {
  const id = route.params.id
  if (!id) {
    proxy.$modal.msgError("请传入id")
    return
  }
  getRegulation(id).then(async res => {
    lawItem.value = res.data;
    // 正文内容处理(应该用es高亮查询的结果，但是es高亮结果为空)
    if (query.keyword !== "" || query.keyword === undefined) {
      // console.log(query.keyword)
      // if (res.data.stripContent) {
      // lawItem.value.stripContent = res.data.stripContent.replaceAll(query.keyword, `<strong class='text-red-500'>${query.keyword}</strong>`)
      // } else {
      // lawItem.value.content = res.data.content.replaceAll(query.keyword, `<strong class='text-red-500'>${query.keyword}</strong>`)
      // }
    }
    // 语义信息处理
    if (res.data.extra !== null) {
      lawItem.value.extra = JSON.parse(lawItem.value.extra);
    } else {
      lawItem.value.extra = {};
    }
    // console.log(lawItem.value.extra)
    proxy.$modal.msgSuccess(`获取数据成功`);
    text.value = lawItem.value.stripContent !== undefined ? lawItem.value.stripContent : lawItem.value.content;
    textLs.value = text.value.split("\n")
    lawItem.value.sourceId = 1;
    // todo: extra不一定有会导致报错，需要改为case一样
    // 获取来源
    const targetSource = crawler_source.value.filter(item => item.value === lawItem.value.sourceId.toString());
    lawItem.value.source = targetSource[0].label;
    // 获取法条词云（添加10秒延迟，防止词云还未生成获取为空）
    setTimeout(() => {
      getRegulationWorldCloud(id).then(res => {
        if (res.msg !== null) {
          wordCloud.value = res.msg;
          worldCloudFlag.value = true;
        }
      });
    }, 5000); // 延迟10秒（10000毫秒）
    // 打字机效果
    // const timer = setInterval(() => {
    //   if (index < text.value.length) {
    //     displayedText.value += text.value[index];
    //     index++;
    //   } else {
    //     clearInterval(timer);
    //   }
    //   typewriter.scrollLeft = typewriter.scrollWidth; // 滚动到最新输入字符位置
    // }, typeSpeed);
    console.log(textLs.value)
    await Promise.all(textLs.value.map(typeText));
  }).catch(err => {
    // proxy.$modal.msgError(err.msg, text.value)
  })
})


async function typeText(text) {
  // 获取要添加标签的 div 元素
  const divElement = document.getElementById('content');
  const element = document.createElement('p');
  divElement.appendChild(element); // 添加到页面中，你也可以根据需要添加到特定容器中
  for (let i = 0; i < text.length; i++) {
    element.innerHTML += text[i];
    divElement.scrollLeft = divElement.scrollWidth; // 滚动到最新输入字符位置
    // typewriter.scrollLeft = typewriter.scrollWidth; // 滚动到最新输入字符位置
    await new Promise(resolve => setTimeout(resolve, typeSpeed));
  }
  element.innerHTML = element.innerHTML.replace(query.keyword, `<strong class='text-red-500'>${query.keyword}</strong>`);
}

const handleTabClick = (pane, ev) => {
  // console.log(pane.props.label, pane)
  if (pane.props.label === '法条词云' && worldCloudFlag === false) {
    // 获取案例词云（添加10秒延迟，防止词云还未生成获取为空）
    getRegulationWorldCloud(route.params.id).then(res => {
      if (res.msg === null) {
        proxy.$modal.msgError("词云还未生成，请稍后")
        return
      }
      wordCloud.value = res.msg;
      worldCloudFlag.value = true;
    });
  }
}
</script>

<template>
  <div class="app-container">

    <el-row :gutter="20" :justify="'space-between'">
      <el-col :span="12" class="contentWrapper">
        <el-link :href="lawItem.url" :icon="Link" :type="'success'" class="bold" target="_blank">{{
            lawItem.name
          }}
        </el-link>
        <p style="display: flex;justify-content: space-between">
          <span v-if="lawItem.releaseOrganization" class="flex">
              <Icon class="text-2xl text-blue-700"
                    icon="map:courthouse"/>
            {{ lawItem.releaseOrganization }}
          </span>
          <span v-if="lawItem.type" class="flex">
              <Icon class="text-2xl text-yellow-500" icon="ep:discount"/>
            {{ lawItem.type }}
          </span>
          <span v-if="lawItem.releaseDate" class="flex">
              <Icon class="text-2xl text-purple-500" icon="material-symbols:calendar-clock"/>
              {{ lawItem.releaseDate }}
          </span>
        </p>
        <el-card ref="typewriter">
          <div id="content" class="content" style="padding: 10px;">
            <!--          <div v-for="(item,index) in textLs" :key="index" style="padding: 10px;">-->
            <!--              todo 加粗的html也被显示出来了-->
            <!--            <span v-for="str in handleContent(item)" v-html="str"/>-->
            <!--          <p v-html="displayedText"/>-->
            <!--          </div>-->
            <!--            <p v-for="(text, index) in textLs" :key="index" style="padding: 10px;"/>-->
          </div>
          <!--          <p style="padding: 10px;" v-html="displayedText"/>-->
        </el-card>
      </el-col>
      <el-col :span="12" class="baseInfo">

        <el-tabs class="mytabs" style="height: 200px" tab-position="top" @tab-click="handleTabClick">
          <el-tab-pane label="基本信息">
            <el-card :shadow="'always'" class="el-space--vertical">
              <el-tag style="font-weight: bold;font-size: large">基本信息</el-tag>
              <el-row :gutter="20" :justify="'space-between'">
                <el-col v-if="lawItem.releaseOrganization" class="flex">
                  <Icon :icon="icons['releaseOrganization']" class="text-2xl"/>
                  颁布组织：{{
                    lawItem.releaseOrganization
                  }}
                </el-col>
                <el-col v-if="lawItem.field">
                  <Icon :icon="icons['field']" class="text-2xl"/>
                  归属领域 ：{{ lawItem.field }}
                </el-col>
                <el-col v-if="lawItem.type">
                  <Icon :icon="icons['type']" class="text-2xl"/>
                  案件类型：{{ lawItem.type }}
                </el-col>
                <el-col v-if="lawItem.structure">
                  <Icon :icon="icons['structure']" class="text-2xl"/>
                  法条结构：{{ lawItem.structure }}
                </el-col>
                <el-col v-if="lawItem.reviseNum">
                  <Icon :icon="icons['reviseNum']" class="text-2xl"/>
                  修订次数：{{ lawItem.reviseNum }}
                </el-col>
                <el-col v-if="lawItem.releaseDate">
                  <Icon :icon="icons['releaseDate']" class="text-2xl"/>
                  发布日期：{{ lawItem.releaseDate }}
                </el-col>
                <el-col v-if="lawItem.executeDate">
                  <Icon :icon="icons['executeDate']" class="text-2xl"/>
                  实施日期：{{ lawItem.executeDate }}
                </el-col>
                <el-col v-if="lawItem.isValidity">
                  <Icon :icon="icons['isValidity']" class="text-2xl"/>
                  现行有效：{{ lawItem.isValidity }}
                </el-col>
                <el-col v-if="lawItem.url">
                  <Icon :icon="icons['url']" class="text-2xl"/>
                  案件来源：
                  <el-link :href="lawItem.url" target="_blank" type="primary">
                    {{ lawItem.source }}
                  </el-link>
                </el-col>
              </el-row>
            </el-card>
          </el-tab-pane>
          <el-tab-pane :lazy="true" label="法条词云">
            <el-card :shadow="'always'">
              <el-tag style="font-weight: bold;font-size: large" type="warning">法条词云</el-tag>
              <el-divider/>
              <el-image v-show="worldCloudFlag" :loading="'lazy'" :src="wordCloud"
                        style="margin-left: 52px;height: 80%;width: 80%"/>
            </el-card>
          </el-tab-pane>
          <el-tab-pane v-if="Object.keys(lawItem.extra).length !== 0" label="语义信息"
                       style="font-weight: normal;font-size: medium">

            <el-tabs tab-position="right">

              <el-tab-pane v-if="Object.keys(lawItem.extra).length !== 0" label="附加信息">
                <el-card :shadow="'always'">
                  <el-row :gutter="20" :justify="'space-between'">
                    <el-col v-if="lawItem.extra.field"><span class="font-black">所属领域：</span>{{
                        lawItem.extra.field
                      }}
                    </el-col>
                    <el-col v-if="lawItem.extra.type"><span class="font-black">法条类型：</span>{{ lawItem.extra.type }}
                    </el-col>
                    <el-col v-if="lawItem.extra.organization"><span
                        class="font-black">颁布组织：</span>{{ lawItem.extra.organization }}
                    </el-col>
                    <el-col v-if="lawItem.extra.release"><span
                        class="font-black">发行日期：</span>{{ lawItem.extra.release }}
                    </el-col>
                    <el-col v-if="lawItem.extra.execute"><span
                        class="font-black">实施日期：</span>{{ lawItem.extra.execute }}
                    </el-col>
                    <el-col v-if="lawItem.extra.scope"><span class="font-black">作用范围：</span>{{
                        lawItem.extra.scope
                      }}
                    </el-col>
                  </el-row>
                </el-card>
              </el-tab-pane>
              <!--              todo 摘要用打字机效果，正文直接显示-->
              <el-tab-pane v-if="lawItem.extra.abstract" label="摘要总结" style="font-weight: bold;font-size: medium"
                           type="warning">
                <el-card :shadow="'always'">
                  <el-tag style="font-weight: bold;font-size: large" type="warning">摘要总结</el-tag>
                  <el-divider/>
                  <p style="padding: 10px; margin: 10px; color: rgb(63,61,61); text-indent: 2em">
                    {{ lawItem.extra.abstract }}</p>
                </el-card>
              </el-tab-pane>
              <el-tab-pane v-if="lawItem.extra.basis" label="法条依据">
                <el-card :shadow="'always'" style="color: #ba2636">
                  <el-tag style="font-weight: bold;font-size: large" type="danger">法律依据</el-tag>
                  <el-divider/>
                  <ol>
                    <li v-for="(item,index) in lawItem.extra.basis" :key="index">
                      {{
                        item
                      }}
                    </li>
                  </ol>
                </el-card>
              </el-tab-pane>
              <el-tab-pane v-if="lawItem.extra.main" label="主要内容">
                <el-card :shadow="'always'">
                  <el-tag style="font-weight: bold;font-size: large" type="warning">主要内容</el-tag>
                  <el-divider/>
                  <ol v-if="Array.isArray(lawItem.extra.main)">
                    <li v-for="(item,index) in lawItem.extra.main" :key="index">
                      {{
                        item
                      }}
                    </li>
                  </ol>
                  <p v-else style="padding: 10px; margin: 10px; color: rgb(63,61,61); text-indent: 2em">
                    {{ lawItem.extra.main }}</p>
                </el-card>
              </el-tab-pane>
            </el-tabs>
          </el-tab-pane>
        </el-tabs>


      </el-col>

    </el-row>
    <el-backtop :bottom="100" :right="100"/>
  </div>
</template>

<style lang="scss" scoped>

$primary-color: #7a6df0;
$secondary-color: #e1c199;
// ... 其他颜色变量

.el-col[v-if*="lawItem."] {
  color: var(--color-primary); // 使用CSS变量替代直接写入的颜色值
}

.baseInfo {


  .el-tag {
    @apply font-bold text-xl text-center
  }

  .el-divider {
    @apply my-4;
  }

  .el-col {
    @apply p-2;
  }

  .el-link {
    @apply text-blue-500;
  }

  /* 添加图标 */
  .el-col {
    @apply flex items-center;
  }

  .el-col::before {
    content: '';
    @apply mr-2 h-full w-1 rounded-full;
  }

  /* 设置不同的颜色 */
  .el-col:nth-of-type(1)::before {
    @apply bg-purple-500;
  }

  .el-col:nth-of-type(2)::before {
    @apply bg-red-500;
  }

  .el-col:nth-of-type(3)::before {
    @apply bg-green-500;
  }

  .el-col:nth-of-type(4)::before {
    @apply bg-blue-500;
  }

  .el-col:nth-of-type(5)::before {
    @apply bg-yellow-500;
  }

  .el-col:nth-of-type(6)::before {
    @apply bg-indigo-500;
  }

  .el-col:nth-of-type(7)::before {
    @apply bg-teal-500;
  }

  .el-col:nth-of-type(8)::before {
    @apply bg-pink-500;
  }

  /* 调整卡片阴影 */
  .el-card {
    @apply shadow-lg;
    padding: 2px;

  }
}

.el-card {
  padding: 20px;
}

.el-row > .el-col {
  padding: 5px 0;
  margin: 10px 0;

  &:not(:last-child) {
    border-bottom: 1px solid #ccc;
  }
}

.bold {
  font-weight: bold;
  font-size: large;
}

.content {
  text-shadow: #ebeee8 1px 10px 1px;
  text-align: justify;
  text-justify: auto;
  text-indent: 2em; /* 设置段落首行缩进两个字符 */
}


.mytabs {
  min-height: 800px;
}


</style>
