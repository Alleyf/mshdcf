<template>
  <div class="app-container home mt-0.5">
    <div class="container mx-auto my-8">
      <el-row :gutter="20" class="flex justify-between items-center">
        <!-- 装饰元素 -->
        <el-col :span="6">
          <div class="text-center"></div>
        </el-col>
        <!-- 平台标题 -->
        <el-col :span="12">
          <h1 class="gradient-text text-2xl font-bold text-center">多源异构司法数据汇聚融合平台</h1>
        </el-col>
        <!-- 当前时间 -->
        <el-col :span="6">
          <time class="text-xl font-bold">{{ currentTime }}</time>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="my-8">
        <el-col v-for="(card,index) in cards" :key="card.title" :span="6">
          <el-card :class="{'cursor-pointer':true}" :shadow="'hover'" class="box-card" @click="handleClick(index)">
            <div class="flex justify-between">
              <span>{{ card.title }}</span>
              <el-tag :type="card.tagType" effect="dark">{{ card.tag }}</el-tag>
            </div>
            <div class="text-4xl font-bold mt-4 flex justify-evenly items-center">{{ card.value }}
              <!-- 对于平台司法案例（总数） -->
              <Icon v-if="index === 0" class="text-4xl text-green-500" icon="material-symbols:gavel"/>
              <!-- 对于平台司法案例（日增量） -->
              <Icon v-if="index === 1" class="text-4xl text-yellow-500" icon="material-symbols:calendar-add-on"/>
              <!-- 对于平台法律法规（总数） -->
              <Icon v-if="index === 2" class="text-4xl text-red-500" icon="ic:twotone-menu-book"/>
              <!-- 对于平台法律法规（日增量） -->
              <Icon v-if="index === 3" class="text-4xl text-blue-500" icon="material-symbols:box-add-rounded"/>
            </div>
            <div class="text-gray-500 text-center">{{ card.subtitle }}</div>
          </el-card>
        </el-col>
      </el-row>
      <!--      最新文书排行榜-->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-card class="box-card bg-blue-100">
            <h2 class="text-2xl font-bold text-center">最新司法案例TOP10</h2>
            <div class="scroll-container">
              <ul v-if="topTenCases!==undefined" class="scroll-list">
                <li v-for="(caseItem, index) in topTenCases" :key="index" class="scroll-list-item">
                  <el-link :underline="false" class="decoration-0 hover:text-red-500" type="primary">
                    <router-link :to="{name:'CaseDetail',params:{id:caseItem.id}}"
                                 class="flex justify-between items-center">
                      <Icon class="text-2xl text-green-500" icon="ic:twotone-menu-book"/>
                      {{ index + 1 }}.{{ caseItem.name }}
                    </router-link>
                  </el-link>
                </li>
              </ul>
            </div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card class="box-card">
            <h2 class="text-2xl font-bold text-center">最新法律法规TOP10</h2>
            <div class="scroll-container">
              <ul v-if="topTenLaws!==undefined" class="scroll-list">
                <li v-for="(lawItem, index) in topTenLaws" :key="index" class="scroll-list-item">
                  <el-link :underline="false" class="decoration-0 hover:text-red-500" type="primary">
                    <router-link :to="{name:'LawDetail',params:{id:lawItem.id}}"
                                 class="flex justify-between items-center">
                      <Icon class="text-2xl text-red-500" icon="ic:twotone-menu-book"/>
                      {{ index + 1 }}.{{ lawItem.name }}
                    </router-link>
                  </el-link>
                </li>
              </ul>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script name="Index" setup>
import {use} from "echarts/core";
import {Icon} from '@iconify/vue';

import {
  countProvinceCase,
  incrementCase,
  incrementLaw,
  totalCase,
  totalLaw,
  rankTenCases,
  rankTenLaws
} from "@/api/manage/statisticAnalyse";
import {useRouter} from "vue-router";
import {CanvasRenderer} from "echarts/renderers";
import {LineChart, BarChart} from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent,

} from "echarts/components";
import {THEME_KEY} from "vue-echarts";
import {ref, provide, onMounted, watch} from "vue";
import {countCauseCase, countProcessCase, countTypeCase, countTypeLaw} from "@/api/manage/statisticAnalyse";
import * as echarts from 'echarts';
import chinaMap from "@/views/chinaMap";


use([
  CanvasRenderer,
  LineChart,
  BarChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent
]);

provide(THEME_KEY, "white");


const router = useRouter();
const currentTime = ref(new Date().toLocaleString());

const caseTotal = ref(0);
const lawTotal = ref(0);
const caseIncrement = ref(0);
const lawIncrement = ref(0);
const totalDocs = ref(0);
const topTenCases = ref([]);
const topTenLaws = ref([]);

