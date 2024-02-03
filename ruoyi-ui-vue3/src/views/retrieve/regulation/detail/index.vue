<script setup>
import {getCurrentInstance, ref, onMounted} from 'vue';
import {useRoute} from 'vue-router'
import {Discount, Link} from "@element-plus/icons-vue";
import {getRegulation} from "@/api/retrieve/regulation";

const {proxy} = getCurrentInstance();
const {
  crawler_source,
  crawl_common_status,
  law_type
} = proxy.useDict('crawler_source', 'crawl_common_status', 'law_type')

const route = useRoute();
const lawItem = ref({});
const text = ref("")
const displayedText = ref('');
const typeSpeed = 1; // 字符间隔时间，单位：毫秒
const typewriter = ref(null);

let index = 0;

onMounted(() => {
  const id = route.params.id
  if (!id) {
    proxy.$modal.msgError("请传入id")
    return
  }
  getRegulation(id).then(res => {
    lawItem.value = res.data;
    proxy.$modal.msgSuccess(`获取数据成功`);
    text.value = lawItem.value.content;
    lawItem.value.sourceId = 1;
    // 获取来源
    const targetSource = crawler_source.value.filter(item => item.value === lawItem.value.sourceId.toString());
    lawItem.value.source = targetSource[0].label;
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
    proxy.$modal.msgError(err.msg, text.value)
  })
})
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
          <span>
            <School style="width: 1em; height: 1em; margin-right: 2px"/>
            {{ lawItem.releaseOrganization }}
          </span>
          <span>
            <Discount style="width: 1em; height: 1em; margin-right: 1px"/>
            {{ lawItem.type }}
          </span>
          <span>
             <Timer style="width: 1em; height: 1em; margin-right: 2px"/>
              {{ lawItem.releaseDate }}
          </span>
        </p>
        <el-card ref="typewriter" class="content">
          <!--          {{ lawItem.content }}-->
          {{ displayedText }}
        </el-card>
      </el-col>
      <el-col :span="12" class="baseInfo">

        <el-tabs class="demo-tabs" style="height: 200px" tab-position="top">
          <el-tab-pane label="基本信息">
            <el-card :shadow="'always'" class="el-space--vertical">
              <el-tag style="font-weight: bold;font-size: large">基本信息</el-tag>
              <el-row :gutter="20" :justify="'space-between'">
                <el-col v-if="lawItem.releaseOrganization">颁布组织：{{
                    lawItem.releaseOrganization
                  }}
                </el-col>
                <el-col v-if="lawItem.field">归属领域 ：{{ lawItem.field }}</el-col>
                <el-col v-if="lawItem.type">案件类型：{{ lawItem.type }}</el-col>
                <el-col v-if="lawItem.structure">法条结构：{{ lawItem.structure }}</el-col>
                <el-col v-if="lawItem.reviseNum">修订次数：{{ lawItem.reviseNum }}</el-col>
                <el-col v-if="lawItem.releaseDate">发布日期：{{ lawItem.releaseDate }}</el-col>
                <el-col v-if="lawItem.executeDate">实施日期：{{ lawItem.executeDate }}</el-col>
                <el-col v-if="lawItem.isValidity">现行有效：{{ lawItem.isValidity }}</el-col>
                <el-col v-if="lawItem.url">案件来源：
                  <el-link :href="lawItem.url" type="primary">
                    {{ lawItem.source }}
                  </el-link>
                </el-col>
              </el-row>
            </el-card>
          </el-tab-pane>
          <!--          <el-tab-pane label="法律依据">-->
          <!--            <el-card :shadow="'always'">-->
          <!--              <el-tag style="font-weight: bold;font-size: large" type="danger">法律依据</el-tag>-->
          <!--              <ul>-->
          <!--                <li>{{ lawItem.legalBasis }}</li>-->
          <!--              </ul>-->
          <!--            </el-card>-->
          <!--          </el-tab-pane>-->
          <!--          <el-tab-pane label="相关案例">-->
          <!--            <el-card :shadow="'always'">-->
          <!--              <el-tag style="font-weight: bold;font-size: large" type="warning">相关案例</el-tag>-->
          <!--              <ul>-->
          <!--                <li>{{ lawItem.relatedCases }}</li>-->
          <!--              </ul>-->
          <!--            </el-card>-->
          <!--          </el-tab-pane>-->
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
