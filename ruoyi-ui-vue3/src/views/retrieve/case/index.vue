<script setup>
import {
  listCase,
  getCase,
  delCase,
  addCase,
  updateCase,
} from '@/api/retrieve/case'
import {download} from '@/utils/request'
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {getToken} from "@/utils/auth";

const {proxy} = getCurrentInstance();
const {
  doc_case_cause,
  doc_case_type,
  crawler_source,
  crawl_common_status
} = proxy.useDict("doc_case_cause", "doc_case_type", "crawler_source", "crawl_common_status");


const caseList = ref([]);
const total = ref(0);
const loading = ref(true);
const buttonLoading = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);

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
  url: import.meta.env.VITE_APP_BASE_API + "/retrieve/case/importData"
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
  }
})

const {queryParams, form, rules} = toRefs(data);


const title = ref('')
const open = ref(false)
const openContent = ref(false)


const queryForm = ref(null)
const dialogForm = ref(null)

const getList = () => {
  loading.value = true
  console.log(queryParams.value)
  listCase(queryParams.value).then(res => {
    caseList.value = res.rows
    total.value = res.total
    loading.value = false
  })

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
  proxy.$modal.msgSuccess("已选中" + selection.length + "条数据");
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

const submitForm = () => {
  dialogForm.value.validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
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
    return delCase(ids)
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
  proxy.download("retrieve/case/importTemplate", {}, `case_template_${new Date().getTime()}.xlsx`);
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
};

const handleExport = () => {
  console.log({
    ...queryParams.value
  }, typeof {
    ...queryParams.value
  })
  proxy.download('retrieve/case/export',
    {...queryParams.value}
    , `case_${new Date().getTime()}.xlsx`)
}

const handleExportSelected = () => {
  const idLs = []
  if (!ids.value.length) {
    ElMessage.warning('请选择要导出的项目')
    return
  }
  for (let id in ids.value) {
    idLs.push(parseInt(ids.value[id]))
  }
  console.log(idLs, typeof idLs)
  download('retrieve/case/exportSelected',
    idLs
    , `case_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList()

})
</script>


<template>
  <div class="app-container">
    <el-card class="box-card" shadow="hover">
      <el-row :gutter="10" align="middle" class="header" justify="space-between">
        <el-form
          v-show="showSearch"
          ref="queryForm"
          :inline="true"
          :model="queryParams"
          label-position="left"
          label-width="80px"
        >
          <el-form-item label="案件名称" label-width="80">
            <el-input v-model="queryParams.name"></el-input>
          </el-form-item>
          <el-form-item label="审判法院" label-width="80">
            <el-input v-model="queryParams.court"></el-input>
          </el-form-item>
          <el-form-item label="案号" label-width="40">
            <el-input v-model="queryParams.number"></el-input>
          </el-form-item>
          <el-form-item label="案由" label-width="80">
            <el-select v-model="queryParams.cause">
              <el-option
                v-for="item in doc_case_cause"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="文书类型" label-width="80">
            <el-select v-model="queryParams.type">
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
          <el-form-item label="案件来源" label-width="80">
            <el-select v-model="queryParams.sourceId">
              <el-option
                v-for="item in crawler_source"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="判决日期">
            <el-date-picker
              v-model="queryParams.judgeDate"
              placeholder="选择日期"
              type="date"
              value-format="YYYY-MM-DD"
            />
          </el-form-item>
          <el-form-item label="公开日期">
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
          <el-form-item label="状态">
            <el-select v-model="queryParams.status">
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
      <el-row slot="header" :gutter="10" class="mb8" clearfix>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['retrieve:case:add']"
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
            v-hasPermi="['retrieve:case:edit']"
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
            v-hasPermi="['retrieve:case:remove']"
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
            v-hasPermi="['retrieve:case:import']"
            icon="Upload"
            plain
            type="info"
            @click="handleImport"
          >导入
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            v-hasPermi="['retrieve:case:export']"
            icon="Download"
            plain
            size="default"
            type="warning"
            @click="handleExport"
          >导出
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
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
        <el-table-column type="selection" width="55"></el-table-column>
        <!--        <el-table-column label="案件主键id" prop="id" width="150"></el-table-column>-->
        <el-table-column fixed label="案件名称" prop="name" width="200"></el-table-column>
        <el-table-column
          label="审判法院"
          prop="court"
          width="180"></el-table-column>
        <el-table-column label="案号" prop="number" width="210"></el-table-column>
        <el-table-column label="案由" prop="cause" width="150"></el-table-column>
        <el-table-column label="原始链接" prop="url" width="150">
          <template #default="scope">
            <el-link :href="scope.row.url" :title="scope.row.url" target="_blank" type="primary">
              {{ scope.row.url }}
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
        <el-table-column label="法律依据" prop="legalBasis" style="overflow: hidden" width="150"></el-table-column>
        <!--        <el-table-column label="当事人" prop="party" width="150"></el-table-column>-->
        <!--        <el-table-column label="相关案件" prop="relatedCases" width="180"></el-table-column>-->
        <el-table-column label="状态" prop="status" width="150">
          <template #default="scope">
            <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
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
              v-hasPermi="['dataRetrieve:case:edit']"
              icon="Edit"
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
            >
              修改
            </el-button>
            <el-button
              v-hasPermi="['dataRetrieve:case:remove']"
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

      <!-- 添加或修改司法案例对话框 -->
      <el-dialog v-model="open" :title="title" align-center>
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
            <el-link href="javascript:void(0);" type="primary" @click="openContent=true">进入案件正文</el-link>
            <el-dialog v-model="openContent" title="输入案件正文">
              <editor v-model="form.content" :min-height="300"/>
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
          <el-form-item label="当事人" prop="party">
            <el-input v-model="form.party" placeholder="请输入当事人"/>
          </el-form-item>
          <el-form-item label="相关案件" prop="relatedCases">
            <el-input v-model="form.relatedCases" placeholder="请输入内容" type="textarea"/>
          </el-form-item>
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
</style>
