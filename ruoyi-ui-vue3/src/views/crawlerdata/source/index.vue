<script setup>
import {listSource, getSource, delSource, addSource, updateSource} from "@/api/crawlerdata/source";
import {getCurrentInstance, onMounted, reactive, ref} from "vue";
import {ElMessage, ElMessageBox} from "element-plus";

const sourceList = ref([]);
const total = ref(0);
const loading = ref(true);
const buttonLoading = ref(false);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const showSearch = ref(true);

let queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  sourceName: null,
  sourceUrl: null,
  sourceTypeId: null,
  status: null,
});

let form = reactive({
  id: null,
  sourceName: null,
  sourceUrl: null,
  sourceTypeId: null,
  remark: null,
  status: null,
});

const rules = reactive({
  id: [
    {required: true, message: "数据源id不能为空", trigger: "blur"}
  ],
  sourceName: [
    {required: true, message: "数据源名称不能为空", trigger: "blur"}
  ],
  sourceUrl: [
    {required: true, message: "数据源url地址不能为空", trigger: "blur"}
  ],
  sourceTypeId: [
    {required: true, message: "数据源类型不能为空", trigger: "change"}
  ],
  remark: [
    {required: true, message: "数据源备注说明不能为空", trigger: "blur"}
  ],
  status: [
    {required: true, message: "状态不能为空", trigger: "change"}
  ],
});
const {proxy} = getCurrentInstance();
const {crawler_source_type, crawl_common_status} = proxy.useDict("crawler_source_type", "crawl_common_status");


const title = ref("");
const open = ref(false);

const queryForm = ref(null);
const dialogForm = ref(null);

