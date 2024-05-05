<script setup>
// 1.1 引入Vditor 构造函数
import Vditor from 'vditor'
// 1.2 引入样式
import 'vditor/dist/index.css';

import {getToken} from "@/utils/auth";
import {onMounted, getCurrentInstance, ref, toRaw, onActivated, onBeforeUnmount} from "vue";

const props = defineProps({
  /* 编辑器的内容 */
  modelValue: {
    type: String,
  },
  /* 高度 */
  height: {
    type: Number,
    default: null,
  },
  /* 宽度 */
  width: {
    type: Number,
    default: null,
  },
  /* 最小高度 */
  minHeight: {
    type: Number,
    default: 300,
  },
  /* 只读 */
  readOnly: {
    type: Boolean,
    default: false,
  },
  /* 上传文件大小限制(MB) */
  fileSize: {
    type: Number,
    default: 5,
  },
  /* 类型（base64格式、url格式） */
  type: {
    type: String,
    default: "url",
  }
});

const {proxy} = getCurrentInstance();
// 上传的图片服务器地址
const uploadUrl = ref(import.meta.env.VITE_APP_BASE_API + "/resource/oss/upload");
const headers = ref({Authorization: "Bearer " + getToken()});

// 2. 获取DOM引用
const vditor = ref()

const option = ref({
  // 设置编辑器的宽高
  height: props.height,
  width: props.width,
  minHeight: props.minHeight,
  placeholder: "请输入内容",
  // 设置编辑器中展示的文本
  // 设置工具栏中展示的工具
  // toolbar: ['emoji', 'br', 'bold', '|', 'line', 'quote', 'list', 'check'],
  // 编辑器失去焦点后的回调函数
  blur(value) {
    // 保存文档....
    proxy.$emit('update:modelValue', value)
    // console.log('保存成功', props.modelValue)
  },
  toolbarConfig: {
    pin: true,
  },
  counter: {
    enable: true,
  },
  cache: {
    enable: false,
  },
  outline: {
    enable: true,
  },
  transform: (html) => {
    // vditor.value.destroy()

  },
  after: () => {
    // vditor.value.destroy()
    console.log(props.modelValue)
    vditor.value.setValue(props.modelValue ? props.modelValue : '')
  },
  upload: {
    // 配置上传地址
    url: uploadUrl.value,
    // 配置请求头
    headers: headers.value,
    // 配置请求参数
    extraData: {
      type: props.type,
    },
    accept: "image/*",
    multiple: false,
    fieldName: "file",
    // 配置请求成功回调
    success(editor, res) {
      res = JSON.parse(res);
      handleUploadSuccess(editor, res);
    },
    // 配置请求失败回调
    error(res) {
      console.log(res);
      res = JSON.parse(res);
      proxy.$modal.msgError(`上传失败:${res.msg}`);
    },

  },
  resize: {
    enable: true,
  },
  preview: {
    mode: "both",
    hljs: {
      enable: true,
      style: "github",
      lineNumber: true,
    },
    theme: {
      current: "ant-design",
    },
  }
})

// watch(() => props.modelValue, (v) => {
//   if (v !== content.value) {
//     content.value = v === undefined ? "<p></p>" : v;
//   }
// }, {immediate: true});
// 3. 在组件初始化时，就创建Vditor对象，并引用
onMounted(() => {
  vditor.value = new Vditor('vditor', option.value)
})
onBeforeUnmount(() => {
  if (vditor.value) {
    vditor.value.destroy();
  }
});

// 图片上传成功返回图片地址
function handleUploadSuccess(editor, res) {
  console.log(editor, res)
  if (res.code === 200) {
    const url = res.data.url;
    const fileName = res.data.fileName;
    console.log(url, fileName, vditor.value)
    vditor.value.insertValue(`![${fileName}](${url})`);
  } else {
    proxy.$modal.msgError(res.msg);
  }
}

// 图片上传前拦截
function handleBeforeUpload(file) {
  const type = ["image/jpeg", "image/jpg", "image/png", "image/svg"];
  const isJPG = type.includes(file.type);
  //检验文件格式
  if (!isJPG) {
    proxy.$modal.msgError(`图片格式错误!`);
    return false;
  }
  // 校检文件大小
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize;
    if (!isLt) {
      proxy.$modal.msgError(`上传文件大小不能超过 ${props.fileSize} MB!`);
      return false;
    }
  }
  proxy.$modal.loading("正在上传文件，请稍候...");
  return true;
}

// 图片失败拦截
function handleUploadError(err) {
  proxy.$modal.msgError("上传文件失败");
}
</script>

<template>
  <!-- 指定一个容器 -->
  <div>
    <!--    <el-upload-->
    <!--      v-if="type === 'url'"-->
    <!--      ref="uploadRef"-->
    <!--      :action="uploadUrl"-->
    <!--      :before-upload="handleBeforeUpload"-->
    <!--      :headers="headers"-->
    <!--      :on-error="handleUploadError"-->
    <!--      :on-success="handleUploadSuccess"-->
    <!--      :show-file-list="false"-->
    <!--      class="editor-img-uploader"-->
    <!--      name="file"-->
    <!--    >-->
    <!--    </el-upload>-->
    <div id="vditor" class="editor">

    </div>
  </div>

</template>
