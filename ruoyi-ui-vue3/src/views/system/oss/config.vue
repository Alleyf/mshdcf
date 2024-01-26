<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :inline="true" :model="queryParams">
      <el-form-item label="配置key" prop="configKey">
        <el-input
          v-model="queryParams.configKey"
          clearable
          placeholder="配置key"
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="桶名称" prop="bucketName">
        <el-input
          v-model="queryParams.bucketName"
          clearable
          placeholder="请输入桶名称"
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否默认" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态" style="width: 200px">
          <el-option key="0" label="是" value="0"/>
          <el-option key="1" label="否" value="1"/>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="search" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:oss:add']"
          icon="Plus"
          plain
          type="primary"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:oss:edit']"
          :disabled="single"
          icon="Edit"
          plain
          type="success"
          @click="handleUpdate"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:oss:remove']"
          :disabled="multiple"
          icon="Delete"
          plain
          type="danger"
          @click="handleDelete"
        >删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ossConfigList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55" />
      <el-table-column v-if="columns[0].visible" align="center" label="主建" prop="ossConfigId"/>
      <el-table-column v-if="columns[1].visible" align="center" label="配置key" prop="configKey" />
      <el-table-column v-if="columns[2].visible" align="center" label="访问站点" prop="endpoint" width="200" />
      <el-table-column v-if="columns[3].visible" align="center" label="自定义域名" prop="domain" width="200" />
      <el-table-column v-if="columns[4].visible" align="center" label="桶名称" prop="bucketName" />
      <el-table-column v-if="columns[5].visible" align="center" label="前缀" prop="prefix" />
      <el-table-column v-if="columns[6].visible" align="center" label="域" prop="region" />
      <el-table-column v-if="columns[7].visible" align="center" label="桶权限类型" prop="accessPolicy" >
        <template #default="scope">
          <el-tag v-if="scope.row.accessPolicy === '0'" type="warning">private</el-tag>
          <el-tag v-if="scope.row.accessPolicy === '1'" type="success">public</el-tag>
          <el-tag v-if="scope.row.accessPolicy === '2'" type="info">custom</el-tag>
        </template>
      </el-table-column>
      <el-table-column v-if="columns[8].visible" align="center" label="是否默认" prop="status">
        <template #default="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="150">
        <template #default="scope">
          <el-button v-hasPermi="['system:oss:edit']" icon="Edit" link type="primary" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['system:oss:remove']" icon="Delete" link type="primary" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 添加或修改对象存储配置对话框 -->
    <el-dialog v-model="open" :title="title" append-to-body width="800px">
      <el-form ref="ossConfigRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="配置key" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入配置key" />
        </el-form-item>
        <el-form-item label="访问站点" prop="endpoint">
          <el-input v-model="form.endpoint" placeholder="请输入访问站点" />
        </el-form-item>
        <el-form-item label="自定义域名" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入自定义域名" />
        </el-form-item>
        <el-form-item label="accessKey" prop="accessKey">
          <el-input v-model="form.accessKey" placeholder="请输入accessKey" />
        </el-form-item>
        <el-form-item label="secretKey" prop="secretKey">
          <el-input v-model="form.secretKey" placeholder="请输入秘钥" show-password />
        </el-form-item>
        <el-form-item label="桶名称" prop="bucketName">
          <el-input v-model="form.bucketName" placeholder="请输入桶名称" />
        </el-form-item>
        <el-form-item label="前缀" prop="prefix">
          <el-input v-model="form.prefix" placeholder="请输入前缀" />
        </el-form-item>
        <el-form-item label="是否HTTPS">
          <el-radio-group v-model="form.isHttps">
            <el-radio
              v-for="dict in sys_yes_no"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="桶权限类型">
          <el-radio-group v-model="form.accessPolicy">
            <el-radio label="0">private</el-radio>
            <el-radio label="1">public</el-radio>
            <el-radio label="2">custom</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="域" prop="region">
          <el-input v-model="form.region" placeholder="请输入域" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入内容" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script name="OssConfig" setup>

