<script setup>
import {
  listCase,
  getCase,
  delCase,
  addCase,
  updateCase, saveProcessCase, syncAllCase,
} from '@/api/manage/case'
import {download} from '@/utils/request'
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {getToken} from "@/utils/auth";
import QEditor from "@/components/Editor/index.vue";
import {
  Back,
  Brush, Check, CirclePlus,
  Close,
  Edit, HelpFilled,
  Picture,
  Refresh,
  Right,
  Switch,
  UploadFilled,
  UserFilled
} from "@element-plus/icons-vue";
import {Icon} from '@iconify/vue';
import {miningCase, reviseCase} from "@/api/manage/process";
import FileUpload from "@/components/FileUpload/index.vue";


const {proxy} = getCurrentInstance();
const {
  doc_case_cause,
  doc_case_type,
  crawler_source,
  crawl_common_status
} = proxy.useDict("doc_case_cause", "doc_case_type", "crawler_source", "crawl_common_status");


const caseList = ref([]);
const caseListStriped = ref([]);
const caseListOrigin = ref([]);

const total = ref(0);
const loading = ref(true);
const buttonLoading = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);
const currentTab = ref(1);

const uploadPdf = ref({
  // 是否显示弹出层（PDF上传）
  open: false,
  // 弹出层标题（PDF上传）
  title: "PDF文件上传",
  // 是否禁用上传
  isUploading: true,
  // 设置上传的请求头部
  headers: {Authorization: "Bearer " + getToken()},
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "/manage/case/uploadPdf"
})

/*** 案例导入参数 */
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
  url: import.meta.env.VITE_APP_BASE_API + "/manage/case/importData"
});


const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    name: null,
    court: null,
    number: null,
    cause: null,
    type: null,
    process: null,
    label: null,
    sourceId: null,
    judgeDate: null,
    pubDate: null,
    legalBasis: null,
    party: null,
    status: null,
    isMining: 0
  },

  form: {},

  rules: {
    id: [
      {required: true, message: '案件主键id不能为空', trigger: 'blur'},
    ],
    name: [
      {required: true, message: '案件名称不能为空', trigger: 'blur'},
    ],
    court: [
      {required: true, message: '审判法院不能为空', trigger: 'blur'},
    ],
    number: [
      {required: true, message: '案号不能为空', trigger: 'blur'},
    ],
    url: [
      {pattern: /http:\/\/|https:\/\//, message: '请输入正确的链接', trigger: 'blur'},
    ],
    cause: [
      {required: true, message: '案由不能为空', trigger: 'change'},
    ],
    type: [
      {required: true, message: '文书类型不能为空', trigger: 'change'},
    ],
    process: [],
    label: [],
    content: [
      {required: true, message: '案件正文不能为空', trigger: 'blur'},
    ],
    sourceId: [
      {required: true, message: '案件来源不能为空', trigger: 'change'},
    ],
    judgeDate: [
      {required: true, message: '判决日期不能为空', trigger: 'blur'},
    ],
    pubDate: [],
    legalBasis: [],
    party: [],
    relatedCases: [],
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
    plea: '',
    label: '',
    plai: '',
    defe: '',
    article: '',
    party: {
      plaintiff: '',
      defendant: '',
    },
    fact: '',
    note: '',
    summary: ''
  }
})

const {queryParams, form, rules, processData, processDataForm, processDataStage, extra} = toRefs(data);


const title = ref('')
const open = ref(false)
const processDialog = ref(false)
const defaultListTab = ref(1)
const defaultTab = ref(0)
const processStep = ref(0)
const openContent = ref(false)

const queryForm = ref(null)
const dialogForm = ref(null)


