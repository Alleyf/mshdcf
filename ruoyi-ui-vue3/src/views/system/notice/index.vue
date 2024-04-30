<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryRef" :inline="true" :model="queryParams">
      <el-form-item label="公告标题" prop="noticeTitle">
        <el-input
          v-model="queryParams.noticeTitle"
          clearable
          placeholder="请输入公告标题"
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人员" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          clearable
          placeholder="请输入操作人员"
          style="width: 200px"
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="noticeType">
        <el-select v-model="queryParams.noticeType" clearable placeholder="公告类型" style="width: 200px">
          <el-option
            v-for="dict in sys_notice_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:notice:add']"
          icon="Plus"
          plain
          type="primary"
          @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:notice:edit']"
          :disabled="single"
          icon="Edit"
          plain
          type="success"
          @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['system:notice:remove']"
          :disabled="multiple"
          icon="Delete"
          plain
          type="danger"
          @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column align="center" label="序号" prop="noticeId" width="100"/>
      <el-table-column
        :show-overflow-tooltip="true"
        align="center"
        label="公告标题"
        prop="noticeTitle"
      />
      <el-table-column align="center" label="公告类型" prop="noticeType" width="100">
        <template #default="scope">
          <dict-tag :options="sys_notice_type" :value="scope.row.noticeType"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="状态" prop="status" width="100">
        <template #default="scope">
          <dict-tag :options="sys_notice_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="创建者" prop="createBy" width="100"/>
      <el-table-column align="center" label="创建时间" prop="createTime" width="100">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template #default="scope">
          <el-button v-hasPermi="['system:notice:edit']" icon="Edit" link type="primary"
                     @click="handleUpdate(scope.row)">修改
          </el-button>
          <el-button v-hasPermi="['system:notice:remove']" icon="Delete" link type="primary"
                     @click="handleDelete(scope.row)">删除
          </el-button>

          <el-popover :width="300" placement="top-start" title="选择发送对象" trigger="contextmenu">
            <template #reference>
              <el-button v-hasPermi="['system:notice:query']" icon="Promotion" link type="primary"
                         @click="handleSend(scope.row)">发送
              </el-button>
            </template>
            <template #default>
              <el-container>
                <el-checkbox
                  v-model="checkAll"
                  :indeterminate="indeterminate"
                  @change="handleCheckAll"
                >
                  All
                </el-checkbox>
                <el-select-v2
                  v-model="targetIds"
                  :max-collapse-tags="1"
                  :options="options"
                  clearable
                  collapse-tags
                  multiple
                  placeholder="请选择目标对象"
                  popper-class="custom-header"
                  style="width: 240px"
                >
                </el-select-v2>
              </el-container>

            </template>
          </el-popover>
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

    <!-- 添加或修改公告对话框 -->
    <el-dialog v-model="open" :title="title" :width="'60%'" append-to-body>
      <el-form ref="noticeRef" :model="form" :rules="rules" label-width="80px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="公告标题" prop="noticeTitle">
              <el-input v-model="form.noticeTitle" placeholder="请输入公告标题"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="公告类型" prop="noticeType">
              <el-select v-model="form.noticeType" placeholder="请选择">
                <el-option
                  v-for="dict in sys_notice_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in sys_notice_status"
                  :key="dict.value"
                  :label="dict.value"
                >{{ dict.label }}
                </el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="内容">
              <!--                            <q-editor v-model="form.noticeContent" :min-height="192"/>-->
              <v-editor v-if="open" v-model="form.noticeContent" :min-height="200" :width="'100%'"/>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script name="Notice" setup>
import {listNotice, getNotice, delNotice, addNotice, updateNotice, sendNotice} from "@/api/system/notice";
import {listUser} from "@/api/system/user";

const {proxy} = getCurrentInstance();
const {sys_notice_status, sys_notice_type} = proxy.useDict("sys_notice_status", "sys_notice_type");

const noticeList = ref([]);
const targetIds = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const checkAll = ref(false)
const indeterminate = ref(false)
const value = ref([])
const options = ref(
  Array.from(listUser().then(res => {
    const users = res.rows;
    users.map(user => {
      options.value.push({
        value: user.userId,
        label: user.userName
      })
    })
  }))
)


watch(value, (val) => {
  if (val.length === 0) {
    checkAll.value = false
    indeterminate.value = false
  } else if (val.length === options.value.length) {
    checkAll.value = true
    indeterminate.value = false
  } else {
    indeterminate.value = true
  }
})

const handleCheckAll = (val) => {
  indeterminate.value = false
  if (val) {
    targetIds.value = options.value.map((_) => _.value)
    // form.value.targetIds = options.value.map((_) => _.value)
  } else {
    targetIds.value = []
    // form.value.targetIds = []
  }
}

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    noticeTitle: undefined,
    targetIds: [],
    createBy: undefined,
    status: undefined
  },
  rules: {
    noticeTitle: [{required: true, message: "公告标题不能为空", trigger: "blur"}],
    noticeType: [{required: true, message: "公告类型不能为空", trigger: "change"}],
    targetIds: [{required: true, message: "目标对象不能为空", trigger: "change"}]
  },
});

const {queryParams, form, rules} = toRefs(data);

/** 查询公告列表 */
function getList() {
  loading.value = true;
  listNotice(queryParams.value).then(response => {
    noticeList.value = response.rows;
    noticeList.value.forEach(notice => {
      notice.targetIds = [];
    });
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
    noticeId: undefined,
    noticeTitle: undefined,
    targetIds: undefined,
    noticeType: undefined,
    noticeContent: undefined,
    status: "0"
  };
  proxy.resetForm("noticeRef");
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

/** 多选框选中数据 */
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.noticeId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加公告";
}

/**修改按钮操作 */
function handleUpdate(row) {
  reset();
  const noticeId = row.noticeId || ids.value;
  getNotice(noticeId).then(response => {
    form.value = response.data;
    // form.value.targetIds = options.value;
    open.value = true;
    title.value = "修改公告";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["noticeRef"].validate(valid => {
    if (valid) {
      if (form.value.noticeId != undefined) {
        updateNotice(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addNotice(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const noticeIds = row.noticeId || ids.value
  proxy.$modal.confirm('是否确认删除公告编号为"' + noticeIds + '"的数据项？').then(function () {
    return delNotice(noticeIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {
  });
}

const handleSend = (row) => {
  row.targetIds = targetIds.value
  console.log(row, form.value)
  if (!row.targetIds.length) {
    proxy.$modal.msgWarning("请选择目标对象");
    return
  }
  sendNotice(row).then(response => {
    if (response.code === 200) {
      targetIds.value = []
      proxy.$modal.msgSuccess("发送成功");
    } else {
      proxy.$modal.msgError("发送失败");
    }
  })
}

getList();
</script>
<style lang="scss">
.custom-header {
  .el-checkbox {
    display: flex;
    height: unset;
  }
}
</style>
