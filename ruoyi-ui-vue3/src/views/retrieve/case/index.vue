<template>
  <el-container style="height: 80vh;display: flex">
    <el-header v-show="headerShow" class="search">
      <el-input
        v-model="advancedForm.name"
        class="input-with-select keyword"
        clearable
        maxlength="20"
        placeholder="请输入关键词进行搜索..."
        prefix-icon="Document"
        show-word-limit
        suffix-icon="Search"
        @keydown.enter.native="handleSearch()"
      >
        <template #prepend>
          <el-select v-model="selectAdvance" placeholder="请选择检索类型">
            <el-option key="1" label="常规搜索" value="false"/>
            <el-option key="2" label="高级搜索" value="true" @click="handleAdvanced()"/>
          </el-select>
        </template>
      </el-input>
    </el-header>
    <right-toolbar v-model:showSearch="headerShow" class="toolbar" @queryTable="handleSearch()"></right-toolbar>
    <el-main v-show="!headerShow" style="margin-top: 10%">
      <el-carousel
        v-show="caseList.length > 0"
        :interval="5000"
        arrow="hover"
        autoplay
        direction="horizontal"
        indicator-position="none"
        type="card"
      >
        <el-carousel-item v-for="(item,index) in caseList" :key="item.id">
          <el-card class="box-card" shadow="always">
            <template #header>
              <el-row :align="'middle'" :gutter="10" :justify="'center'" class="card-header">
                <el-col :span="12" class="horFlex">
                  <el-badge :value="index+1" class="mr-1" type="success"/>
                  <el-link :href="item.url" :underline="false" class="hidden" target="_blank" type="primary"
                           v-html="item.highlightName?item.highlightName:item.name">
                  </el-link>
                  <el-button circle icon="View" style="font-size: 1em" @click="handleDetail(item)"/>
                </el-col>
                <el-col :span="12" class="horFlex">
                  <el-tag type="success">{{ item.court }}</el-tag>
                  <el-tag type="danger">{{ item.judgeDate }}</el-tag>
                </el-col>
              </el-row>
            </template>
            <template #default>
              <el-row :gutter="10" :justify="'center'" class="card-content">
                <el-col :span="16">
                  <el-tabs style="height: 100%" tab-position="left" type="card">
                    <el-tab-pane label="案件摘要">
                      <el-tooltip placement="bottom" style="width: 50%;height: 100%">
                        <template #content>
                          <p class="newLine">
                            {{ item.legalBasis }}
                          </p>
                        </template>
                        <p class="hidden content"> {{ item.content }}</p>
                      </el-tooltip>
                    </el-tab-pane>
                    <el-tab-pane label="关联案件">
                      <el-container v-if="item.relatedCases.length>0"
                                    class="text-muted hidden">
                        <el-link v-for="(item,index) in item.relatedCases" :key="index" :href="item.url"
                                 :icon="Notebook"
                                 class="relatedCase" target="_blank">
                          {{ item.name }}
                        </el-link>
                      </el-container>
                      <el-container v-else>
                        <strong class="text-center text-xl" style="color: coral">暂无相关案件</strong>
                      </el-container>
                    </el-tab-pane>
                  </el-tabs>
                </el-col>
                <el-col :span="8" class="" style="text-align: left">
                  <el-tag v-if="item.court" size="large" type="success">法院：{{ item.court }}</el-tag>
                  <el-tag v-if="item.number" size="large">案号：{{ item.number }}</el-tag>
                  <el-tag v-if="item.type" size="large" type="danger">类型：{{ item.type }}</el-tag>
                  <el-tag v-if="item.process" size="large" type="info">程序：{{ item.process }}</el-tag>
                  <el-tag v-if="item.cause" size="large" type="warning">案由：{{ item.cause }}</el-tag>
                </el-col>
              </el-row>

            </template>
          </el-card>
        </el-carousel-item>
      </el-carousel>
      <pagination
        v-show="total>0"
        v-model:limit="advancedForm.pageSize"
        v-model:page="advancedForm.pageNum"
        :total="total"
        @pagination="handleSearch"
      />
    </el-main>
    <!--  高级检索条件选择对话框-->
    <el-dialog
      v-model="dialogVisible"
      align-center
      draggable
      title="检索条件"
      width="30%"
    >
      <el-form
        ref="advancedFormRef"
        :inline="true"
        :label-width="formLabelWidth"
        :model="advancedForm"
        class="form-inline"
        label-position="top"
      >
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="案件名称" label-width="80" prop="name">
              <el-input v-model="advancedForm.name"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审判法院" label-width="80" prop="court">
              <el-input v-model="advancedForm.court"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="文书类型" label-width="80" prop="type">
              <el-select v-model="advancedForm.type">
                <el-option
                  v-for="item in doc_case_type"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="案由" label-width="80" prop="cause">
              <el-select v-model="advancedForm.cause">
                <el-option
                  v-for="item in doc_case_cause"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="案号" label-width="40" prop="number">
              <el-input v-model="advancedForm.number"></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="审理程序" label-width="80" prop="process">
              <el-input v-model="advancedForm.process"/>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="10">
          <el-col :span="12">
            <el-form-item label="判决日期" prop="judgeDate">
              <el-date-picker
                v-model="advancedForm.judgeDate"
                placeholder="选择日期"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="公开日期" prop="pubDate">
              <el-date-picker
                v-model="advancedForm.pubDate"
                placeholder="选择日期"
                type="date"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <!--        <el-row :gutter="10">-->
        <!--          <el-col :span="12">-->
        <!--            <el-form-item label="法律依据" prop="legalBasis">-->
        <!--              <el-input v-model="advancedForm.legalBasis"></el-input>-->
        <!--            </el-form-item>-->
        <!--          </el-col>-->
        <!--          <el-col :span="12">-->
        <!--            <el-form-item label="当事人" prop="party">-->
        <!--              <el-input v-model="advancedForm.party"></el-input>-->
        <!--            </el-form-item>-->
        <!--          </el-col>-->
        <!--        </el-row>-->
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button icon="Close" @click="clearForm()">取消</el-button>
        <el-button icon="Search" type="primary" @click="handleSearch()">
          确认
        </el-button>
      </span>
      </template>
    </el-dialog>
  </el-container>