//案例数据清洗挖掘
const handleProcess = () => {
  // 浅拷贝
  // todo 第二次打开extra为空
  processData.value = []
  processDataStage.value = []
  let toAdd
  let tempList;
  getList()
  if (defaultListTab.value === 1) {
    tempList = caseListOrigin.value
  } else if (defaultListTab.value === 2) {
    tempList = caseListStriped.value
  } else {
    tempList = caseList.value
  }
  // console.log(tempList)
  toAdd = tempList.filter(item => ids.value.includes(item.id)).map(item => {
    if ((item.extra !== "" || item.extra !== null) && typeof item.extra === 'string') {
      // 解析json字符串
      item.extra = JSON.parse(item.extra);
    } else {
      item.extra = extra.value
    }
    return item;
  })
  // tempList.value.forEach(item => {
  //   const existIndex = processData.value.findIndex(data => item.id === data.id)
  //   const exist = existIndex !== -1
  //
  //   // alert(JSON.stringify(item.extra))
  //   // 判断extra是否是非空字符串，是则需要解析，否则无需解析
  //   if ((item.extra !== "" || item.extra !== null) && typeof item.extra === 'string') {
  //     // 解析json字符串
  //     item.extra = JSON.parse(item.extra);
  //   } else {
  //     item.extra = extra.value
  //   }
  //   // 更新已存在于ProcessData的extra 字段
  //   if (ids.value.includes(item.id)) {
  //     toAdd.push(item); // 将元素添加到新数组中
  //     // 将解析后的json对象还原为json字符串
  //     // item.extra = JSON.stringify(item.extra);
  //   }
  // })
  processData.value = processData.value.concat(toAdd);
  processDialog.value = true
}

