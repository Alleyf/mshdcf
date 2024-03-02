<script setup>
import {getCurrentInstance, reactive, ref, toRefs, onMounted, onBeforeMount} from 'vue';
import {pageCase, getCase, listCase, getCaseWorldCloud} from "@/api/retrieve/case";
import {useRoute} from 'vue-router'
import {Link} from "@element-plus/icons-vue";
import {Icon} from '@iconify/vue';


const {proxy} = getCurrentInstance();
const {
  doc_case_cause,
  doc_case_type,
  crawler_source,
  crawl_common_status
} = proxy.useDict("doc_case_cause", "doc_case_type", "crawler_source", "crawl_common_status");

const route = useRoute();
const query = route.query;

const caseItem = ref({
  extra: {},
});
// 定义图标映射
const icons = {
  court: 'material-symbols:gavel',
  number: 'material-symbols:format-list-numbered',
  type: 'lucide:book-type',
  label: 'ph:tag',
  cause: 'tabler:brand-reason',
  process: 'carbon:ibm-event-processing',
  url: 'gg:website',
  judgeDate: 'material-symbols:calendar-clock',
  party: 'material-symbols:supervisor-account'
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
  getCase(id).then(async res => {
    caseItem.value = res.data;
    // console.log(query)
    if (query.keyword !== "" || query.keyword === undefined) {
      // if (res.data.stripContent) {
      //   // caseItem.value.stripContent = res.data.stripContent.replaceAll(query.keyword, `<strong class='text-red-500'>${query.keyword}</strong>`)
      // } else {
      //   // caseItem.value.content = res.data.content.replaceAll(query.keyword, `<strong class='text-red-500'>${query.keyword}</strong>`)
      // }
    }
    // caseItem.value.content = res.data.content.split("\n").filter(element => element !== "").join("。<br/>&emsp;&emsp;");
    // console.log(caseItem.value.content)
    if (res.data.extra !== null) {
      caseItem.value.extra = JSON.parse(caseItem.value.extra);
    } else {
      caseItem.value.extra = {};
    }
    // console.log(caseItem.value)
    // wordCloud.value = res.msg;
    proxy.$modal.msgSuccess(`获取数据成功`);
    text.value = caseItem.value.stripContent !== undefined ? caseItem.value.stripContent : caseItem.value.content;
    textLs.value = text.value.split("\n")
    // caseItem.value.sourceId = 1;
    // 获取来源
    const targetSource = crawler_source.value.filter(item => item.value === caseItem.value.sourceId.toString());
    caseItem.value.source = targetSource[0].label;
    // 获取案例词云（添加10秒延迟，防止词云还未生成获取为空）
    setTimeout(() => {
      getCaseWorldCloud(id).then(res => {
        // console.log(res.msg)
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
    // proxy.$modal.msgError(err.msg)
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
  if (pane.props.label === '案例词云' && worldCloudFlag.value === false) {
    // 获取案例词云（添加10秒延迟，防止词云还未生成获取为空）
    getCaseWorldCloud(route.params.id).then(res => {
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
        <el-link :href="caseItem.url" :icon="Link" :type="'success'" class="bold" target="_blank">{{
            caseItem.name
          }}
        </el-link>
        <p style="display: flex;justify-content: space-between">
          <span class="flex">
            <Icon v-if="index === 0" class="text-2xl text-blue-700" icon="map:courthouse"/>
            {{ caseItem.court }}
          </span>
          <span class="flex">
            <Icon v-if="index === 0" class="text-2xl text-yellow-500" icon="ep:discount"/>
            {{ caseItem.number }}
          </span>
          <span class="flex">
            <Icon v-if="index === 0" class="text-2xl text-purple-500" icon="material-symbols:calendar-clock"/>
              {{ caseItem.judgeDate }}
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
        </el-card>
      </el-col>
      <el-col :span="12" class="baseInfo">

        <el-tabs class="mytabs" style="height: 200px" tab-position="top" @tab-click="handleTabClick">
          <el-tab-pane label="基本信息">
            <el-card :shadow="'always'" class="el-space--vertical">
              <el-tag class="text-gray-500" style="font-weight: bold;font-size: large">基本信息</el-tag>
              <el-divider/>

              <el-row :gutter="20" :justify="'space-between'">
                <el-col v-if="caseItem.court" class="flex">
                  <Icon :icon="icons['court']" class="text-2xl"/>
                  审判法院：{{
                    caseItem.court
                  }}
                </el-col>
                <el-col v-if="caseItem.number">
                  <Icon :icon="icons['number']" class="text-2xl"/>
                  案 号：{{ caseItem.number }}
                </el-col>
                <el-col v-if="caseItem.type">
                  <Icon :icon="icons['type']" class="text-2xl"/>
                  案件类型：{{ caseItem.type }}
                </el-col>
                <el-col v-if="caseItem.label">
                  <Icon :icon="icons['label']" class="text-2xl"/>
                  标签：{{ caseItem.label }}
                </el-col>
                <el-col v-if="caseItem.cause">
                  <Icon :icon="icons['cause']" class="text-2xl"/>
                  案由：{{ caseItem.cause }}
                </el-col>
                <el-col v-if="caseItem.process">
                  <Icon :icon="icons['process']" class="text-2xl"/>
                  审理程序：{{ caseItem.process }}
                </el-col>
                <el-col v-if="caseItem.url">
                  <Icon :icon="icons['url']" class="text-2xl"/>
                  案件来源：
                  <el-link :href="caseItem.url" target="_blank" type="primary">
                    {{ caseItem.source }}
                  </el-link>
                </el-col>
                <el-col v-if="caseItem.judgeDate">
                  <Icon :icon="icons['judgeDate']" class="text-2xl"/>
                  判决日期：{{ caseItem.judgeDate }}
                </el-col>
                <el-col v-if="caseItem.party">
                  <Icon :icon="icons['party']" class="text-2xl"/>
                  当事人：{{ caseItem.party }}
                </el-col>
              </el-row>
            </el-card>
          </el-tab-pane>
          <!--          <el-tab-pane v-if="caseItem.extra.summary" label="摘要总结">-->
          <!--            <el-card :shadow="'always'">-->
          <!--              <el-tag style="font-weight: bold;font-size: large" type="warning">摘要总结</el-tag>-->
          <!--              <el-divider/>-->
          <!--              <p-->
          <!--                style="background-color: rgb(240, 240, 240); padding: 10px; margin: 10px; border: 1px solid rgb(240, 240, 240);color: rgb(100, 100, 100);">-->
          <!--                {{ caseItem.extra.summary }}</p>-->
          <!--            </el-card>-->
          <!--          </el-tab-pane>-->
          <!--          <el-tab-pane v-if="caseItem.legalBasis || caseItem.extra.article" label="法律依据">-->
          <!--            <el-card :shadow="'always'">-->
          <!--              <el-tag style="font-weight: bold;font-size: large" type="danger">法律依据</el-tag>-->
          <!--              <el-divider/>-->
          <!--              <p v-if="caseItem.legalBasis">{{ caseItem.legalBasis }}</p>-->
          <!--              <p v-else-if="caseItem.extra.article">{{ caseItem.extra.article }}</p>-->
          <!--            </el-card>-->
          <!--          </el-tab-pane>-->
          <!--          <el-tab-pane v-if="caseItem.relatedCases" label="相关案例">-->
          <!--            <el-card :shadow="'always'">-->
          <!--              <el-tag style="font-weight: bold;font-size: large" type="warning">相关案例</el-tag>-->
          <!--              <el-divider/>-->
          <!--              <ul>-->
          <!--                <el-link v-for="(item,index) in caseItem.relatedCases" :key="index" :href="item.url">-->
          <!--                  {{-->
          <!--                    item.name-->
          <!--                  }}-->
          <!--                </el-link>-->
          <!--              </ul>-->
          <!--            </el-card>-->
          <!--          </el-tab-pane>-->
          <el-tab-pane :lazy="true" label="案例词云">
            <el-card :shadow="'always'">
              <el-tag style="font-weight: bold;font-size: large" type="warning">案例词云</el-tag>
              <el-divider/>
              <el-image v-show="worldCloudFlag" :loading="'lazy'" :src="wordCloud"
                        style="margin-left: 52px;height: 80%;width: 80%"/>
            </el-card>
          </el-tab-pane>
          <el-tab-pane v-if="Object.keys(caseItem.extra).length !== 0" label="语义信息"
                       style="font-weight: normal;font-size: medium">
            <!--            <el-divider/>-->
            <!--            <el-tag style="font-weight: bold;font-size: large" type="warning">语义信息</el-tag>-->

            <el-tabs tab-position="right">

              <el-tab-pane v-if="Object.keys(caseItem.extra).length !== 0" label="附加信息">
                <el-card :shadow="'always'">
                  <el-row :gutter="20" :justify="'space-between'">
                    <div v-if="caseItem.extra.party" class="text-center m-auto">
                      <el-tag size="small" type="success"> {{ caseItem.extra.party.plaintiff }}</el-tag>
                      ⚖️
                      <el-tag size="small" type="danger">{{ caseItem.extra.party.defendant }}</el-tag>
                    </div>
                    <el-col v-if="caseItem.extra.keyword">
                      <span class="font-black">关键字：</span>{{ caseItem.extra.keyword }}
                    </el-col>
                    <el-col v-if="caseItem.extra.plea"><span class="font-black">诉讼要求：</span>{{
                        caseItem.extra.plea
                      }}
                    </el-col>
                    <el-col v-if="caseItem.extra.label"><span class="font-black">案件类型：</span>{{
                        caseItem.extra.label
                      }}
                    </el-col>
                    <el-col v-if="caseItem.extra.plai"><span class="font-black">原告诉述：</span>{{
                        caseItem.extra.plai
                      }}
                    </el-col>
                    <el-col v-if="caseItem.extra.defe"><span class="font-black">被告辩称：</span>{{
                        caseItem.extra.defe
                      }}
                    </el-col>
                    <el-col v-if="caseItem.extra.fact"><span class="font-black">案件事实：</span>{{
                        caseItem.extra.fact
                      }}
                    </el-col>
                    <el-col v-if="caseItem.extra.note"><span class="font-black">判决记录：</span>{{
                        caseItem.extra.note
                      }}
                    </el-col>
                  </el-row>
                </el-card>
              </el-tab-pane>
              <!--              todo 摘要用打字机效果，正文直接显示-->
              <el-tab-pane v-if="caseItem.extra.summary" label="摘要总结" style="font-weight: bold;font-size: medium"
                           type="warning">
                <el-card :shadow="'always'">
                  <el-tag style="font-weight: bold;font-size: large" type="warning">摘要总结</el-tag>
                  <el-divider/>
                  <p style="padding: 10px; margin: 10px; color: rgb(63,61,61); text-indent: 2em">
                    {{ caseItem.extra.summary }}</p>
                </el-card>
              </el-tab-pane>
              <el-tab-pane v-if="caseItem.legalBasis || caseItem.extra.article" label="法律依据">
                <el-card :shadow="'always'" style="color: #ba2636">
                  <el-tag style="font-weight: bold;font-size: large" type="danger">法律依据</el-tag>
                  <el-divider/>
                  <p v-if="caseItem.legalBasis">{{ caseItem.legalBasis }}</p>
                  <p v-else-if="caseItem.extra.article">{{ caseItem.extra.article }}</p>
                </el-card>
              </el-tab-pane>
              <el-tab-pane v-if="caseItem.relatedCases" label="相关案例">
                <el-card :shadow="'always'">
                  <el-tag style="font-weight: bold;font-size: large" type="warning">相关案例</el-tag>
                  <el-divider/>
                  <ul>
                    <el-link v-for="(item,index) in caseItem.relatedCases" :key="index" :href="item.url">
                      {{
                        item.name
                      }}
                    </el-link>
                  </ul>
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

.el-col[v-if*="caseItem."] {
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

.addInfo {
  font-weight: normal;
  font-size: medium
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
