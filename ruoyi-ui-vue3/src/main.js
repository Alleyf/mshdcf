import {createApp} from 'vue'

import Cookies from 'js-cookie'

import ElementPlus from 'element-plus'
import locale from 'element-plus/lib/locale/lang/zh-cn' // 中文语言

import '@/assets/styles/index.scss' // global css
import '@/assets/styles/tailwind.css'


import App from './App'
import store from './store'
import router from './router'
import directive from './directive' // directive
import VueCharts from 'vue-echarts'

// 引入vue-fullpage
import VueFullPage from 'vue-fullpage.js';
// import 'fullpage.js/vendors/scrolloverflow' // 如果需要使用scrollOverflow选项，需要引入此文件
import 'fullpage.js/dist/fullpage.min.css' // 引入fullpage.js的样式文件
// markdown预览插件
import VMdPreview from '@kangc/v-md-editor/lib/preview';
import '@kangc/v-md-editor/lib/style/preview.css';
import githubTheme from '@kangc/v-md-editor/lib/theme/github.js';
import '@kangc/v-md-editor/lib/theme/style/github.css';

// highlightjs
import hljs from 'highlight.js';

VMdPreview.use(githubTheme, {
  Hljs: hljs,
});

// 注册指令
import plugins from './plugins' // plugins
import {download} from '@/utils/request'
import {sendWebMessage} from "@/utils/websocket";


// svg图标
import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon'
import elementIcons from '@/components/SvgIcon/svgicon'


import './permission' // permission control

import {useDict} from '@/utils/dict'
import {getConfigKey, updateConfigByKey} from "@/api/system/config";
import {parseTime, resetForm, addDateRange, handleTree, selectDictLabel, selectDictLabels} from '@/utils/ruoyi'

// 分页组件
import Pagination from '@/components/Pagination'
// 自定义表格工具组件
import RightToolbar from '@/components/RightToolbar'
// 富文本组件
import QEditor from "@/components/Editor"
// 富文本组件
import WEditor from "@/components/WEditor"
// markdown 组件
import Vditor from "@/components/Vditor"


// 文件上传组件
import FileUpload from "@/components/FileUpload"
// 图片上传组件
import ImageUpload from "@/components/ImageUpload"
// 图片预览组件
import ImagePreview from "@/components/ImagePreview"
// 自定义树选择组件
import TreeSelect from '@/components/TreeSelect'
// 字典标签组件
import DictTag from '@/components/DictTag'
// iframe组件
import IFrame from "@/components/IFrame";
import useUserStore from "@/store/modules/user";


const app = createApp(App)

// 全局变量


// 全局方法挂载
app.config.globalProperties.useDict = useDict
app.config.globalProperties.getConfigKey = getConfigKey
app.config.globalProperties.updateConfigByKey = updateConfigByKey
app.config.globalProperties.download = download
app.config.globalProperties.parseTime = parseTime
app.config.globalProperties.resetForm = resetForm
app.config.globalProperties.handleTree = handleTree
app.config.globalProperties.addDateRange = addDateRange
app.config.globalProperties.selectDictLabel = selectDictLabel
app.config.globalProperties.selectDictLabels = selectDictLabels
app.config.globalProperties.sendWebMessage = sendWebMessage

// 全局组件挂载
app.component('DictTag', DictTag)
app.component('Pagination', Pagination)
app.component('TreeSelect', TreeSelect)
app.component('FileUpload', FileUpload)
app.component('ImageUpload', ImageUpload)
app.component('ImagePreview', ImagePreview)
app.component('RightToolbar', RightToolbar)
app.component('WEditor', WEditor)
app.component('QEditor', QEditor)
app.component('VEditor', Vditor)
app.component('IFrame', IFrame)
app.component('v-chart', VueCharts)
app.component('svg-icon', SvgIcon)


app.use(router)
app.use(store)
app.use(plugins)
app.use(elementIcons)
app.use(VueFullPage)
app.use(VMdPreview)

directive(app)

// 使用element-plus 并且设置全局的大小
app.use(ElementPlus, {
  locale: locale,
  // 支持 large、default、small
  size: Cookies.get('size') || 'default'
})

// 修改 el-dialog 默认点击遮照为不关闭
app._context.components.ElDialog.props.closeOnClickModal.default = true

app.mount('#app')
