<script setup>
import {
  listRegulation,
  getRegulation,
  delRegulation,
  addRegulation,
  updateRegulation, saveProcessRegulation, syncAllRegulation,
} from '@/api/manage/regulation'
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {parseTime} from "@/utils/ruoyi";
import {miningLaw, reviseLaw} from "@/api/manage/process";
import {Edit, Switch, UploadFilled} from "@element-plus/icons-vue";
import {getToken} from "@/utils/auth";

const {proxy} = getCurrentInstance();

const {
  crawler_source,
  crawl_common_status,
  law_type
} = proxy.useDict('crawler_source', 'crawl_common_status', 'law_type')

const regulationList = ref([])
const regulationListStriped = ref([])
const regulationListOrigin = ref([])
const total = ref(0)
const loading = ref(true)
const buttonLoading = ref(false)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)

/*** 法条导入参数 */
const upload = reactive({
  // 是否显示弹出层（案例导入）
  open: false,
  // 弹出层标题（案例导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 是否更新已经存在的案例数据
  updateSupport: 0,
  // 设置上传的请求头部
  headers: {Authorization: "Bearer " + getToken()},
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/manage/regulation/importData"
});

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    type: null,
    isValidity: null,
    releaseOrganization: null,
    sourceId: null,
    status: null,
    isMining: 0
  },
  rules: {
    id: [],
    name: [
      {required: true, message: '法规名称不能为空', trigger: 'blur'},
    ],
    field: [],
    type: [],
    url: [
      {required: true, message: '原始链接不能为空', trigger: 'blur'},
    ],
    isValidity: [
      {required: true, message: '有效性不能为空', trigger: 'blur'},
    ],
    releaseDate: [
      {required: true, message: '发布日期不能为空', trigger: 'blur'},
    ],
    executeDate: [],
    releaseOrganization: [],
    content: [
      {required: true, message: '法规正文不能为空', trigger: 'blur'},
    ],
    sourceId: [
      {required: true, message: '法规来源不能为空', trigger: 'change'},
    ],
    structure: [],
    reviseNum: [],
    status: [
      {required: true, message: '状态不能为空', trigger: 'change'},
    ],
  },
  processData: [],
  processDataForm: {
    id: null,
    content: '',
    stripContent: '',
    extra: '',
  },
  processDataStage: [],
  extra: {
    keyword: '',
    field: '',
    type: '',
    organization: '',
    release: '',
    execute: '',
    basis: [],
    scope: '',
    main: [],
    abstract: ''
  }
})

const {queryParams, form, rules, processData, processDataForm, processDataStage, extra} = toRefs(data);


const title = ref('')
const open = ref(false)
const processDialog = ref(false)
const defaultTab = ref(0)
const defaultListTab = ref(1)
const currentTab = ref(1);

const processStep = ref(0)
const openContent = ref(false)

const queryForm = ref(null)
const dialogForm = ref(null)

//法条数据清洗挖掘
const handleProcess = () => {
  // 浅拷贝
  processData.value = []
  processDataStage.value = []
  // console.log(processData.value)
  const toAdd = []
  let tempList
  getList()
  if (defaultListTab.value === 1) {
    tempList = regulationListOrigin.value
  } else if (defaultListTab.value === 2) {
    tempList = regulationListStriped.value
  } else {
    tempList = regulationList.value
  }
  tempList.forEach(item => {
    // const exist = processData.value.findIndex(data => item.id === data.id) !== -1
    if (ids.value.includes(item.id)) {
      // 判断extra是否是非空字符串，是则需要解析，否则无需解析
      if ((item.extra !== "" || item.extra !== null) && typeof item.extra === 'string') {
        // 解析json字符串
        item.extra = JSON.parse(item.extra);
      } else {
        item.extra = extra.value
      }
      toAdd.push(item); // 将元素添加到新数组中
      // 将解析后的json对象还原为json字符串
      // item.extra = JSON.stringify(item.extra);
    }
  })
  processData.value = processData.value.concat(toAdd);
  processDialog.value = true
  // processData.value = regulationListCopy
  // console.log(ids.value, processData.value)
}

