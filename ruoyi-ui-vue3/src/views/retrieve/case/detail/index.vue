<script setup>
import {getCurrentInstance, reactive, ref, toRefs, onMounted} from 'vue';
import {pageCase, getCase, listCase} from "@/api/retrieve/case";
import {useRoute} from 'vue-router'
import {Discount, Link} from "@element-plus/icons-vue";

const {proxy} = getCurrentInstance();
const {
  doc_case_cause,
  doc_case_type,
  crawler_source,
  crawl_common_status
} = proxy.useDict("doc_case_cause", "doc_case_type", "crawler_source", "crawl_common_status");

const route = useRoute();
const caseItem = ref({});
const text = ref("")
const displayedText = ref('');
const typeSpeed = 1; // 字符间隔时间，单位：毫秒
const typewriter = ref(null);
const wordCloud = ref(null);

let index = 0;

onMounted(() => {
  const id = route.params.id
  if (!id) {
    proxy.$modal.msgError("请传入id")
    return
  }
  getCase(id).then(res => {
    caseItem.value = res.data;
    wordCloud.value = res.msg;
    proxy.$modal.msgSuccess(`获取数据成功`);
    text.value = caseItem.value.content;
    caseItem.value.sourceId = 1;
    // 获取来源
    const targetSource = crawler_source.value.filter(item => item.value === caseItem.value.sourceId.toString());
    caseItem.value.source = targetSource[0].label;
    // 打字机效果
    const timer = setInterval(() => {
      if (index < text.value.length) {
        displayedText.value += text.value[index];
        index++;
      } else {
        clearInterval(timer);
      }
      typewriter.scrollLeft = typewriter.scrollWidth; // 滚动到最新输入字符位置
    }, typeSpeed);
  }).catch(err => {
    // proxy.$modal.msgError(err.msg)
  })
})
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
          <span>
            <School style="width: 1em; height: 1em; margin-right: 2px"/>
            {{ caseItem.court }}
          </span>
          <span>
            <Discount style="width: 1em; height: 1em; margin-right: 1px"/>
            {{ caseItem.number }}
          </span>
          <span>
             <Timer style="width: 1em; height: 1em; margin-right: 2px"/>
              {{ caseItem.judgeDate }}
          </span>
        </p>
        <el-card ref="typewriter" class="content">
          <!--          {{ caseItem.content }}-->
          {{ displayedText }}
        </el-card>
      </el-col>
      <el-col :span="12" class="baseInfo">

        <el-tabs class="demo-tabs" style="height: 200px" tab-position="top">
          <el-tab-pane label="基本信息">
            <el-card :shadow="'always'" class="el-space--vertical">
              <el-tag style="font-weight: bold;font-size: large">基本信息</el-tag>
              <el-divider/>

              <el-row :gutter="20" :justify="'space-between'">
                <el-col v-if="caseItem.court">审判法院：{{
                    caseItem.court
                  }}
                </el-col>
                <el-col v-if="caseItem.number">案 号：{{ caseItem.number }}</el-col>
                <el-col v-if="caseItem.type">案件类型：{{ caseItem.type }}</el-col>
                <el-col v-if="caseItem.label">标签：{{ caseItem.label }}</el-col>
                <el-col v-if="caseItem.cause">案由：{{ caseItem.cause }}</el-col>
                <el-col v-if="caseItem.process">审理程序：{{ caseItem.process }}</el-col>
                <el-col v-if="caseItem.url">案件来源：
                  <el-link :href="caseItem.url" type="primary">
                    {{ caseItem.source }}
                  </el-link>
                </el-col>
                <el-col v-if="caseItem.judgeDate">判决日期：{{ caseItem.judgeDate }}</el-col>
                <el-col v-if="caseItem.party">当事人：{{ caseItem.party }}</el-col>
              </el-row>
            </el-card>
          </el-tab-pane>
          <el-tab-pane label="法律依据">
            <el-card :shadow="'always'">
              <el-tag style="font-weight: bold;font-size: large" type="danger">法律依据</el-tag>
              <el-divider/>
              <ol>
                <li>{{ caseItem.legalBasis }}</li>
                <!--                <el-divider/>-->
              </ol>
            </el-card>
          </el-tab-pane>
          <el-tab-pane label="相关案例">
            <el-card :shadow="'always'">
              <el-tag style="font-weight: bold;font-size: large" type="warning">相关案例</el-tag>
              <el-divider/>
              <ul>
                <li>{{ caseItem.relatedCases }}</li>
              </ul>
            </el-card>
          </el-tab-pane>
          <el-tab-pane :lazy="true" label="案例词云">
            <el-card :shadow="'always'">
              <el-tag style="font-weight: bold;font-size: large" type="warning">案例词云</el-tag>
              <el-divider/>
              <el-image :loading="'lazy'" :src="wordCloud" style="margin-left: 52px;height: 80%;width: 80%"/>
            </el-card>
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
  text-indent: 2em;
}


</style>