const handleProcessStage = (data) => {
  // 检查数组中是否存在具有相同 id 的元素
  const existingIndex = processDataStage.value.findIndex(item => item.id === data.id);
  if (existingIndex !== -1) {
    // 如果找到，更新数组中的元素
    processDataStage.value[existingIndex] = {
      id: data.id,
      name: data.name,
      content: data.content,
      stripContent: data.stripContent,
      extra: JSON.stringify(data.extra),
      relatedCases: JSON.stringify(data.relatedCases),
    };
  } else {
    // 如果没有找到，将新元素添加到数组中
    processDataStage.value.push({
      id: data.id,
      name: data.name,
      content: data.content,
      stripContent: data.stripContent,
      extra: JSON.stringify(data.extra),
      relatedCases: JSON.stringify(data.relatedCases),
    });
  }
  console.log(processDataStage.value)
  ElMessage.success(`数据<${data.name}>暂存成功，已经暂存${processDataStage.value.length}条数据`)
}
const handleProcessSubmit = () => {
  if (processDataStage.value.length === 0) {
    ElMessage.error("请先暂存已修改的数据")
    return
  }
  console.log(processDataStage.value)
  saveProcessCase(processDataStage.value).then(res => {
    if (res.code === 200) {
      // 数据更新重新获取
      ElMessage.success(res.msg)
    } else {
      ElMessage.error(res.msg)
    }
    getList()
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
  reviseCase(data.id).then(res => {
    if (res.code === 200) {
      // 数据更新重新获取
      proxy?.$modal.closeLoading();
      data.stripContent = res.data.stripContent
      ElMessage.success(res.msg)
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const handleProcessMining = (data) => {
  // todo 调用数据挖掘接口，并将结果更新到processData中
  proxy.$modal.loading('数据挖掘中，请稍后...')
  console.log(data)
  miningCase(data.id).then(res => {
    if (res.code === 200) {
      // 数据更新重新获取
      proxy?.$modal.closeLoading();
      data.extra = JSON.parse(res.data.extra)
      ElMessage.success(res.msg)
    } else {
      ElMessage.error(res.msg)
    }
  })
}

const resetProcess = (data) => {
  getCase(data.id).then(res => {
    if (processStep.value === 0) {
      data.stripContent = res.data.stripContent
    } else {
      data.extra = JSON.parse(res.data.extra)
    }
  })

}


//--------------------分割线--------------------
const handleOpenContent = () => {
  openContent.value = true
  // console.log(form.value)
}

const getList = () => {
  loading.value = true
  // console.log(queryParams.value)
  listCase(queryParams.value).then(res => {
    // caseList.value = res.rows
    caseList.value = res.rows.filter(item => {
      return item.isMining === "MININGED"
    })
    caseListStriped.value = res.rows.filter(item => {
      return item.isMining === "STRIPED"
    })
    caseListOrigin.value = res.rows.filter(item => {
      return item.isMining === "ORIGIN"
    })
    loading.value = false
    total.value = res.total

  })

}


const handleTabClick = (pane, ev) => {
  // todo 数据清洗挖掘tab页切换回调函数
  // console.log(pane.props.name)
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
  if (queryForm.value) {
    queryForm.value.resetFields()
    queryForm.value.clearValidate()
  }
  proxy.resetForm("queryForm");
  queryParams.value = {
    pageNum: 1,
    pageSize: 10,
    name: null,
    court: null,
    number: null,
    cause: null,
    type: null,
    process: null,
    label: null,
    sourceId: null,
    judgeDate: null,
    pubDate: null,
    legalBasis: null,
    party: null,
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
  if (dialogForm.value) {
    dialogForm.value.resetFields()
    dialogForm.value.clearValidate()
  }
  proxy.resetForm("dialogForm");
  form.value = {
    id: null,
    name: null,
    court: null,
    number: null,
    url: null,
    cause: null,
    type: null,
    process: null,
    label: null,
    content: null,
    sourceId: null,
    judgeDate: null,
    pubDate: null,
    legalBasis: null,
    party: null,
    relatedCases: null,
    status: null,
    createBy: null,
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
  title.value = '添加司法案例'
}

const handleUpdate = row => {
  loading.value = true
  resetForm()
  const id = row.id || ids.value
  getCase(id).then(res => {
    loading.value = false
    form.value = res.data
    open.value = true
    title.value = '修改司法案例'
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
        updateCase(form.value).then(() => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        }).finally(() => {
          buttonLoading.value = false
        })
      } else {
        addCase(form.value).then(() => {
          ElMessage.success('新增成功')
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
  ElMessageBox.confirm(`是否确认删除司法案例编号为"${idLs}"的数据项？`).then(() => {
    loading.value = true
    return delCase(idLs)
  }).then(() => {
    loading.value = false
    getList()
    ElMessage.success('删除成功')
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
  proxy.download("manage/case/importTemplate", {}, `case_template_${new Date().getTime()}.xlsx`);
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
  console.log({
    ...queryParams.value
  }, typeof {
    ...queryParams.value
  })
  proxy.download('manage/case/export',
    {...queryParams.value}
    , `case_${new Date().getTime()}.xlsx`)
}

const handleExportSelected = () => {
  // const idLs = []
  if (!ids.value.length) {
    ElMessage.warning('请选择要导出的项目')
    return
  }
  // for (let id in ids.value) {
  //   idLs.push(parseInt(ids.value[id]))
  // }
  // console.log(idLs, typeof idLs)
  download('manage/case/exportSelected',
    ids.value
    , `case_${new Date().getTime()}.xlsx`)
}

//全量同步司法案例
const handleSync = () => {
  ElMessageBox.confirm('是否确认同步所有数据项？').then(() => {
    loading.value = true
    return syncAllCase()
  }).then(() => {
    ElMessage.success('同步成功')
    getList()
  }).catch(() => {
  }).finally(() => {
    loading.value = false
  })
}

const handleUploadPdf = () => {
  uploadPdf.value.open = true
}
const pdfContent = ref('')

// function handlePdfFile(uploadFile) {
//   console.log(uploadFile, pdfContent.value)
//
//   const url = uploadFile.url
//   getDocument(url).promise.then((pdfDoc) => {
//
//     for (let pageNum = 1; pageNum <= pdfDoc.numPages; pageNum++) {
//       pdfDoc.getPage(pageNum).then((page) => {
//         page.getTextContent().then((textContent) => {
//           pdfContent.value += textContent.items.map((item) => item.str).join(' ');
//         });
//       });
//     }
//
//   });
//
//   // fileReader.readAsArrayBuffer(uploadFile);
// }

onMounted(() => {
  getList()
  // proxy.sendWebMessage("欢迎使用来到管理分析页面")
})
</script>


<template>
  <div class="app-container">
    <el-card class="box-card" shadow="hover">
      <!--      条件查询表单-->
      <el-row :gutter="10" align="middle" class="header" justify="space-between">
        <el-form
          v-show="showSearch"
          ref="queryForm"
          :inline="true"
          :model="queryParams"
          label-position="left"
          label-width="80px"
        >
          <el-form-item label="案件名称" label-width="80" prop="name">
            <el-input v-model="queryParams.name"/>
          </el-form-item>
          <el-form-item label="审判法院" label-width="80" prop="court">
            <el-input v-model="queryParams.court"/>
          </el-form-item>
          <el-form-item label="案号" label-width="40" prop="number">
            <el-input v-model="queryParams.number"/>
          </el-form-item>
          <el-form-item label="案由" label-width="80" prop="cause">
            <el-select v-model="queryParams.cause" clearable @change="handleQuery">
              <el-option
                v-for="item in doc_case_cause"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="文书类型" label-width="80" prop="type">
            <el-select v-model="queryParams.type" clearable @change="handleQuery">
              <el-option
                v-for="item in doc_case_type"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <!--        <el-form-item label="审理程序">-->
          <!--          <el-input v-model="queryParams.process"/>-->
          <!--        </el-form-item>-->
          <!--        <el-form-item label="详细案由">-->
          <!--          <el-input v-model="queryParams.label"/>-->
          <!--        </el-form-item>-->
          <el-form-item label="案件来源" label-width="80" prop="sourceId">
            <el-select v-model="queryParams.sourceId" clearable @change="handleQuery">
              <el-option
                v-for="item in crawler_source"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="判决日期" prop="judgeDate">
            <el-date-picker
              v-model="queryParams.judgeDate"
              placeholder="选择日期"
              type="date"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="公开日期" prop="pubDate">
            <el-date-picker
              v-model="queryParams.pubDate"
              placeholder="选择日期"
              type="date"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <!--        <el-form-item label="法律依据">-->
          <!--          <el-input v-model="queryParams.legalBasis"></el-input>-->
          <!--        </el-form-item>-->
          <!--        <el-form-item label="当事人">-->
          <!--          <el-input v-model="queryParams.party"></el-input>-->
          <!--        </el-form-item>-->
          <!--        <el-form-item label="相关案件">-->
          <!--          <el-input v-model="queryParams.relatedCases"></el-input>-->
          <!--        </el-form-item>-->
          <el-form-item label="状态" prop="status">
            <el-select v-model="queryParams.status" clearable @change="handleQuery">
              <el-option
                v-for="item in crawl_common_status"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button icon="Search" size="default" type="primary" @click="handleQuery">搜索</el-button>
            <el-button icon="Refresh" size="default" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-row>
      <el-divider/>
      <!--      操作按钮导航-->
      <el-row slot="header" :gutter="10" class="mb8" clearfix>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:case:add']"
            icon="Plus"
            plain
            size="default"
            type="primary"
            @click="handleAdd"
          >新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:case:edit']"
            :disabled="single"
            icon="Edit"
            plain
            size="default"
            type="success"
            @click="handleUpdate"
          >修改
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:case:remove']"
            :disabled="multiple"
            icon="Delete"
            plain
            size="default"
            type="danger"
            @click="handleDelete"
          >删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:case:import']"
            icon="Upload"
            plain
            type="info"
            @click="handleImport"
          >导入
          </el-button>
        </el-col>
        <!--        <el-col :span="1.5">-->
        <!--          <el-button-->
        <!--            v-hasPermi="['manage:case:import']"-->
        <!--            icon="Upload"-->
        <!--            plain-->
        <!--            type="primary"-->
        <!--            @click="handleUploadPdf"-->
        <!--          >上传-->
        <!--          </el-button>-->
        <!--        </el-col>-->
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:case:export']"
            icon="Download"
            plain
            size="default"
            type="warning"
            @click="handleExport"
          >导出
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['manage:case:sync']"
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
            :data="caseListOrigin"
            :default-sort="{ prop: 'judgeDate', order: 'descending' }"
            border
            height="500"
            stripe
            style="width: 100%;text-align: center"
            table-layout="auto"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column align="center" label="序号" prop="id" type="index" width="150"/>
            <el-table-column fixed label="案件名称" prop="name" width="200">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.name }}</template>
                  <span class="hidden">{{ row.name }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="审判法院" prop="court" width="180">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.court }}</template>
                  <span class="hidden">{{ row.court }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="案号" prop="number" width="210"></el-table-column>
            <el-table-column label="案由" prop="cause" width="150"></el-table-column>
            <el-table-column label="原始链接" prop="url" width="150">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">
                  <span class="hidden"> {{ scope.row.url }}</span>
                </el-link>
              </template>
            </el-table-column>
            <el-table-column label="文书类型" prop="type" width="150"></el-table-column>
            <el-table-column label="审理程序" prop="process" width="150"></el-table-column>
            <el-table-column label="详细案由" prop="label" width="180"></el-table-column>
            <el-table-column label="案件来源" prop="sourceId" width="150">
              <template #default="scope">
                <dict-tag :options="crawler_source" :value="scope.row.sourceId"/>
              </template>
            </el-table-column>
            <el-table-column label="判决日期" prop="judgeDate" sortable width="150">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.judgeDate }}</span>
                </div>
              </template>
            </el-table-column>
            <!--        <el-table-column label="公开日期" prop="pubDate" width="150"></el-table-column>-->
            <el-table-column label="法律依据" prop="legalBasis" style="overflow: hidden" width="150">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.legalBasis }}</template>
                  <span class="hidden">{{ row.legalBasis }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <!--        <el-table-column label="当事人" prop="party" width="150"></el-table-column>-->
            <!--        <el-table-column label="相关案件" prop="relatedCases" width="180"></el-table-column>-->
            <el-table-column label="状态" prop="status" width="150">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="创建时间" prop="createTime" sortable width="200">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.createTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="更新时间" prop="updateTime" sortable width="200">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.updateTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              fixed="right"
              label="操作"
              width="180"
            >
              <template #default="scope">
                <el-button
                  v-hasPermi="['manage:case:edit']"
                  icon="Edit"
                  size="small"
                  type="primary"
                  @click="handleUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-hasPermi="['manage:case:remove']"
                  icon="Delete"
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.row)"
                >
                  删除
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
            :data="caseListStriped"
            :default-sort="{ prop: 'judgeDate', order: 'descending' }"
            border
            height="500"
            stripe
            style="width: 100%;text-align: center"
            table-layout="auto"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column align="center" label="序号" prop="id" type="index" width="150"/>
            <el-table-column fixed label="案件名称" prop="name" width="200">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.name }}</template>
                  <span class="hidden">{{ row.name }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="审判法院" prop="court" width="180">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.court }}</template>
                  <span class="hidden">{{ row.court }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="案号" prop="number" width="210"></el-table-column>
            <el-table-column label="案由" prop="cause" width="150"></el-table-column>
            <el-table-column label="原始链接" prop="url" width="150">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">
                  <span class="hidden"> {{ scope.row.url }}</span>
                </el-link>
              </template>
            </el-table-column>
            <el-table-column label="文书类型" prop="type" width="150"></el-table-column>
            <el-table-column label="审理程序" prop="process" width="150"></el-table-column>
            <el-table-column label="详细案由" prop="label" width="180"></el-table-column>
            <el-table-column label="案件来源" prop="sourceId" width="150">
              <template #default="scope">
                <dict-tag :options="crawler_source" :value="scope.row.sourceId"/>
              </template>
            </el-table-column>
            <el-table-column label="判决日期" prop="judgeDate" sortable width="150">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.judgeDate }}</span>
                </div>
              </template>
            </el-table-column>
            <!--        <el-table-column label="公开日期" prop="pubDate" width="150"></el-table-column>-->
            <el-table-column label="法律依据" prop="legalBasis" style="overflow: hidden" width="150">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.legalBasis }}</template>
                  <span class="hidden">{{ row.legalBasis }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <!--        <el-table-column label="当事人" prop="party" width="150"></el-table-column>-->
            <!--        <el-table-column label="相关案件" prop="relatedCases" width="180"></el-table-column>-->
            <el-table-column label="状态" prop="status" width="150">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="新建日期" prop="createTime" sortable width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.createTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="更新日期" prop="updateTime" sortable width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.updateTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              fixed="right"
              label="操作"
              width="180"
            >
              <template #default="scope">
                <el-button
                  v-hasPermi="['manage:case:edit']"
                  icon="Edit"
                  size="small"
                  type="primary"
                  @click="handleUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-hasPermi="['manage:case:remove']"
                  icon="Delete"
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.row)"
                >
                  删除
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
            :data="caseList"
            :default-sort="{ prop: 'judgeDate', order: 'descending' }"
            border
            height="500"
            stripe
            style="width: 100%;text-align: center"
            table-layout="auto"
            @selection-change="handleSelectionChange"
          >
            <el-table-column type="selection" width="55"/>
            <el-table-column align="center" label="序号" prop="id" type="index" width="150"/>
            <el-table-column fixed label="案件名称" prop="name" width="200">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.name }}</template>
                  <span class="hidden">{{ row.name }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="审判法院" prop="court" width="180">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.court }}</template>
                  <span class="hidden">{{ row.court }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <el-table-column label="案号" prop="number" width="210"></el-table-column>
            <el-table-column label="案由" prop="cause" width="150"></el-table-column>
            <el-table-column label="原始链接" prop="url" width="150">
              <template #default="scope">
                <el-link :href="scope.row.url" target="_blank" type="primary">
                  <span class="hidden"> {{ scope.row.url }}</span>
                </el-link>
              </template>
            </el-table-column>
            <el-table-column label="文书类型" prop="type" width="150"></el-table-column>
            <el-table-column label="审理程序" prop="process" width="150"></el-table-column>
            <el-table-column label="详细案由" prop="label" width="180"></el-table-column>
            <el-table-column label="案件来源" prop="sourceId" width="150">
              <template #default="scope">
                <dict-tag :options="crawler_source" :value="scope.row.sourceId"/>
              </template>
            </el-table-column>
            <el-table-column label="判决日期" prop="judgeDate" sortable width="150">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.judgeDate }}</span>
                </div>
              </template>
            </el-table-column>
            <!--        <el-table-column label="公开日期" prop="pubDate" width="150"></el-table-column>-->
            <el-table-column label="法律依据" prop="legalBasis" style="overflow: hidden" width="150">
              <template #default="{ row }">
                <el-tooltip effect="dark" placement="top">
                  <template #content class="newLine">{{ row.legalBasis }}</template>
                  <span class="hidden">{{ row.legalBasis }}</span>
                </el-tooltip>
              </template>
            </el-table-column>
            <!--        <el-table-column label="当事人" prop="party" width="150"></el-table-column>-->
            <!--        <el-table-column label="相关案件" prop="relatedCases" width="180"></el-table-column>-->
            <el-table-column label="状态" prop="status" width="150">
              <template #default="scope">
                <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
              </template>
            </el-table-column>
            <el-table-column label="新建日期" prop="createTime" sortable width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.createTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column label="更新日期" prop="updateTime" sortable width="180">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-icon>
                    <timer/>
                  </el-icon>
                  <span style="margin-left: 10px">{{ scope.row.updateTime }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column
              align="center"
              fixed="right"
              label="操作"
              width="180"
            >
              <template #default="scope">
                <el-button
                  v-hasPermi="['manage:case:edit']"
                  icon="Edit"
                  size="small"
                  type="primary"
                  @click="handleUpdate(scope.row)"
                >
                  修改
                </el-button>
                <el-button
                  v-hasPermi="['manage:case:remove']"
                  icon="Delete"
                  size="small"
                  type="danger"
                  @click="handleDelete(scope.row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
            <template #empty>
              <el-empty></el-empty>
            </template>
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <!-- 添加或修改司法案例对话框 -->
      <el-dialog v-model="open" :title="title" align-center draggable>
        <el-form ref="dialogForm" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="案件名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入案件名称"/>
          </el-form-item>
          <el-form-item label="审判法院" prop="court">
            <el-input v-model="form.court" placeholder="请输入审判法院"/>
          </el-form-item>
          <el-form-item label="案号" prop="number">
            <el-input v-model="form.number" placeholder="请输入案号"/>
          </el-form-item>
          <el-form-item label="原始链接" prop="url">
            <el-input v-model="form.url" placeholder="请输入原始链接"/>
          </el-form-item>
          <el-form-item label="案由" prop="cause">
            <el-select v-model="form.cause" placeholder="请选择案由">
              <el-option
                v-for="dict in doc_case_cause"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="文书类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择文书类型">
              <el-option
                v-for="dict in doc_case_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="审理程序" prop="process">
            <el-input v-model="form.process" placeholder="请输入审理程序"/>
          </el-form-item>
          <el-form-item label="详细案由" prop="label">
            <el-input v-model="form.label" placeholder="请输入详细案由"/>
          </el-form-item>
          <el-form-item label="案件正文" prop="content">
            <el-link href="javascript:void(0);" type="primary" @click="handleOpenContent">进入案件正文</el-link>
            <el-dialog v-model="openContent" draggable overflow title="输入案件正文">
              <q-editor v-model="form.content" :min-height="400"/>
            </el-dialog>
          </el-form-item>
          <el-form-item label="案件来源" prop="sourceId">
            <el-select v-model="form.sourceId" placeholder="请选择案件来源">
              <el-option
                v-for="dict in crawler_source"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="判决日期" prop="judgeDate">
            <el-date-picker v-model="form.judgeDate"
                            clearable
                            placeholder="请选择判决日期"
                            type="date"
                            value-format="YYYY-MM-DD"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="公开日期" prop="pubDate">
            <el-date-picker v-model="form.pubDate"
                            clearable
                            placeholder="请选择公开日期"
                            type="date"
                            value-format="YYYY-MM-DD"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="法律依据" prop="legalBasis">
            <el-input v-model="form.legalBasis" placeholder="请输入法律依据"/>
          </el-form-item>
          <!--          <el-form-item label="当事人" prop="party">-->
          <!--            <el-input v-model="form.party" placeholder="请输入当事人"/>-->
          <!--          </el-form-item>-->
          <!--          <el-form-item label="相关案件" prop="relatedCases">-->
          <!--            <el-input v-model="form.relatedCases" placeholder="请输入内容" type="textarea"/>-->
          <!--          </el-form-item>-->
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option
                v-for="dict in crawl_common_status"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancelDialog">取 消</el-button>
          </span>
        </template>
      </el-dialog>

      <!--      上传pdf对话框-->
      <el-dialog v-model="uploadPdf.open" :title="upload.title" append-to-body draggable width="40%">
        <file-upload
          ref="uploadPdfRef"
          :limit="1"
          :modelValue="pdfContent"
          accept=".pdf"
          drag
          @uploaded="handlePdfFile"
        >
          <el-icon class="el-icon--upload">
            <upload-filled/>
          </el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或
            <em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              只能上传pdf文件，且不超过10M
              {{ pdfContent }}
            </div>
          </template>
        </file-upload>
        <el-form ref="dialogForm" :model="form" :rules="rules" label-width="100px">
          <el-form-item label="案件名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入案件名称"/>
          </el-form-item>
          <el-form-item label="审判法院" prop="court">
            <el-input v-model="form.court" placeholder="请输入审判法院"/>
          </el-form-item>
          <el-form-item label="案号" prop="number">
            <el-input v-model="form.number" placeholder="请输入案号"/>
          </el-form-item>
          <el-form-item label="原始链接" prop="url">
            <el-input v-model="form.url" placeholder="请输入原始链接"/>
          </el-form-item>
          <el-form-item label="案由" prop="cause">
            <el-select v-model="form.cause" placeholder="请选择案由">
              <el-option
                v-for="dict in doc_case_cause"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="文书类型" prop="type">
            <el-select v-model="form.type" placeholder="请选择文书类型">
              <el-option
                v-for="dict in doc_case_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="审理程序" prop="process">
            <el-input v-model="form.process" placeholder="请输入审理程序"/>
          </el-form-item>
          <el-form-item label="详细案由" prop="label">
            <el-input v-model="form.label" placeholder="请输入详细案由"/>
          </el-form-item>
          <el-form-item label="案件正文" prop="content">
            <el-link href="javascript:void(0);" type="primary" @click="handleOpenContent">进入案件正文</el-link>
            <el-dialog v-model="openContent" draggable overflow title="输入案件正文">
              <q-editor v-model="form.content" :min-height="400"/>
            </el-dialog>
          </el-form-item>
          <el-form-item label="案件来源" prop="sourceId">
            <el-select v-model="form.sourceId" placeholder="请选择案件来源">
              <el-option
                v-for="dict in crawler_source"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="判决日期" prop="judgeDate">
            <el-date-picker v-model="form.judgeDate"
                            clearable
                            placeholder="请选择判决日期"
                            type="date"
                            value-format="YYYY-MM-DD"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="公开日期" prop="pubDate">
            <el-date-picker v-model="form.pubDate"
                            clearable
                            placeholder="请选择公开日期"
                            type="date"
                            value-format="YYYY-MM-DD"
            >
            </el-date-picker>
          </el-form-item>
          <el-form-item label="法律依据" prop="legalBasis">
            <el-input v-model="form.legalBasis" placeholder="请输入法律依据"/>
          </el-form-item>
          <!--          <el-form-item label="当事人" prop="party">-->
          <!--            <el-input v-model="form.party" placeholder="请输入当事人"/>-->
          <!--          </el-form-item>-->
          <!--          <el-form-item label="相关案件" prop="relatedCases">-->
          <!--            <el-input v-model="form.relatedCases" placeholder="请输入内容" type="textarea"/>-->
          <!--          </el-form-item>-->
          <el-form-item label="状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择状态">
              <el-option
                v-for="dict in crawl_common_status"
                :key="dict.value"
                :label="dict.label"
                :value="parseInt(dict.value)"
              ></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
            <el-button @click="cancelDialog">取 消</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 案例导入对话框 -->
      <el-dialog v-model="upload.open" :title="upload.title" append-to-body draggable width="400px">
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
                是否更新已经存在的司法案例数据
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
      <el-dialog v-model="processDialog" :fullscreen="true" draggable title="司法案例数据清洗挖掘" width="100%">
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
                      <div v-if="data.extra.party"
                           class="text-center whitespace-pre-wrap flex justify-around">
                        <el-form-item label="">
                          <el-input v-model="data.extra.party.plaintiff" :prefix-icon="UserFilled" clearable/>
                        </el-form-item>
                        <el-form-item class="text-red-500 font-bold">
                          原告⚖️被告
                        </el-form-item>
                        <el-form-item label="">
                          <el-input v-model="data.extra.party.defendant" :prefix-icon="UserFilled" clearable/>
                        </el-form-item>
                      </div>
                      <el-form-item label="关键词语">
                        <el-input v-model="data.extra.keyword" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="诉讼要求">
                        <el-input v-model="data.extra.plea" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="案件类型">
                        <el-input v-model="data.extra.label" :prefix-icon="Switch"/>
                      </el-form-item>
                      <el-form-item label="原告诉述">
                        <el-input v-model="data.extra.plai" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="被告辩称">
                        <el-input v-model="data.extra.defe" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="案件事实">
                        <el-input v-model="data.extra.fact" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="判决记录">
                        <el-input v-model="data.extra.note" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="案件摘要">
                        <el-input v-model="data.extra.summary" type="textarea"/>
                      </el-form-item>
                      <el-form-item label="法律依据">
                        <el-input v-model="data.extra.legalBasis" type="textarea"/>
                      </el-form-item>
                    </el-card>
                  </div>
                </el-col>
              </el-form>
              <div class="flex justify-end items-end mt-3"> <!-- 添加 justify-end 和 items-end 类 -->
                <el-button :icon="Close" @click="cancelDialog">取 消</el-button>
                <el-button :icon="Refresh" :plain="true" type="primary" @click="resetProcess(data)">还原</el-button>
                <el-button :disabled="processStep <= 0" :icon="Back" type="primary"
                           @click="handleProcessPrev">
                  上一步
                </el-button>
                <el-button :disabled="processStep >= 1" :icon="Right" type="primary"
                           @click="handleProcessNext">
                  下一步
                </el-button>
                <el-button :disabled="processStep !== 0" :icon="Brush" :loading="buttonLoading" type="warning"
                           @click="handleProcessStrip(data)">
                  开始清洗
                </el-button>
                <el-button :disabled="processStep !== 1" :icon="HelpFilled" :loading="buttonLoading" type="warning"
                           @click="handleProcessMining(data)">
                  开始挖掘
                </el-button>
                <!--                <el-button :disabled="processStep !== 1" type="success"-->
                <!--                           @click="handleProcessStage(data)">               -->
                <el-button :icon="CirclePlus" type="success"
                           @click="handleProcessStage(data)">
                  暂 存
                </el-button>
                <el-button :disabled="processDataStage.length===0" :icon="Check" type="warning"
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

<style scoped>
.cell {
  color: black;
  text-align: center;
  justify-content: center;
  align-content: center;
}

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