const handleProcessStage = (data) => {
  // todo: 按照正确格式对data进行处理，某些字段处理为数组
  console.log(data)
  // 检查数组中是否存在具有相同 id 的元素
  const existingIndex = processDataStage.value.findIndex(item => item.id === data.id);
  if (existingIndex !== -1) {
    // 如果找到，更新数组中的元素
    processDataStage.value[existingIndex] = {
      id: data.id,
      content: data.content,
      stripContent: data.stripContent,
      extra: JSON.stringify(data.extra),
    };
  } else {
    // 如果没有找到，将新元素添加到数组中
    processDataStage.value.push({
      id: data.id,
      content: data.content,
      stripContent: data.stripContent,
      extra: JSON.stringify(data.extra),
    });
  }
  console.log(processDataStage.value)
  ElMessage.success(`数据<${data.name}>暂存成功`)
}
const handleProcessSubmit = () => {
  if (processDataStage.value.length === 0) {
    ElMessage.error("请先暂存已修改的数据")
    return
  }
  console.log(processDataStage.value)
  saveProcessRegulation(processDataStage.value).then(res => {
    if (res.code === 200) {
      // 数据更新重新获取
      getList()
      ElMessage.success(res.msg)
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const handleProcessNext = () => {
  // todo 数据清洗挖掘下一步按钮回调函数
  if (processStep.value++ > 0) processStep.value = 1
  console.log(processStep.value)

}

const handleProcessPrev = () => {
  // todo 数据清洗挖掘上一步按钮回调函数
  if (processStep.value-- < 1) processStep.value = 0
  console.log(processStep.value)
}

const handleRemoveTab = (targetName) => {
  // todo 数据清洗挖掘tab页删除回调函数
  // 找到要删除的标签页的索引
  const index = targetName;
  if (index !== 0) {
    // 从数组中移除标签页
    processData.value.splice(index, 1);
  }
  // 如果删除的是当前选中的标签页，需要更新 defaultTab
  if (defaultTab.value === targetName) {
    // 设置新的 defaultTab，例如选中第一个标签页
    defaultTab.value = 0;
  }
}

const handleProcessStrip = (data) => {
  // todo 调用数据清洗接口，并将结果更新到processData中
  proxy.$modal.loading('数据清洗中，请稍后...')
  console.log(data)
  loading.value = true
  reviseLaw(data.id).then(res => {
    if (res.code === 200) {
      // 数据更新重新获取
      proxy.$modal.closeLoading()
      data.stripContent = res.data.stripContent
      ElMessage.success(res.msg)
    } else {
      ElMessage.error(res.data)
    }
    loading.value = false
  })
}

const handleProcessMining = (data) => {
  // todo 调用数据挖掘接口，并将结果更新到processData中
  proxy.$modal.loading('数据挖掘中，请稍后...')
  console.log(data)
  loading.value = true
  miningLaw(data.id).then(res => {
    if (res.code === 200) {
      // 数据更新重新获取
      proxy.$modal.closeLoading()
      data.extra = JSON.parse(res.data.extra)
      console.log(data.extra, res.data.extra)
      ElMessage.success(res.msg)
    } else {
      ElMessage.error(res.data)
    }
    loading.value = false
  })
}

const resetProcess = (data) => {
  getRegulation(data.id).then(res => {
    if (processStep.value === 0) {
      data.stripContent = res.data.stripContent
    } else {
      data.extra = JSON.parse(res.data.extra)
    }
  })

}


// ---------------分割线---------------
const getList = () => {
  loading.value = true
  listRegulation(queryParams.value).then((res) => {
    // regulationList.value = res.rows
    regulationList.value = res.rows.filter(item => {
      return item.isMining === "MININGED"
    })
    regulationListStriped.value = res.rows.filter(item => {
      return item.isMining === "STRIPED"
    })
    regulationListOrigin.value = res.rows.filter(item => {
      return item.isMining === "ORIGIN"
    })
    total.value = res.total
    loading.value = false
  })

}

const handleTabClick = (pane, ev) => {
  // todo 数据清洗挖掘tab页切换回调函数
  console.log(pane.props.name)
  if (pane.props.name === 1) {
    // 设置分页的总页数为ORIGIN的总页数
    queryParams.value.isMining = 0
  } else if (pane.props.name === 2) {
    // 设置分页的总页数为STRIPED的总页数
    queryParams.value.isMining = 1
  } else if (pane.props.name === 3) {
    // 设置分页的总页数为MININGED的总页数
    queryParams.value.isMining = 2
  }
  currentTab.value = queryParams.value.isMining
  getList()
}

const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

const resetQueryForm = () => {
  proxy.resetForm("queryForm")
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name: null,
    type: null,
    isValidity: null,
    releaseOrganization: null,
    sourceId: null,
    status: null,
    isMining: currentTab.value
  }

}

const resetQuery = () => {
  resetQueryForm()
  handleQuery()
}

const handleSelectionChange = selection => {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
  if (ids.value.length !== 0) {
    proxy.$modal.msgSuccess("已选中" + selection.length + "条数据");
  }
}

const resetForm = () => {
  proxy.resetForm("dialogForm")
  form.value = {
    id: undefined,
    name: undefined,
    field: undefined,
    type: undefined,
    url: undefined,
    isValidity: undefined,
    releaseDate: undefined,
    executeDate: undefined,
    releaseOrganization: undefined,
    content: undefined,
    sourceId: undefined,
    structure: undefined,
    reviseNum: undefined,
    status: undefined,
    isMining: undefined,
    createBy: undefined,
    createTime: undefined,
    updateBy: undefined,
    updateTime: undefined
  }
}

const cancelDialog = () => {
  open.value = false
  processDialog.value = false
  resetForm()
}

const handleAdd = () => {
  resetForm()
  open.value = true
  title.value = "添加法律法规"
}

const handleUpdate = row => {
  loading.value = true
  const id = row.id || ids.value
  getRegulation(id).then(response => {
    loading.value = false
    form.value = response.data
    open.value = true
    title.value = "修改法律法规"
  })
}

const MiningStatusMap = {
  'ORIGIN': 0,
  'STRIPED': 1,
  'MININGED': 2
}

const reviseForm = () => {
  form.value.isMining = MiningStatusMap[form.value.isMining]
}

const submitForm = () => {
  dialogForm.value.validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
        // 挖掘状态转换
        reviseForm()
        console.log(form.value)
        updateRegulation(form.value).then(response => {
          ElMessage.success("修改成功")
          open.value = false
          getList()
        }).finally(() => {
          buttonLoading.value = false
        })
      } else {
        addRegulation(form.value).then(response => {
          ElMessage.success("新增成功")
          open.value = false
          getList()
        }).finally(() => {
          buttonLoading.value = false
        })
      }
    }
  })
}