const cards = ref([
  {
    name: 'caseTotal',
    title: '平台司法案例',
    value: caseTotal,
    subtitle: "平台总文书占比：",
    tag: '总数',
    tagType: 'success'
  },
  {
    name: 'caseIncrement',
    title: '平台司法案例',
    value: caseIncrement,
    subtitle: '案例文书占比：',
    tag: '日增量',
    tagType: 'warning'
  },
  {
    name: 'lawTotal',
    title: '平台法律法规',
    value: lawTotal,
    subtitle: '平台总文书占比：',
    tag: '总数',
    tagType: 'danger'
  },
  {
    name: 'lawIncrement',
    title: '平台法律法规',
    value: lawIncrement,
    subtitle: '法条文书占比：',
    tag: '日增量',
    tagType: ''
  },
]);

const lineData = ref([])
const barData = ref([])
const allProvinces = [
  "山东", "福建", "上海", "甘肃", "浙江", "广西", "重庆", "湖南", "江西", "河北", "河南", "西藏",
  "云南", "黑龙江", "吉林", "辽宁", "广东", "青海", "陕西", "新疆", "江苏", "北京", "重庆", "南海诸岛",
  "安徽", "湖北", "海南", "四川", "台湾", "天津", "香港", "澳门", "山西", "内蒙古", "宁夏", "贵州",
];

const option = ref({
  backgroundColor: "#ffffff",
  tooltip: {
    trigger: 'axis',

    axisPointer: {
      type: 'cross',
      label: {
        backgroundColor: '#6a7985'
      }
    }
  },
  toolbox: {
    feature: {
      dataZoom: {
        yAxisIndex: 'none'
      },
      saveAsImage: {},
      dataView: {
        readOnly: false
      },
      magicType: {
        type: ['line', 'bar']
      },

    }
  },
  dataZoom: [{
    type: 'inside',
    start: 0,
    end: 100
  }, {
    start: 0,
    end: 100,
    handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V2'
  }],
  grid: {
    top: '8%',
    left: '1%',
    right: '1%',
    bottom: '8%',
    containLabel: true,
  },
  legend: {
    icon: 'circle',
    data: ['文书占比', '案例个数'],
    textStyle: {
      color: '#999999',
      borderColor: '#fff'
    },
  },
  xAxis: [{
    type: 'category',
    boundaryGap: true,
    axisLine: { //坐标轴轴线相关设置。数学上的x轴
      show: true,
      lineStyle: {
        color: 'rgba(0,15,77,0.89)'
      },
    },
    axisLabel: { //坐标轴刻度标签的相关设置
      textStyle: {
        color: 'rgba(0,10,52,0.89)',
        margin: 15,
      },
    },
    axisTick: {
      show: false,
    },
    data: allProvinces,
  }],
  yAxis: [{
    type: 'value',
    min: 0,
    // max: 140,
    splitNumber: 7,
    splitLine: {
      show: true,
      lineStyle: {
        color: '#0a3256'
      }
    },
    axisLine: {
      show: false,
    },
    axisLabel: {
      margin: 20,
      textStyle: {
        color: 'rgba(0,10,52,0.89)',

      },
    },
    axisTick: {
      show: false,
    },
  }],
  series: [{
    name: '文书占比',
    type: 'line',
    symbol: 'circle',  // 默认是空心圆（中间是白色的），改成实心圆
    showAllSymbol: true,
    symbolSize: 8,
    lineStyle: {
      color: "#28ffb3", // 线条颜色
    },
    itemStyle: {
      color: "#28ffb3",
      borderColor: '#fff'
    },
    tooltip: {
      show: true,
      trigger: "item",
      formatter: "{a} <br/>{b} : {c} %"
    },
    areaStyle: { //区域填充样式
      //线性渐变，前4个参数分别是x0,y0,x2,y2(范围0~1);相当于图形包围盒中的百分比。如果最后一个参数是‘true’，则该四个值是绝对像素位置。
      color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
        offset: 0,
        color: 'rgba(0,154,120,1)'
      },
        {
          offset: 1,
          color: 'rgba(0,0,0, 0)'
        }
      ], false),
      shadowColor: 'rgba(53,142,215, 0.9)', //阴影颜色
      shadowBlur: 20 //shadowBlur设图形阴影的模糊大小。配合shadowColor,shadowOffsetX/Y, 设置图形的阴影效果。
    },
    data: lineData.value
  }, {
    name: '案例个数',
    type: 'bar',
    barWidth: 20,
    tooltip: {
      show: true,
      trigger: "item",
      formatter: "{a} <br/>{b} : {c} 个"

    },
    itemStyle: {
      color: '#1492FF'
    },
    emphasis: {
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          {offset: 0, color: '#2378f7'},
          {offset: 0.7, color: '#2378f7'},
          {offset: 1, color: '#83bff6'}
        ])
      }
    },
    data: barData.value
  }]
});

const setTime = () => {
  setInterval(() => {
    currentTime.value = new Date().toLocaleString();
  }, 1000);
}

const getCardData = async () => {
  // 使用 Promise.all 来同时执行所有异步函数
  const [totalCaseRes, incrementCaseRes, totalLawRes, incrementLawRes] = await Promise.all([
    totalCase(),
    incrementCase(),
    totalLaw(),
    incrementLaw()
  ]);
  // alert(totalCaseRes.data, incrementCaseRes.data, totalLawRes.data, incrementLawRes.data)
  // 将获取到的数据赋值给相应的变量
  caseTotal.value = totalCaseRes.data;
  caseIncrement.value = incrementCaseRes.data;
  lawTotal.value = totalLawRes.data;
  lawIncrement.value = incrementLawRes.data;
  totalDocs.value = caseTotal.value + lawTotal.value;
  if (totalDocs.value) {
    setCards();
  }
}