</template>

<script name="Case" setup>
import {getCurrentInstance, reactive, ref, toRefs} from 'vue';
import {pageCase, getCase, listCase} from "@/api/retrieve/case";
import {ElInput, ElHeader, ElMain, ElFooter} from 'element-plus';
import {Edit, Notebook} from "@element-plus/icons-vue";

const {proxy} = getCurrentInstance();

// 创建一个响应式的搜索文本
const headerShow = ref(true);
const dialogVisible = ref(false);
const advancedFormRef = ref(null);
const formLabelWidth = ref('100px');
const selectAdvance = ref("常规检索");
const caseList = ref([]);
const total = ref(0);


const advancedForm = ref({
  pageNum: 1,
  pageSize: 10,
  name: "",
  court: null,
  number: null,
  cause: null,
  type: null,
  process: null,
  judgeDate: null,
  pubDate: null,
  legalBasis: null,
  party: null,
  content: name,
})


const {
  doc_case_cause,
  doc_case_type,
  crawler_source,
  crawl_common_status
} = proxy.useDict("doc_case_cause", "doc_case_type", "crawler_source", "crawl_common_status");

const handleAdvanced = () => {
  dialogVisible.value = true;
  proxy.resetForm("advancedFormRef");
}

const clearForm = () => {
  proxy.resetForm("advancedFormRef");
  dialogVisible.value = false;
  selectAdvance.value = "常规检索";
}

const getList = () => {
  advancedForm.value.pageSize = 10;
  listCase(advancedForm.value).then(res => {
    total.value = res.data.length
  })
}

const handleSearch = () => {
  // 打开遮罩层
  proxy?.$modal.loading("正在检索数据，请稍后...");
  headerShow.value = false;
  dialogVisible.value = false;
  console.log(advancedForm.value)
  pageCase(advancedForm.value).then(res => {
    res.rows.forEach(item => {
      item.relatedCases = JSON.parse(item.relatedCases);
    })
    caseList.value = res.rows
    total.value = res.total
    // alert(JSON.stringify(caseList.value))
    if (res.total === 0) {
      proxy.$message.warning("未检索到相关数据");
    } else {
      proxy.$message.success("检索成功");
    }
    // 打开查询得到的列表
  }).catch(err => {
    proxy.$message.error(err.message);
  })
  // 关闭遮罩层
  proxy?.$modal.closeLoading();
}
const handleDetail = (item) => {
  // 打开遮罩层
  proxy?.$modal.loading("正在打开" + item.name + "案例文书，请稍后...");
  proxy.$router.push("/retrieve/case/caseDetail/" + item.id + "?" + `keyword=${advancedForm.value.name}`)
  // 关闭遮罩层
  proxy?.$modal.closeLoading();
}


</script>

<style lang="scss" scoped>

/* 隐藏垂直滚动条 */
*::-webkit-scrollbar {
  width: 0;
}

/* 隐藏水平滚动条 */
*::-webkit-scrollbar {
  height: 0;
}

.toolbar {
  position: absolute;
  top: 10px;
  right: 10px;

}


.horFlex {
  display: flex;
  justify-items: center;
  justify-content: space-around;
  align-content: center;
  align-items: center;

  .el-tag {
    margin: 20px;
  }
}

.verFlex {
  display: flex;
  justify-content: space-between; /* 可选，如果需要同时水平居中对齐 */
  justify-items: center;
  //align-items: center; /* 主要用于垂直居中对齐，此属性适用于单行布局下的子元素 */
  /* 对于多行布局且需要整个内容区域垂直居中，可以使用： */
  flex-direction: column;
  align-content: space-between; /* 在多行之间进行垂直居中对齐 */
  height: 100%;

  .el-tag {
    margin: 5px 0;
    text-align: left;
  }
}

.hidden {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 8; /* 表示显示3行 */
  overflow: hidden;
  max-height: 100%; /* 设置一个固定的高度或者最大高度（例如：max-height） */
}

.newLine {
  width: 20vw; /* 设置一个宽度来触发换行 */
  overflow-wrap: break-word; /* 当内容溢出容器边界时允许单词内部断行 */
  word-break: break-word; /* 对于不区分单词的脚本（如中文、日文等），也可以使用此属性 */
  white-space: normal; /* 这是默认值，保持常规空白处理和换行行为 */
}

.search {
  margin: 60px auto;
  width: 55%;
  display: flex;
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中 */

}

.keyword {
  overflow: hidden; /* 隐藏超出输入框的内容 */
  text-overflow: ellipsis; /* 文本溢出时显示省略号 */
  white-space: nowrap;

  /* 默认样式 */
  transition: all 0.3s ease;
}

.el-tag {
  font-weight: bold;
  font-size: small;
  text-align: left;
  margin: 10px 0;
}

.search .keyword:hover {
  transform: scale(1.2);
}

.input-with-select .el-input-group__prepend {
  background-color: var(--el-fill-color-blank);
}

.form-inline .el-input {
  --el-input-width: 220px;
}

.form-inline .el-select {
  --el-select-width: 220px;
}

.content {
  text-shadow: #ebeee8 1px 10px 1px;
  text-indent: 2em;
}

.relatedCase {
  display: block;
  margin-top: 10px;
}
</style>