const handleDelete = row => {
  const idLs = row.id || ids.value
  ElMessageBox.confirm(`是否确认删除法律法规编号为"${idLs}"的数据项？`).then(() => {
    loading.value = true
    return delRegulation(ids)
  }).then(() => {
    loading.value = false
    getList()
    ElMessage.success("删除成功")
  }).catch(() => {
  }).finally(() => {
    loading.value = false
  })
}

/** 导入按钮操作 */
const handleImport = () => {
  upload.title = "案例导入";
  upload.open = true;
};

/** 下载模板操作 */
const importTemplate = () => {
  proxy.download("manage/regulation/importTemplate", {}, `law_template_${new Date().getTime()}.xlsx`);
};
/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
};
/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].handleRemove(file);
  proxy.$alert("<div style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'>" + response.msg + "</div>", "导入结果", {dangerouslyUseHTMLString: true});
  getList();
};

/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

const handleExport = () => {
  console.log({...queryParams.value})
  proxy.download('manage/regulation/export',
    {...queryParams.value}
    , `regulation_${new Date().getTime()}.xlsx`)
}

//全量同步法律法规
const handleSync = () => {
  ElMessageBox.confirm('是否确认同步所有数据项？').then(() => {
    loading.value = true
    return syncAllRegulation()
  }).then(() => {
    ElMessage.success('同步成功')
    getList()
  }).catch(() => {
  }).finally(() => {
    loading.value = false
  })
}

