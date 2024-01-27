<script setup>
import {
  listRegulation,
  getRegulation,
  delRegulation,
  addRegulation,
  updateRegulation,
} from '@/api/retrieve/regulation'
import {download} from '@/utils/request'
import {getCurrentInstance, onMounted, reactive, ref, toRefs} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";
import {parseTime} from "@/utils/ruoyi";

const {proxy} = getCurrentInstance();

const {
  crawler_source,
  crawl_common_status,
  law_type
} = proxy.useDict('crawler_source', 'crawl_common_status', 'law_type')

const regulationList = ref([])
const total = ref(0)
const loading = ref(true)
const buttonLoading = ref(false)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const showSearch = ref(true)
const openContent = ref(false)

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
  }
})

const {queryParams, form, rules} = toRefs(data);

const title = ref('')
const open = ref(false)

const queryForm = ref(null)
const dialogForm = ref(null)

const getList = () => {
  loading.value = true
  listRegulation(queryParams.value).then((res) => {
    regulationList.value = res.rows
    total.value = res.total
    loading.value = false
  })

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
  proxy.$modal.msgSuccess("已选中" + selection.length + "条数据")
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
    createBy: undefined,
    createTime: undefined,
    updateBy: undefined,
    updateTime: undefined
  }
}

const cancelDialog = () => {
  open.value = false
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

const submitForm = () => {
  dialogForm.value.validate(valid => {
    if (valid) {
      buttonLoading.value = true
      if (form.value.id != null) {
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

const handleExport = () => {
  console.log({...queryParams.value})
  proxy.download('retrieve/regulation/export',
    {...queryParams.value}
    , `regulation_${new Date().getTime()}.xlsx`)
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
          <el-button v-hasPermi="['retrieve:regulation:add']" icon="Plus" plain size="default" type="primary"
                     @click="handleAdd">新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['retrieve:regulation:edit']" :disabled="single" icon="Edit" plain size="default"
                     type="success"
                     @click="handleUpdate">修改
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['retrieve:regulation:remove']" :disabled="multiple" icon="Delete" plain
                     size="default" type="danger"
                     @click="handleDelete">删除
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button v-hasPermi="['retrieve:regulation:export']" icon="Download" plain size="default" type="warning"
                     @click="handleExport">导出
          </el-button>
        </el-col>
        <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>

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
        <el-table-column align="center" type="selection" width="55"/>
        <!--      <el-table-column v-if="true" align="center" label="法律法规主键id" prop="id"/>-->
        <el-table-column align="center" fixed label="法规名称" prop="name" width="230"/>
        <!--      <el-table-column align="center" label="领域类别" prop="field"/>-->
        <el-table-column align="center" label="法规类型" prop="type"
                         width="180">
          <template #default="scope">
            <dict-tag :options="law_type" :value="scope.row.type" width="180"/>
          </template>
        </el-table-column>
        <el-table-column align="center" label="原始链接" prop="url" width="190">
          <template #default="scope">
            <el-link :href="scope.row.url" target="_blank" type="primary">{{ scope.row.url }}</el-link>
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
            <el-button v-hasPermi="['retrieve:regulation:edit']" icon="Edit" size="default" type="text"
                       @click="handleUpdate(scope.row)">修改
            </el-button>
            <el-button v-hasPermi="['retrieve:regulation:remove']" icon="Delete" size="default" type="text" width="180"
                       @click="handleDelete(scope.row)">删除
            </el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty></el-empty>
        </template>
      </el-table>

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
              <editor v-model="form.content" :min-height="300"/>
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

</style>