const getList = () => {
  loading.value = true;
  listSource(queryParams).then(response => {
    sourceList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
};


const handleQuery = () => {
  queryParams.pageNum = 1;
  getList();
};


const resetQueryForm = () => {
  queryForm.value.resetFields();
  queryForm.value.clearValidate();
};

const resetQuery = () => {
  resetQueryForm();
  handleQuery();
};


const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
};

const resetForm = () => {
  // 清空表单和校验提示
  dialogForm.value.resetFields()
  dialogForm.value.clearValidate()
}

const cancelDialog = () => {
  open.value = false;
  resetForm();
};

const handleAdd = () => {
  open.value = true;
  title.value = "添加爬虫数据源";
};

const handleUpdate = (row) => {
  loading.value = true;
  const id = row.id || ids.value;
  getSource(id).then(response => {
    loading.value = false;
    form = response.data;
    open.value = true;
    title.value = "修改爬虫数据源";
  });
};

const submitForm = () => {
  // 通过表单ref属性结合ref响应式变量引用表单组件对property的value属性进行校验
  dialogForm.value.validate(valid => {
    if (valid) {
      buttonLoading.value = true;
      if (form.id != null) {
        updateSource(form).then(response => {
          ElMessage.success("修改成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      } else {
        addSource(form).then(response => {
          ElMessage.success("新增成功");
          open.value = false;
          getList();
        }).finally(() => {
          buttonLoading.value = false;
        });
      }
    }
  });
};

const handleDelete = (row) => {
  const ids = row.id || ids.value;
  ElMessageBox.confirm('是否确认删除爬虫数据源编号为"' + ids + '"的数据项？').then(() => {
    loading.value = true;
    return delSource(ids);
  }).then(() => {
    loading.value = false;
    getList();
    ElMessage.success("删除成功");
  }).catch(() => {
    ElMessage.error("删除失败")
  }).finally(() => {
    loading.value = false;
  });
};

const handleExport = () => {
  download('crawlerdata/source/export', {
    ...queryParams
  }, `source_${new Date().getTime()}.xlsx`)
};

onMounted(() => {
  getList();
});
</script>


<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :inline="true" :model="queryParams" label-width="100px" size="small">
      <el-form-item label="数据源名称" prop="sourceName">
        <el-input
          v-model="queryParams.sourceName"
          clearable
          placeholder="请输入数据源名称"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据源url地址" prop="sourceUrl">
        <el-input
          v-model="queryParams.sourceUrl"
          clearable
          placeholder="请输入数据源url地址"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="数据源类型" prop="sourceTypeId">
        <el-select v-model="queryParams.sourceTypeId" clearable placeholder="请选择数据源类型">
          <el-option
            v-for="dict in crawler_source_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" clearable placeholder="请选择状态">
          <el-option
            v-for="dict in crawl_common_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button icon="Search" size="small" type="primary" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" size="small" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['crawlerdata:source:add']"
          icon="el-icon-plus"
          plain
          size="default"
          type="primary"
          @click="handleAdd"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['crawlerdata:source:edit']"
          :disabled="single"
          icon="el-icon-edit"
          plain
          size="default"
          type="success"
          @click="handleUpdate"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['crawlerdata:source:remove']"
          :disabled="multiple"
          icon="el-icon-delete"
          plain
          size="default"
          type="danger"
          @click="handleDelete"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          v-hasPermi="['crawlerdata:source:export']"
          icon="el-icon-download"
          plain
          size="default"
          type="warning"
          @click="handleExport"
        >导出
        </el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="sourceList" @selection-change="handleSelectionChange">
      <el-table-column align="center" type="selection" width="55"/>
      <el-table-column v-if="true" align="center" label="数据源id" prop="id"/>
      <el-table-column align="center" label="数据源名称" prop="sourceName"/>
      <el-table-column align="center" label="数据源url地址" prop="sourceUrl">
        <template #default="scope">
          <el-link :href="scope.row.sourceUrl" target="_blank" type="primary">{{ scope.row.sourceUrl }}</el-link>
        </template>
      </el-table-column>
      <el-table-column align="center" label="数据源类型" prop="sourceTypeId">
        <template #default="scope">
          <dict-tag :options="crawler_source_type" :value="scope.row.sourceTypeId"/>
        </template>
      </el-table-column>
      <el-table-column align="center" label="数据源备注说明" prop="remark"/>
      <el-table-column align="center" label="状态" prop="status">
        <template #default="scope">
          <dict-tag :options="crawl_common_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column align="center" class-name="small-padding fixed-width" label="操作">
        <template #default="scope">
          <el-button
            v-hasPermi="['crawlerdata:source:edit']"
            icon="Edit"
            size="mini"
            type="text"
            @click="handleUpdate(scope.row)"
          >修改
          </el-button>
          <el-button
            v-hasPermi="['crawlerdata:source:remove']"
            icon="Delete"
            size="mini"
            type="text"
            @click="handleDelete(scope.row)"
          >删除
          </el-button>
        </template>
      </el-table-column>
      <template #empty>
        <el-empty></el-empty>
      </template>
    </el-table>

    <pagination
      v-show="total>0"
      :limit.sync="queryParams.pageSize"
      :page.sync="queryParams.pageNum"
      :total="total"
      @pagination="getList"
    />

    <!-- 添加或修改爬虫数据源对话框 -->
    <el-dialog v-model="open" :title="title" align-center append-to-body width="40%">
      <el-form ref="dialogForm" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="数据源名称" prop="sourceName">
          <el-input v-model="form.sourceName" placeholder="请输入数据源名称"/>
        </el-form-item>
        <el-form-item label="数据源url地址" prop="sourceUrl">
          <el-input v-model="form.sourceUrl" placeholder="请输入数据源url地址"/>
        </el-form-item>
        <el-form-item label="数据源类型" prop="sourceTypeId">
          <el-select v-model="form.sourceTypeId" placeholder="请选择数据源类型">
            <el-option
              v-for="dict in crawler_source_type"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="数据源备注说明" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入数据源备注说明"/>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in crawl_common_status"
              :key="dict.value"
              :label="parseInt(dict.value)"
            >{{ dict.label }}
            </el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
      <span class="dialog-footer">
        <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancelDialog(dialogForm)">取 消</el-button>
      </span>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>

el-form-item el-button {
  justify-items: center;
  align-items: center;
  text-align: center;
}

</style>