const setCards = () => {
  let percentage, percentageString;
  cards.value.forEach(card => {
    switch (card.name) {
      case 'caseTotal':
        card.value = caseTotal.value;
        percentage = totalDocs.value === 0 ? 0 : (caseTotal.value / totalDocs.value) * 100;
        percentageString = percentage.toFixed(2) + '%';
        card.subtitle = `平台总文书占比：${percentageString}`;
        break;
      case 'caseIncrement':
        card.value = caseIncrement.value;
        percentage = totalDocs.value === 0 ? 0 : (caseIncrement.value / caseTotal.value) * 100;
        percentageString = percentage.toFixed(2) + '%';
        card.subtitle = `案例文书占比：${percentageString}`;
        break;
      case 'lawTotal':
        card.value = lawTotal.value;
        percentage = totalDocs.value === 0 ? 0 : (lawTotal.value / totalDocs.value) * 100;
        percentageString = percentage.toFixed(2) + '%';
        card.subtitle = `平台总文书占比：${percentageString}`;
        break;
      case 'lawIncrement':
        card.value = lawIncrement.value;
        percentage = totalDocs.value === 0 ? 0 : (lawIncrement.value / lawTotal.value) * 100;
        percentageString = percentage.toFixed(2) + '%';
        card.subtitle = `法条文书占比：${percentageString}`;
        break;
    }
  })
}

const getProvince = () => {
  let resData = {};
  let total = 0;
  countProvinceCase().then((res) => {
    resData = res.data; // 存储返回的数据
    allProvinces.forEach((province) => {
      barData.value.push(resData[province] || 0);
      total += resData[province] || 0;
    })
    barData.value.forEach((province) => (
      lineData.value.push((province * 100 / totalDocs.value).toFixed(1))
    ))
    // console.log(caseTotal.value, barData.value, lineData.value)
  })

}


const handleClick = (index) => {
  switch (index) {
    case 0:
      router.push({path: '/manage/case'});
      break;
    case 1:
      router.push({path: '/manage/case'});
      break
    case 2:
      router.push({path: '/manage/regulation'});
      break;
    case 3:
      router.push({path: '/manage/regulation'});
      break;
  }
}


const getTopTen = async () => {
  const [rankTenCasesRes, rankTenLawsRes] = await Promise.all([
    rankTenCases(),
    rankTenLaws()
  ]);
  topTenCases.value = rankTenCasesRes.data;
  topTenLaws.value = rankTenLawsRes.data;
  console.log(topTenCases.value, topTenLaws.value)
  if (topTenCases.value && topTenLaws.value) {
    updateList();
  }
}


// 更新列表的方法
function updateList() {
  setInterval(() => {
    topTenCases.value.push(topTenCases.value.shift());
    topTenLaws.value.push(topTenLaws.value.shift());
  }, 2000);
}

onMounted(() => {
  setTime();
  getCardData();
  // getProvince();
  getTopTen();
})


</script>

<style lang="scss" scoped>


/* 为页面容器添加一些样式 */
.app-container {
  //background-color: #f5f5f5; /* 设置背景颜色 */
  color: #333; /* 设置文本颜色 */
  //background-image: url("../assets/images/bg2.png");
  //background-size: cover;
}


.box-card {
  @apply mb-4;
  margin-bottom: 20px; /* 增加卡片之间的间距 */
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1); /* 添加阴影 */
  border-radius: 4px; /* 添加圆角 */
  overflow: hidden;
  text-overflow: ellipsis; /* 超出部分显示省略号 */
  //opacity: 0.6;
}

.gradient-text {
  background: linear-gradient(135deg, #40e0d0, #ff8c00, #ff0080); /* 设置渐变色 */
  -webkit-background-clip: text; /* 限定背景绘制区域为文本 */
  color: transparent; /* 设置文本颜色为透明，以便背景色可以显示出来 */
  font-size: 24px; /* 设置字体大小 */
  transition: text-shadow 0.3s ease; /* 鼠标悬浮时的阴影过渡效果 */
}

.gradient-text:hover {
  text-shadow: 0 0 10px rgb(157, 139, 139); /* 添加鼠标悬停时的阴影 */
}


.screen {
  position: fixed;
  overflow: hidden;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  transform-origin: left top;
  //background-color: pink;
}


.scroll-list-item {
  white-space: nowrap; /* 文本不换行 */
  overflow: hidden; /* 隐藏超出部分 */
  text-overflow: ellipsis; /* 超出部分显示省略号 */
  padding: 10px;
  border-bottom: 1px solid #eee;
  transition: color 0.3s; /* 添加过渡效果 */
  a {
    text-decoration: none;
  }

  :hover {
    color: #ff4b4b; /* 鼠标悬浮时的颜色 */
  }
}

</style>

