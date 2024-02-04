<template>
  <div>
    <div style="border: 1px solid #ccc; margin-top: 10px;">
      <!-- 工具栏 -->
      <Toolbar
        :default-config="toolbarConfig"
        :editor="editor"
        style="border-bottom: 1px solid #ccc"
      />
      <!-- 编辑器 -->
      <Editor
        v-model="currentValue"
        :default-config="editorConfig"
        :style="{ 'height': editorHeight, 'overflow-y': 'hidden'}"
        @onChange="onChange"
        @onCreated="onCreated"
      />
    </div>
  </div>
</template>

<script>
import {Editor, Toolbar} from '@wangeditor/editor-for-vue'
import {getToken} from "@/utils/auth";

export default {
  name: "WEditor",
  components: {Editor, Toolbar},
  props: {
    /* 编辑器的内容 */
    value: {
      type: String,
      default: "",
    },
    /* 高度 */
    height: {
      type: Number,
      default: null,
    },
    /* 最小高度 */
    minHeight: {
      type: Number,
      default: null,
    },
    /* 只读 */
    readOnly: {
      type: Boolean,
      default: false,
    },
    // 上传文件大小限制(MB)
    fileSize: {
      type: Number,
      default: 5,
    },
    /* 类型（base64格式、url格式） */
    type: {
      type: String,
      default: "url",
    },
    editorHeight: {
      type: String,
      default: '400px'
    },
  },
  data() {
    return {
      editor: null,
      toolbarConfig: {
        // toolbarKeys: [ /* 显示哪些菜单，如何排序、分组 */ ],
        // excludeKeys: [ /* 隐藏哪些菜单 */ ],
      },
      editorConfig: {
        placeholder: '请输入内容...',
        // autoFocus: false,
        // 所有的菜单配置，都要在 MENU_CONF 属性下
        MENU_CONF: {
          uploadImage: {
            server: import.meta.env.VITE_APP_BASE_API + "/resource/oss/upload",
            // 自定义增加 http  header
            headers: {
              Authorization: "Bearer " + getToken()
            },
            // 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
            allowedFileTypes: ['image/*'],
            // 最多可上传几个文件，默认为 100
            maxNumberOfFiles: 10,
            // 单个文件的最大体积限制，默认为 5M
            maxFileSize: 5 * 1024 * 1024, // 5M
            // form-data fieldName ，默认值 'wangeditor-uploaded-image'
            fieldName: 'file',
            // 自定义插入图片
            customInsert(res, insertFn) {
              // res 即服务端的返回结果
              // 从 res 中找到 url alt href ，然后插图图片
              const url = res.data.url
              const alt = res.data.fileName
              const href = null
              insertFn(url, alt, href)
            },
            // 单个文件上传成功之后
            onSuccess(file, res) {          // JS 语法
              console.log(`${file.name} 上传成功`, res)
            },
            // 单个文件上传失败
            onFailed(file, res) {           // JS 语法
              console.error(`${file.name} 上传失败`, res)
            },
            // 上传错误，或者触发 timeout 超时
            onError(file, err, res) {               // JS 语法
              console.error(`${file.name} 上传出错`, err, res)
            },
          },
          uploadVideo: {
            server: import.meta.env.VITE_APP_BASE_API + "/resource/oss/upload",
            // 自定义增加 http  header
            headers: {
              Authorization: "Bearer " + getToken()
            },
            // 选择文件时的类型限制，默认为 ['image/*'] 。如不想限制，则设置为 []
            allowedFileTypes: ['video/*'],
            // 最多可上传几个文件，默认为 5
            maxNumberOfFiles: 1,
            // 单个文件的最大体积限制，默认为 5M
            maxFileSize: 20 * 1024 * 1024, // 5M
            // form-data fieldName ，默认值 'wangeditor-uploaded-image'
            fieldName: 'file',
            // 超时时间，默认为 30 秒
            timeout: 15 * 1000, // 15 秒
            // 自定义插入图片
            customInsert(res, insertFn) {
              const url = res.data.url
              const poster = res.data.poster
              // res 即服务端的返回结果
              // 从 res 中找到 url poster ，然后插入视频
              insertFn(url, poster)
            },
            // 单个文件上传成功之后
            onSuccess(file, res) {          // JS 语法
              console.log(`${file.name} 上传成功`, res)
            },
            // 单个文件上传失败
            onFailed(file, res) {          // JS 语法
              console.log(`${file.name} 上传失败`, res)
            },
            // 上传错误，或者触发 timeout 超时
            onError(file, err, res) {               // JS 语法
              console.log(`${file.name} 上传出错`, err, res)
            },
          },
        },
        readOnly: this.readOnly,
        codeSelectLang: {
          codeLangs: [
            {text: 'CSS', value: 'css'},
            {text: 'HTML', value: 'html'},
            {text: 'XML', value: 'xml'},
            // 其他
          ]
        }
      },
      currentValue: "",
    };
  },
  computed: {
    styles() {
      let style = {};
      if (this.minHeight) {
        style.minHeight = `${this.minHeight}px`;
      }
      if (this.height) {
        style.height = `${this.height}px`;
      }
      return style;
    },
  },
  watch: {
    value: {
      handler(val) {
        if (val !== this.currentValue) {
          this.currentValue = val === null ? "" : val;
          if (this.editor) {
            this.editor.setContent(this.currentValue);
          }
        }
      },
      immediate: true,
    },
  },
  beforeDestroy() {
    const editor = this.editor
    if (editor == null) return
    editor.destroy() // 组件销毁时，及时销毁 editor ，重要！！！
  },
  created() {
    console.log(this.$props.value)
    this.currentValue = this.$props.value
  },
  methods: {
    onCreated(editor) {
      this.editor = Object.seal(editor) // 【注意】一定要用 Object.seal() 否则会报错
      // onCreated 编辑器创建完毕时的回调函数。赋 父组件给的初值
      console.log(`${this.$props.value}`)
      // 确保父组件已传递了 value，并且值已存在
      if (this.$props.value) {
        console.log(`${this.$props.value}`);
        // 设置编辑器内容
        this.editor.setHtml(`<p>${this.$props.value}</p>`);
      }
    },
    onChange(editor) {
      // 子数据传递给父组件v-model绑定的数据
      this.$emit("input", editor.getHtml());
    },
  },
};
</script>
<style src="@wangeditor/editor/dist/css/style.css"></style>
