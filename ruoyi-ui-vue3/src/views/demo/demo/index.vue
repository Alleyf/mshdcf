<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :inline="true" :model="queryParams">
      <el-form-item label="key键" prop="testKey">
        <el-input
          v-model="queryParams.testKey"
          clearable
          placeholder="请输入key键"
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="值" prop="value">
        <el-input
          v-model="queryParams.value"
          clearable
          placeholder="请输入值"
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker
          v-model="daterangeCreateTime"
          :default-time="[new Date(2000, 1, 1, 0, 0, 0), new Date(2000, 1, 1, 23, 59, 59)]"
          end-placeholder="结束日期"
          range-separator="-"
          start-placeholder="开始日期"
          type="daterange"
          value-format="YYYY-MM-DD HH:mm:ss"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button icon="search" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="search" type="primary" @click="handlePage">搜索(自定义分页接口)</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['demo:demo:add']"
          icon="Plus"
          plain
          type="primary"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['demo:demo:edit']"
          :disabled="single"
          icon="Edit"
          plain
          type="success"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['demo:demo:remove']"
          :disabled="multiple"
          icon="Delete"
          plain
          type="danger"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['demo:demo:import']"
          icon="Upload"
          plain
          type="info"
          @click="handleImport"
        >导入(校验)</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['demo:demo:export']"
          icon="Download"
          plain
          type="warning"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="demoList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column v-if="columns[0].visible" align="center" label="主键" prop="id"/>
      <el-table-column v-if="columns[1].visible" align="center" label="部门id" prop="deptId"/>
      <el-table-column v-if="columns[2].visible" align="center" label="用户id" prop="userId"/>
      <el-table-column v-if="columns[3].visible" align="center" label="排序号" prop="orderNum"/>
      <el-table-column v-if="columns[4].visible" align="center" label="key键" prop="testKey"/>
      <el-table-column v-if="columns[5].visible" align="center" label="值" prop="value"/>
      <el-table-column v-if="columns[6].visible" align="center" label="创建时间" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="columns[7].visible" align="center" label="创建人" prop="createBy" />
      <el-table-column v-if="columns[8].visible" align="center" label="更新时间" prop="updateTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.updateTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="columns[9].visible" align="center" label="更新人" prop="updateBy" />
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="150">
        <template #default="scope">
          <el-button v-hasPermi="['demo:demo:edit']" icon="Edit" link type="primary" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['demo:demo:remove']" icon="Delete" link type="primary" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      v-model:limit="queryParams.pageSize"
      v-model:page="queryParams.pageNum"
      :total="total"
      @pagination="getList"
    />

    <!-- 添加或修改测试单表对话框 -->
    <el-dialog v-model="open" :title="title" append-to-body width="500px">
      <el-form ref="demoRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="部门id" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门id" />
        </el-form-item>
        <el-form-item label="用户id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户id" />
        </el-form-item>
        <el-form-item label="排序号" prop="orderNum">
          <el-input v-model="form.orderNum" placeholder="请输入排序号" />
        </el-form-item>
        <el-form-item label="key键" prop="testKey">
          <el-input v-model="form.testKey" placeholder="请输入key键" />
        </el-form-item>
        <el-form-item label="值" prop="value">
          <el-input v-model="form.value" placeholder="请输入值" />
        </el-form-item>
        <el-form-item label="创建时间" prop="createTime">
          <el-date-picker v-model="form.createTime"
                          clearable
                          placeholder="选择创建时间"
                          type="datetime"
                          value-format="YYYY-MM-DD HH:mm:ss">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
    <!-- 用户导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" append-to-body width="400px">
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
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script name="Demo" setup>
import { listDemo, pageDemo, getDemo, delDemo, addDemo, updateDemo } from "@/api/demo/demo";
import {getToken} from "@/utils/auth";

const { proxy } = getCurrentInstance();

const demoList = ref([]);
const open = ref(false);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const daterangeCreateTime = ref([]);

/*** 用户导入参数 */
const upload = reactive({
  // 是否显示弹出层（用户导入）
  open: false,
  // 弹出层标题（用户导入）
  title: "",
  // 是否禁用上传
  isUploading: false,
  // 设置上传的请求头部
  headers: { Authorization: "Bearer " + getToken() },
  // 上传的地址
  url: import.meta.env.VITE_APP_BASE_API + "demo/demo/importData"
});

// 列显隐信息
const columns = ref([
  { key: 0, label: `主键`, visible: false },
  { key: 1, label: `部门id`, visible: true },
  { key: 2, label: `用户id`, visible: true },
  { key: 3, label: `排序号`, visible: true },
  { key: 4, label: `key键`, visible: true },
  { key: 5, label: `值`, visible: true },
  { key: 6, label: `创建时间`, visible: true },
  { key: 7, label: `创建人`, visible: true },
  { key: 8, label: `更新时间`, visible: true },
  { key: 9, label: `更新人`, visible: true }
]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    testKey: undefined,
    value: undefined,
    createTime: undefined,
  },
  rules: {
    testKey: [
      { required: true, message: "key键不能为空", trigger: "blur" }
    ],
    value: [
      { required: true, message: "值不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询OSS对象存储列表 */
function getList() {
  loading.value = true;
  listDemo(proxy.addDateRange(queryParams.value, daterangeCreateTime.value, "CreateTime")).then(response => {
    demoList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
/** 自定义分页查询 */
function getPage() {
  loading.value = true;
  pageDemo(proxy.addDateRange(queryParams.value, daterangeCreateTime.value, "CreateTime")).then(response => {
    demoList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}
/** 取消按钮 */
function cancel() {
  open.value = false;
  reset();
}
/** 表单重置 */
function reset() {
  form.value = {
    id: undefined,
    deptId: undefined,
    userId: undefined,
    orderNum: undefined,
    testKey: undefined,
    value: undefined,
    version: undefined,
    createTime: undefined,
    createBy: undefined,
    updateTime: undefined,
    updateBy: undefined,
    delFlag: undefined
  };
  proxy.resetForm("demoRef");
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 搜索按钮操作 */
function handlePage() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  daterangeCreateTime.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加测试单表";
}
/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true;
  reset();
  const ids = row.id || ids.value;
  getDemo(ids).then((response) => {
    loading.value = false;
    form.value = response.data;
    open.value = true;
    title.value = "修改测试单表";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["demoRef"].validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id != null) {
        updateDemo(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addDemo(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      }
    }
  });
}
/** 删除按钮操作 */
function handleDelete(row) {
  const ids = row.id || ids.value;
  proxy.$modal.confirm('是否确认删除测试单表编号为"' + ids + '"的数据项?').then(() => {
    loading.value = true;
    return delDemo(ids);
  }).then(() => {
    loading.value = false;
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).finally(() => {
    loading.value = false;
  });
}
/** 导入按钮操作 */
function handleImport() {
  upload.title = "测试导入";
  upload.open = true;
}
/** 导出按钮操作 */
function handleExport() {
  proxy.download("demo/demo/export", {
    ...queryParams.value,
  },`demo_${new Date().getTime()}.xlsx`);
}
/**文件上传中处理 */
const handleFileUploadProgress = (event, file, fileList) => {
  upload.isUploading = true;
}
/** 文件上传成功处理 */
const handleFileSuccess = (response, file, fileList) => {
  upload.open = false;
  upload.isUploading = false;
  proxy.$refs["uploadRef"].clearFiles();
  proxy.$alert(response.msg, "导入结果", { dangerouslyUseHTMLString: true });
  getList();
}
/** 提交上传文件 */
function submitFileForm() {
  proxy.$refs["uploadRef"].submit();
}

getList()
getPage()
</script>
