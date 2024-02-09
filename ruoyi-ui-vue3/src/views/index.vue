<template>
  <div class="app-container home">
    <div class="container mx-auto px-4 py-8">
      <el-row :gutter="20">
        <el-col v-for="card in cards" :key="card.title" :span="6">
          <el-card class="box-card">
            <div class="flex justify-between">
              <span>{{ card.title }}</span>
              <el-tag :type="card.tagType" effect="dark">{{ card.tag }}</el-tag>
            </div>
            <div class="text-3xl font-bold mt-4">{{ card.value }}</div>
            <div class="text-gray-500">{{ card.subtitle }}</div>
          </el-card>
        </el-col>
      </el-row>
      <!-- 中国地图 省级 一级页面 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <china-map :title="'全国各省司法案例分布'"/>
        </el-col>
        <el-col :span="12">
          <!--          <el-calendar v-model="value"/>-->
          <v-chart :option="option" autoresize class="m-5 mx-auto chart"></v-chart>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script name="Index" setup>
import {use} from "echarts/core";

import {countProvinceCase, incrementCase, incrementLaw, totalCase, totalLaw} from "@/api/manage/statisticAnalyse";
import {useRouter} from "vue-router";
import {CanvasRenderer} from "echarts/renderers";
import {LineChart, BarChart} from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent,
  GridComponent,
} from "echarts/components";
import {THEME_KEY} from "vue-echarts";
import {ref, provide, onMounted, watch} from "vue";
import {countCauseCase, countProcessCase, countTypeCase, countTypeLaw} from "@/api/manage/statisticAnalyse";
import * as echarts from 'echarts';


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


import chinaMap from "@/views/chinaMap";

const router = useRouter();
const value = ref(new Date())

const caseTotal = ref(0);
const lawTotal = ref(0);
const caseIncrement = ref(0);
const lawIncrement = ref(0);
const totalDocs = ref(0);
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
      // type: 'shadow',
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

const getCardData = async () => {
  // 使用 Promise.all 来同时执行所有异步函数
  const [totalCaseRes, incrementCaseRes, totalLawRes, incrementLawRes] = await Promise.all([
    totalCase(),
    incrementCase(),
    totalLaw(),
    incrementLaw()
  ]);
  // 将获取到的数据赋值给相应的变量
  caseTotal.value = totalCaseRes.data;
  caseIncrement.value = incrementCaseRes.data;
  lawTotal.value = totalLawRes.data;
  lawIncrement.value = incrementLawRes.data;
  totalDocs.value = caseTotal.value + lawTotal.value;
  setCards();
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
        percentage = totalDocs.value === 0 ? 0 : (caseIncrement.value / totalDocs.value) * 100;
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
        percentage = totalDocs.value === 0 ? 0 : (lawIncrement.value / totalDocs.value) * 100;
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


onMounted(() => {
  getCardData();
  getProvince()
})


</script>

<style lang="scss" scoped>
.box-card {
  @apply mb-4;
}


.screen {
  position: fixed;
  overflow: hidden;
  top: 50%;
  left: 50%;
  width: 100%;
  height: 100%;
  transform-origin: left top;
  background-color: pink;
}

.chart {
  width: 100%;
  height: 375px; /* 你可以根据需要调整高度 */
}
</style>

