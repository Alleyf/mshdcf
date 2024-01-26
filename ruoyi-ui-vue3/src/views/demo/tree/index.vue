<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :inline="true" :model="queryParams">
      <el-form-item label="树节点名" prop="treeName">
        <el-input
          v-model="queryParams.treeName"
          clearable
          placeholder="请输入树节点名"
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
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['demo:tree:add']"
          icon="Plus"
          plain
          type="primary"
          @click="handleAdd"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            icon="Sort"
            plain
            type="info"
            @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-if="refreshTable"
      v-loading="loading"
      :data="treeList"
      :default-expand-all="isExpandAll"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      row-key="id"
    >
      <el-table-column v-if="columns[0].visible" label="父id" prop="parentId" />
      <el-table-column v-if="columns[1].visible" align="center" label="部门id" prop="deptId" />
      <el-table-column v-if="columns[2].visible" align="center" label="用户id" prop="userId" />
      <el-table-column v-if="columns[3].visible" align="center" label="树节点名" prop="treeName" />
      <el-table-column v-if="columns[4].visible" align="center" label="创建时间" prop="createTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作" width="150">
        <template #default="scope">
          <el-button v-hasPermi="['demo:tree:edit']" icon="Edit" link type="primary" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-hasPermi="['demo:tree:add']" icon="Plus" link type="primary" @click="handleAdd(scope.row)">新增</el-button>
          <el-button v-hasPermi="['demo:tree:remove']" icon="Delete" link type="primary" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改测试树表对话框 -->
    <el-dialog v-model="open" :title="title" append-to-body width="500px">
      <el-form ref="treeRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="父id" prop="parentId">
          <treeselect
              v-model:value="form.parentId"
              :objMap="{ value: 'id', label: 'true_name', children: 'children' }"
              :options="treeOptions"
              placeholder="请选择父id"
          />
        </el-form-item>
        <el-form-item label="部门id" prop="deptId">
          <el-input v-model="form.deptId" placeholder="请输入部门id" />
        </el-form-item>
        <el-form-item label="用户id" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户id" />
        </el-form-item>
        <el-form-item label="树节点名" prop="treeName">
          <el-input v-model="form.treeName" placeholder="请输入树节点名" />
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

<script name="Tree" setup>
import { listTree, getTree, delTree, addTree, updateTree } from "@/api/demo/tree";

const { proxy } = getCurrentInstance();

const treeList = ref([]);
const open = ref(false);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const isExpandAll = ref(true);
const refreshTable = ref(true);
const treeOptions = ref(undefined);
const daterangeCreateTime = ref([]);

// 列显隐信息
const columns = ref([
  { key: 0, label: `父id`, visible: false },
  { key: 1, label: `部门id`, visible: true },
  { key: 2, label: `用户id`, visible: true },
  { key: 3, label: `树节点名`, visible: true },
  { key: 4, label: `创建时间`, visible: true }
]);

const data = reactive({
  // 查询参数
  queryParams: {
    treeName: null,
    createTime: null,
  },
  // 表单参数
  form: {},
  // 表单校验
  rules: {
    treeName: [
      { required: true, message: "树节点名不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询测试树表列表 */
function getList() {
  loading.value = true;
  listTree(proxy.addDateRange(queryParams.value, daterangeCreateTime.value, "CreateTime")).then(response => {
    treeList.value = proxy.handleTree(response.data, "id", "parentId");
    total.value = response.total;
    loading.value = false;
  });
}
/** 查询部门下拉树结构 */
async function getTreeselect() {
  await listTree().then(response => {
    treeOptions.value = [];
    const data = { id: 0, treeName: '顶级节点', children: [] };
    data.children = proxy.handleTree(response.data, "id", "parentId");
    treeOptions.value.push(data);
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
    parentId: undefined,
    deptId: undefined,
    userId: undefined,
    treeName: undefined,
    version: undefined,
    createTime: undefined,
    createBy: undefined,
    updateTime: undefined,
    updateBy: undefined,
    delFlag: undefined
  };
  proxy.resetForm("treeRef");
}
/** 搜索按钮操作 */
function handleQuery() {
  getList();
}
/** 重置按钮操作 */
function resetQuery() {
  daterangeCreateTime.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}
/** 新增按钮操作 */
async function handleAdd(row) {
  reset();
  await getTreeselect();
  if (row != null && row.id) {
    form.value.parentId = row.id;
  } else {
    form.value.parentId = 0;
  }
  open.value = true;
  title.value = "添加测试树表";
}

/** 展开/折叠操作 */
function toggleExpandAll() {
  refreshTable.value = false;
  isExpandAll.value = !isExpandAll.value;
  nextTick(() => {
    refreshTable.value = true;
  });
}

/** 修改按钮操作 */
async function handleUpdate(row) {
  loading.value = true;
  reset();
  await getTreeselect();
  if (row != null) {
    form.value.parentId = row.id;
  }
  getTree(row.id).then((response) => {
    loading.value = false;
    form.value = response.data;
    open.value = true;
    title.value = "修改测试树表";
  });
}
/** 提交按钮 */
function submitForm() {
  proxy.$refs["treeRef"].validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id != null) {
        updateTree(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addTree(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除测试单表编号为"' + row.id + '"的数据项?').then(() => {
    loading.value = true;
    return delTree(row.id);
  }).then(() => {
    loading.value = false;
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).finally(() => {
    loading.value = false;
  });
}

getList()
</script>