import {
  listOssConfig,
  getOssConfig,
  delOssConfig,
  addOssConfig,
  updateOssConfig,
  changeOssConfigStatus
} from "@/api/system/ossConfig";

const { proxy } = getCurrentInstance();
const { sys_yes_no } = proxy.useDict("sys_yes_no");

const ossConfigList = ref([]);
const open = ref(false);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

// 列显隐信息
const columns = ref([
  { key: 0, label: `主建`, visible: true },
  { key: 1, label: `配置key`, visible: false },
  { key: 2, label: `访问站点`, visible: true },
  { key: 3, label: `自定义域名`, visible: true },
  { key: 4, label: `桶名称`, visible: true },
  { key: 5, label: `前缀`, visible: true },
  { key: 6, label: `域`, visible: true },
  { key: 7, label: `桶权限类型`, visible: true },
  { key: 8, label: `状态`, visible: true }
]);

const data = reactive({
  form: {},
  // 查询参数
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    configKey: undefined,
    bucketName: undefined,
    status: undefined,
  },
  rules: {
    configKey: [
      { required: true, message: "configKey不能为空", trigger: "blur" },
    ],
    accessKey: [
      { required: true, message: "accessKey不能为空", trigger: "blur" },
      {
        min: 2,
        max: 200,
        message: "accessKey长度必须介于 2 和 100 之间",
        trigger: "blur",
      },
    ],
    secretKey: [
      { required: true, message: "secretKey不能为空", trigger: "blur" },
      {
        min: 2,
        max: 100,
        message: "secretKey长度必须介于 2 和 100 之间",
        trigger: "blur",
      },
    ],
    bucketName: [
      { required: true, message: "bucketName不能为空", trigger: "blur" },
      {
        min: 2,
        max: 100,
        message: "bucketName长度必须介于 2 和 100 之间",
        trigger: "blur",
      },
    ],
    endpoint: [
      { required: true, message: "endpoint不能为空", trigger: "blur" },
      {
        min: 2,
        max: 100,
        message: "endpoint名称长度必须介于 2 和 100 之间",
        trigger: "blur",
      },
    ],
    accessPolicy:[
      { required: true, message: "accessPolicy不能为空", trigger: "blur" }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询对象存储配置列表 */
function getList() {
  loading.value = true;
  listOssConfig(queryParams.value).then((response) => {
    ossConfigList.value = response.rows;
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
    ossConfigId: undefined,
    configKey: undefined,
    accessKey: undefined,
    secretKey: undefined,
    bucketName: undefined,
    prefix: undefined,
    endpoint: undefined,
    domain: undefined,
    isHttps: "N",
    accessPolicy: "1",
    region: undefined,
    status: "1",
    remark: undefined,
  };
  proxy.resetForm("ossConfigRef");
}
/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 选择条数  */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.ossConfigId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}
/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加对象存储配置";
}
/** 修改按钮操作 */
function handleUpdate(row) {
  loading.value = true;
  reset();
  const ossConfigId = row.ossConfigId || ids.value;
  getOssConfig(ossConfigId).then((response) => {
    loading.value = false;
    form.value = response.data;
    open.value = true;
    title.value = "修改对象存储配置";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["ossConfigRef"].validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.ossConfigId != null) {
        updateOssConfig(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addOssConfig(form.value).then(response => {
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
/** 用户状态修改  */
function handleStatusChange(row) {
  let text = row.status === "0" ? "启用" : "停用";
  proxy.$modal.confirm('确认要"' + text + '""' + row.configKey + '"配置吗?').then(() => {
    return changeOssConfigStatus(row.ossConfigId, row.status, row.configKey);
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess(text + "成功");
  }).catch(function () {
    row.status = row.status === "0" ? "1" : "0";
  });
}
/** 删除按钮操作 */
function handleDelete(row) {
  const ossConfigIds = row.ossConfigId || ids.value;
  proxy.$modal.confirm('是否确认删除OSS配置编号为"' + ossConfigIds + '"的数据项?').then(() => {
    loading.value = true;
    return delOssConfig(ossConfigIds);
  }).then(() => {
    loading.value = false;
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).finally(() => {
    loading.value = false;
  });
}

getList();
</script>