onMounted(() => {
  getList()
})
</script>

<template>
  <div class="app-container">
    <el-card class="card-box el-card" shadow="hover">
      <el-form v-show="showSearch" ref="queryForm" :inline="true" :model="queryParams" label-width="80px"
               size="default">
        <el-form-item label="法规名称" prop="name">
          <el-input v-model="queryParams.name" clearable placeholder="请输入法规名称" @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="法规类型" prop="type">
          <el-select v-model="queryParams.type"
                     clearable placeholder="请选择法规类型">
            <el-option v-for="dict in law_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="有效性" prop="isValidity">
          <el-select v-model="queryParams.isValidity" placeholder="请选择有效性" @change="handleQuery">
            <el-option v-for="dict in crawl_common_status" :key="dict.value" :label="dict.label"
                       :value="parseInt(dict.value)">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发布机关" prop="releaseOrganization">
          <el-input v-model="queryParams.releaseOrganization" clearable placeholder="请输入发布机关"
                    @keyup.enter="handleQuery"/>
        </el-form-item>
        <el-form-item label="法规来源" prop="sourceId">
          <el-select v-model="queryParams.sourceId" clearable placeholder="请选择法规来源">
            <el-option v-for="dict in crawler_source" :key="dict.value" :label="dict.label"
                       :value="dict.value"/>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" clearable placeholder="请选择状态">
            <el-option v-for="dict in crawl_common_status" :key="dict.value" :label="dict.label"
                       :value="dict.value"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button icon="Search" size="default" type="primary" @click="handleQuery">搜索</el-button>
          <el-button icon="Refresh" size="default" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button v-hasPermi="['manage:regulation:add']" icon="Plus" plain size="default" type="primary"
                     @click="handleAdd">新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['manage:regulation:edit']" :disabled="single" icon="Edit" plain size="default"
                     type="success"
                     @click="handleUpdate">修改
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['manage:regulation:remove']" :disabled="multiple" icon="Delete" plain
                     size="default" type="danger"
                     @click="handleDelete">删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:regulation:import']" icon="Upload" plain type="info" @click="handleImport"
          >导入
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['manage:regulation:export']" icon="Download" plain size="default" type="warning"
                     @click="handleExport">导出
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:regulation:sync']"
            icon="Plus"
            plain
            size="default"
            type="success"
            @click="handleSync"
          >全量同步
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:case:process']"
            :disabled="multiple"
            icon="Edit"
            size="default"
            type="primary"
            @click="handleProcess"
          >清洗挖掘
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

      <!--      数据列表-->
      <el-tabs v-model="defaultListTab" :tab-position="'right'" class="el-tabs" style="height: 500px"
               @tab-click="handleTabClick">
        <el-tab-pane :name="1" label="未清洗挖掘">
          <el-table
            v-loading="loading"
            :data="regulationListOrigin"
            :default-sort="{ prop: 'releaseDate', order: 'descending' }"
            border
            height="500"
            stripe
            style="width: 100%;text-align: center"
            table-layout="auto"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column align="center" label="序号" prop="id" type="index" width="150"/>
            <el-table-column align="center" fixed label="法规名称" prop="name" width="230">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.name }}</template>
                  <span class="hidden">{{ row.name }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <!--      <el-table-column align="center" label="领域类别" prop="field"/>-->
            <el-table-column align="center" label="法规类型" prop="type" width="180">
              <template #default="scope">
                <dict-tag v-if="scope.row.type!==null" :options="law_type" :value="scope.row.type" width="180"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="原始链接" prop="url" width="120">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">
                  <span class="hidden">{{ scope.row.url }}</span>
                </el-link>
              </template>
            </el-table-column>
            <el-table-column align="center" label="有效性" prop="isValidity" width="180">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.isValidity"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="发布日期" prop="releaseDate" sortable width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px;color: #1c84c6">{{ scope.row.releaseDate }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="实施日期" prop="executeDate" width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px;color: #b96060">{{ scope.row.executeDate }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="发布机关" prop="releaseOrganization" width="180"/>
            <!--      <el-table-column align="center" label="法规正文" prop="content"/>-->
            <el-table-column align="center" label="法规来源" prop="sourceId" width="180">
              <template #default="scope">
                <dict-tag :options="crawler_source" :value="scope.row.sourceId"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="法规结构" prop="structure" width="180"/>
            <el-table-column align="center" label="修改次数" prop="reviseNum" width="180"/>
            <el-table-column align="center" label="状态" prop="status" width="180">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="创建时间" prop="createTime" width="200">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="更新时间" prop="updateTime" width="200">
              <template #default="scope">
                <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center"
                             class-name="small-padding fixed-width"
                             fixed="right" label="操作" width="180">
              <template #default="scope">
                <el-button v-hasPermi="['manage:regulation:edit']" icon="Edit" size="default" type="text"
                           @click="handleUpdate(scope.row)">修改
                </el-button>
                <el-button v-hasPermi="['manage:regulation:remove']" icon="Delete" size="default" type="text"
                           width="180"
                           @click="handleDelete(scope.row)">删除
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty></el-empty>
            </template>
          </el-table>

        </el-tab-pane>
        <el-tab-pane :name="2" label="已清洗">
          <el-table
            v-loading="loading"
            :data="regulationListStriped"
            :default-sort="{ prop: 'releaseDate', order: 'descending' }"
            border
            height="500"
            stripe
            style="width: 100%;text-align: center"
            table-layout="auto"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column align="center" label="序号" prop="id" type="index" width="150"/>
            <el-table-column align="center" fixed label="法规名称" prop="name" width="230">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.name }}</template>
                  <span class="hidden">{{ row.name }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <!--      <el-table-column align="center" label="领域类别" prop="field"/>-->
            <el-table-column align="center" label="法规类型" prop="type" width="180">
              <template #default="scope">
                <dict-tag v-if="scope.row.type!==null" :options="law_type" :value="scope.row.type" width="180"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="原始链接" prop="url" width="120">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">
                  <span class="hidden">{{ scope.row.url }}</span>
                </el-link>
              </template>
            </el-table-column>
            <el-table-column align="center" label="有效性" prop="isValidity" width="180">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.isValidity"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="发布日期" prop="releaseDate" sortable width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px;color: #1c84c6">{{ scope.row.releaseDate }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="实施日期" prop="executeDate" width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px;color: #b96060">{{ scope.row.executeDate }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="发布机关" prop="releaseOrganization" width="180"/>
            <!--      <el-table-column align="center" label="法规正文" prop="content"/>-->
            <el-table-column align="center" label="法规来源" prop="sourceId" width="180">
              <template #default="scope">
                <dict-tag :options="crawler_source" :value="scope.row.sourceId"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="法规结构" prop="structure" width="180"/>
            <el-table-column align="center" label="修改次数" prop="reviseNum" width="180"/>
            <el-table-column align="center" label="状态" prop="status" width="180">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="创建时间" prop="createTime" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="更新时间" prop="updateTime" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center"
                             class-name="small-padding fixed-width"
                             fixed="right" label="操作" width="180">
              <template #default="scope">
                <el-button v-hasPermi="['manage:regulation:edit']" icon="Edit" size="default" type="text"
                           @click="handleUpdate(scope.row)">修改
                </el-button>
                <el-button v-hasPermi="['manage:regulation:remove']" icon="Delete" size="default" type="text"
                           width="180"
                           @click="handleDelete(scope.row)">删除
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty></el-empty>
            </template>
          </el-table>


        </el-tab-pane>
        <el-tab-pane :name="3" label="已挖掘">
          <el-table
            v-loading="loading"
            :data="regulationList"
            :default-sort="{ prop: 'releaseDate', order: 'descending' }"
            border
            height="500"
            stripe
            style="width: 100%;text-align: center"
            table-layout="auto"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column align="center" label="序号" prop="id" type="index" width="150"/>
            <el-table-column align="center" fixed label="法规名称" prop="name" width="230">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.name }}</template>
                  <span class="hidden">{{ row.name }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <!--      <el-table-column align="center" label="领域类别" prop="field"/>-->
            <el-table-column align="center" label="法规类型" prop="type" width="180">
              <template #default="scope">
                <dict-tag v-if="scope.row.type!==null" :options="law_type" :value="scope.row.type" width="180"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="原始链接" prop="url" width="120">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">
                  <span class="hidden">{{ scope.row.url }}</span>
                </el-link>
              </template>
            </el-table-column>
            <el-table-column align="center" label="有效性" prop="isValidity" width="180">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.isValidity"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="发布日期" prop="releaseDate" sortable width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px;color: #1c84c6">{{ scope.row.releaseDate }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="实施日期" prop="executeDate" width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px;color: #b96060">{{ scope.row.executeDate }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column align="center" label="发布机关" prop="releaseOrganization" width="180"/>
            <!--      <el-table-column align="center" label="法规正文" prop="content"/>-->
            <el-table-column align="center" label="法规来源" prop="sourceId" width="180">
              <template #default="scope">
                <dict-tag :options="crawler_source" :value="scope.row.sourceId"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="法规结构" prop="structure" width="180"/>
            <el-table-column align="center" label="修改次数" prop="reviseNum" width="180"/>
            <el-table-column align="center" label="状态" prop="status" width="180">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column align="center" label="创建时间" prop="createTime" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center" label="更新时间" prop="updateTime" width="180">
              <template #default="scope">
                <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
              </template>
            </el-table-column>
            <el-table-column align="center"
                             class-name="small-padding fixed-width"
                             fixed="right" label="操作" width="180">
              <template #default="scope">
                <el-button v-hasPermi="['manage:regulation:edit']" icon="Edit" size="default" type="text"
                           @click="handleUpdate(scope.row)">修改
                </el-button>
                <el-button v-hasPermi="['manage:regulation:remove']" icon="Delete" size="default" type="text"
                           width="180"
                           @click="handleDelete(scope.row)">删除
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty></el-empty>
            </template>
          </el-table>
        </el-tab-pane>
      </el-tabs>

      <!-- 添加或修改法律法规对话框 -->
      <el-dialog v-model="open" :title="title" align-center width="50%">
        <el-form ref="dialogForm" :model="form" :rules="rules" label-width="120px">
          <el-form-item label="法规名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入法规名称"/>
          </el-form-item>
          <el-form-item label="领域类别" prop="field">
            <el-input v-model="form.field" placeholder="请输入领域类别"/>
          </el-form-item>
          <el-form-item label="法规类型" prop="type">
            <el-select v-model="form.type"
                       placeholder="请选择法规类型">
              <el-option v-for="dict in law_type" :key="dict.value" :label="dict.label"
                         :value="dict.value"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="原始链接" prop="url">
            <el-input v-model="form.url" placeholder="请输入原始链接"/>
          </el-form-item>
          <el-form-item label="有效性" prop="isValidity">
            <el-select v-model="form.isValidity" placeholder="请选择有效性">
              <el-option v-for="dict in crawl_common_status" :key="dict.value" :label="dict.label"
                         :value="parseInt(dict.value)"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发布日期" prop="releaseDate">
            <el-date-picker v-model="form.releaseDate" clearable placeholder="请选择发布日期" type="date"
                            value-format="YYYY-MM-DD">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="实施日期" prop="executeDate">
            <el-date-picker v-model="form.executeDate" clearable placeholder="请选择实施日期" type="date"
                            value-format="YYYY-MM-DD">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="发布机关" prop="releaseOrganization">
            <el-input v-model="form.releaseOrganization" placeholder="请输入发布机关"/>
          </el-form-item>
          <el-form-item label="法规正文" prop="content">
            <el-link href="javascript:void(0);" type="primary" @click="openContent=true">进入法条正文</el-link>
            <el-dialog v-model="openContent" title="输入法条正文">
              <q-editor v-model="form.content" :min-height="300"/>
            </el-dialog>
          </el-form-item>
          <el-form-item label="法规来源" prop="sourceId">
            <el-select v-model="form.sourceId" placeholder="请选择法规来源">
              <el-option v-for="dict in crawler_source" :key="dict.value" :label="dict.label"
                         :value="parseInt(dict.value)"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="法规结构" prop="structure">
            <el-input v-model="form.structure" placeholder="请输入法规结构"/>
          </el-form-item>
          <el-form-item label="修改次数" prop="reviseNum">
            <el-input v-model="form.reviseNum" placeholder="请输入修改次数"/>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-radio-group v-model="form.status">
              <el-radio v-for="dict in crawl_common_status" :key="dict.value" :label="parseInt(dict.value)">
                {{ dict.label }}
              </el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer class="dialog-footer">
          <el-button :loading="buttonLoading" type="info" @click="cancelDialog">取 消</el-button>
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        </template>
      </el-dialog>
      <!-- 法条导入对话框 -->
      <el-dialog v-model="upload.open" :title="upload.title" append-to-body width="400px">
        <el-upload
          ref="uploadRef"
          :action="upload.url + '?updateSupport=' + upload.updateSupport"
          :auto-upload="false"
          :disabled="upload.isUploading"
          :headers="upload.headers"
          :limit="1"
          :on-progress="handleFileUploadProgress"
          :on-success="handleFileSuccess"
          accept=".xlsx, .xls"
          drag
        >
          <el-icon class="el-icon--upload">
            <upload-filled/>
          </el-icon>
          <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
          <template #tip>
            <div class="el-upload__tip text-center">
              <div class="el-upload__tip">
                <el-checkbox v-model="upload.updateSupport"/>
                是否更新已经存在的法律法规数据
              </div>
              <span>仅允许导入xls、xlsx格式文件。</span>
              <el-link :underline="false" style="font-size:12px;vertical-align: baseline;" type="primary"
                       @click="importTemplate">下载模板
              </el-link>
            </div>
          </template>
        </el-upload>
        <template #footer>
          <div class="dialog-footer">
            <el-button type="primary" @click="submitFileForm">确 定</el-button>
            <el-button @click="upload.open = false">取 消</el-button>
          </div>
        </template>
      </el-dialog>
      <!-- 清洗挖掘对话框 -->
      <el-dialog v-model="processDialog" :fullscreen="true" title="法律法规数据清洗挖掘" width="100%">
        <el-steps :active="processStep" align-center class="mb-10" finish-status="success">
          <el-step :icon="Edit" description="对数据格式进行格式化并去除异常字符" title="Step 1：数据清洗"/>
          <el-step :icon="UploadFilled" :status="'finish'" description="对数据进行挖掘分析提取潜在信息"
                   title="Step 2：数据挖掘"/>
        </el-steps>
        <el-tabs v-model="defaultTab" :closable="true" :tab-position="'left'" @tab-remove="handleRemoveTab">
          <el-card :shadow="'hover'" class="mt-3 shadow-sm w-100%">
            <el-tab-pane v-for="(data,index) in processData" :key="data.id" :label="data.name" :name="index">
              <template #label>
                <el-tooltip :content="data.name" class="item" effect="dark" placement="top">
                  <span class="text-overflow overflow-hidden whitespace-nowrap text-sm">{{
                      data.name.substring(0, 15) + '...'
                    }}</span>
                </el-tooltip>
              </template>
              <el-form :label-position="'left'" :model="data" :size="'default'" class="flex">
                <el-col :span="12" class="mr-2">
                  <div v-if="processStep===0">
                    <el-header>
                      原始正文
                    </el-header>
                    <el-form-item>
                      <el-input v-model="data.content" :autosize="{ minRows: 25, maxRows: 25 }" resize="vertical"
                                type="textarea"/>
                    </el-form-item>
                  </div>
                  <div v-else-if="processStep===1">
                    <el-header>
                      修正正文
                    </el-header>
                    <el-input v-model="data.stripContent" :autosize="{ minRows: 25, maxRows: 25 }" resize="vertical"
                              type="textarea"/>
                  </div>
                </el-col>
                <el-col :span="12">
                  <div v-if="processStep===0">
                    <el-header>
                      修正正文
                    </el-header>
                    <el-input v-model="data.stripContent" :autosize="{ minRows: 25, maxRows: 25 }" type="textarea"/>
                  </div>
                  <div v-else-if="processStep===1">
                    <el-header>
                      语义信息
                    </el-header>
                    <el-card :shadow="'never'">
                      <el-form-item label="所属领域">
                        <el-input v-model="data.extra.field"/>
                      </el-form-item>
                      <el-form-item label="颁布组织">
                        <el-input v-model="data.extra.organization" :prefix-icon="Switch"/>
                      </el-form-item>
                      <el-form-item label="适用范围">
                        <el-input v-model="data.extra.scope" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="法条依据">
                        <el-input v-model="data.extra.basis" :autosize="true" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="主要内容">
                        <el-input v-model="data.extra.main" :autosize="true" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="法条摘要">
                        <el-input v-model="data.extra.summary" :autosize="true" type="textarea"/>
                      </el-form-item>
                    </el-card>
                  </div>
                </el-col>
              </el-form>
              <div class="flex justify-end items-end mt-3"> <!-- 添加 justify-end 和 items-end 类 -->
                <el-button @click="cancelDialog">取 消</el-button>
                <el-button :plain="true" type="primary" @click="resetProcess(data)">还原</el-button>
                <el-button :disabled="processStep <= 0" type="primary"
                           @click="handleProcessPrev">
                  上一步
                </el-button>
                <el-button :disabled="processStep >= 1" type="primary"
                           @click="handleProcessNext">
                  下一步
                </el-button>
                <el-button :disabled="processStep !== 0" :loading="buttonLoading" type="warning"
                           @click="handleProcessStrip(data)">
                  开始清洗
                </el-button>
                <el-button :disabled="processStep !== 1" :loading="buttonLoading" type="warning"
                           @click="handleProcessMining(data)">
                  开始挖掘
                </el-button>
                <el-button type="success"
                           @click="handleProcessStage(data)">
                  暂 存
                </el-button>
                <el-button :disabled="processDataStage.length===0" type="warning"
                           @click="handleProcessSubmit">
                  提 交
                </el-button>

              </div>
            </el-tab-pane>


          </el-card>

        </el-tabs>


      </el-dialog>
    </el-card>
    <pagination
      v-show="total>0"
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNum"
      :total="total"
      @pagination="getList"
    />
  </div>
</template>

<style lang="scss" scoped>
.hidden {
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 1; /* 表示显示3行 */
  overflow: hidden;
}

.newLine * {
  width: 10px; /* 设置一个宽度来触发换行 */
  overflow-wrap: break-word; /* 当内容溢出容器边界时允许单词内部断行 */
  word-break: break-word; /* 对于不区分单词的脚本（如中文、日文等），也可以使用此属性 */
  white-space: normal; /* 这是默认值，保持常规空白处理和换行行为 */
}

.el-header {
  text-align: center;
  font-size: large;
  font-weight: bold;
}
</style>
