<script setup>
import {use} from "echarts/core";
import {CanvasRenderer} from "echarts/renderers";
import {PieChart} from "echarts/charts";
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent
} from "echarts/components";
import {THEME_KEY} from "vue-echarts";
import {ref, provide, onMounted} from "vue";
import {
  countCauseCase,
  countProcessCase,
  countProvinceCase,
  countTypeCase,
  countTypeLaw, totalCase, totalLaw
} from "@/api/manage/statisticAnalyse";
import * as echarts from 'echarts';

use([
  CanvasRenderer,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  ToolboxComponent
]);

provide(THEME_KEY, "white");

const defaultListTab = ref(1);

const lineData = ref([])
const barData = ref([])
const totalDocs = ref(0);
const allProvinces = [
  "山东", "福建", "上海", "甘肃", "浙江", "广西", "重庆", "湖南", "江西", "河北", "河南", "西藏",
  "云南", "黑龙江", "吉林", "辽宁", "广东", "青海", "陕西", "新疆", "江苏", "北京", "重庆", "南海诸岛",
  "安徽", "湖北", "海南", "四川", "台湾", "天津", "香港", "澳门", "山西", "内蒙古", "宁夏", "贵州",
];

const caseTypePie = ref({
  animation: true,
  title: {
    text: "司法案件",
    subtext: "种类",
    x: "center",
    y: "center",
    textStyle: {
      color: "#003b79",
      fontSize: 16,
      fontWeight: "600",
      align: "center",
      // "width": "200px"
    },
    subtextStyle: {
      color: "#003b79",
      fontSize: 12,
      fontWeight: "normal",
      align: "center"
    }
  },
  toolbox: {
    show: true,
    orient: "vertical",
    left: "left",
    top: "center",
    feature: {
      dataView: {
        readOnly: false
      },
      restore: {},
      saveAsImage: {}
    }
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: "horizontal",
    left: "center",
    data: []
  },
  series: [
    {
      name: "案例类型",
      type: "pie",
      center: ["50%", "60%"],
      radius: ["40%", "60%"],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      }
    }
  ]
});
const caseProcessPie = ref({
  title: {
    text: "案例审理进程统计",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  toolbox: {
    show: true,
    orient: "vertical",
    left: "left",
    feature: {
      dataView: {
        readOnly: false
      },
      restore: {},
      saveAsImage: {}
    }
  },
  legend: {
    orient: "vertical",
    left: "right",
    data: []
  },
  series: [
    {
      name: "审理进程",
      type: "pie",
      radius: "55%",
      center: ["50%", "60%"],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      }
    }
  ]
});
const caseCausePie = ref({
  title: {
    text: "案例案由统计",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  toolbox: {
    show: true,
    orient: "vertical",
    left: "right",
    feature: {
      dataView: {
        readOnly: false
      },
      restore: {},
      saveAsImage: {}
    }
  },
  legend: {
    orient: "vertical",
    left: "left",
    data: []
  },
  series: [
    {
      name: "案由",
      type: "pie",
      radius: "55%",
      center: ["50%", "60%"],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      }
    }
  ]
});
const lawTypePie = ref({
  animation: true,
  title: {
    text: "法律法规",
    subtext: "种类",
    x: "center",
    y: "center",
    textStyle: {
      color: "#003b79",
      fontSize: 16,
      fontWeight: "600",
      align: "center",
    },
    subtextStyle: {
      color: "#003b79",
      fontSize: 12,
      fontWeight: "normal",
      align: "center"
    }
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  toolbox: {
    show: true,
    orient: "vertical",
    left: "right",
    top: "center",
    feature: {
      dataView: {
        readOnly: false
      },
      restore: {},
      saveAsImage: {}
    }
  },
  legend: {
    orient: "horizontal",
    left: "center",
    data: []
  },
  series: [
    {
      name: "法条类型",
      type: "pie",
      radius: ["40%", "60%"],
      center: ["50%", "60%"],
      data: [],
      emphasis: {
        itemStyle: {
          shadowBlur: 10,
          shadowOffsetX: 0,
          shadowColor: "rgba(0, 0, 0, 0.5)"
        }
      }
    }
  ]
});
const allDocsBarLine = ref({
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
    end: 500
  },],
  grid: {
    top: '8%',
    left: '1%',
    right: '1%',
    bottom: '8%',
    containLabel: true,
    borderColor: 'rgb(218,213,213)',
    borderWidth: 1,
    shadowColor: 'rgb(255,255,255)',
    shadowBlur: 10,
    shadowOffsetX: 0,
    shadowOffsetY: 0,
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
    max: 500,
    splitNumber: 10,
    splitLine: {
      show: true,
      lineStyle: {
        color: '#dad5d5'
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

const initTypeCasePie = () => {
  // 案件类型饼图
  countTypeCase().then(res => {
    const map = res.data;
    caseTypePie.value.legend.data = map.keys;
    Object.keys(map).forEach(key => {
      caseTypePie.value.series[0].data.push({
        value: map[key],
        name: key
      })
    });
  });
};


const initTypeLawPie = () => {
  // 法案类型饼图
  countTypeLaw().then(res => {
    const map = res.data;
    lawTypePie.value.legend.data = map.keys;
    Object.keys(map).forEach(key => {
      lawTypePie.value.series[0].data.push({
        value: map[key],
        name: key
      })
    })
  });
  // const lawTypeChart = echarts.init(lawTypeRef.value)  // 初始化echarts图，注意，这里的topLeft是响应式的dom，必须要加value，这里我经常忘记
  // lawTypeChart.setOption(lawTypePie.value)  // 设置option
  // lawTypeChart.resize()  // 这里也比较关键，在option后，最好是重绘一下图，可以解决百分比宽高的问题，也就是解决第2个问题
  // window.addEventListener('resize', () => {  // 窗口大小变化后，重绘图
  //   lawTypeChart.resize()
  // })
};
const initProcessCasePie = () => {
  // 审理进程饼图
  countProcessCase().then(res => {
    const map = res.data;
    caseProcessPie.value.legend.data = map.keys;
    Object.keys(map).forEach(key => {
      caseProcessPie.value.series[0].data.push({
        value: map[key],
        name: key
      })
    })
  });
  // const caseProcessChart = echarts.init(caseProcessRef.value)  // 初始化echarts图，注意，这里的topLeft是响应式的dom，必须要加value，这里我经常忘记
  // caseProcessChart.setOption(caseTypePie.value)  // 设置option
  // caseProcessChart.resize()  // 这里也比较关键，在option后，最好是重绘一下图，可以解决百分比宽高的问题，也就是解决第2个问题
  // window.addEventListener('resize', () => {  // 窗口大小变化后，重绘图
  //   caseProcessChart.resize()
  // })
};
const initCauseCasePie = () => {
  // 案件案由饼图
  countCauseCase().then(res => {
    const map = res.data;
    caseCausePie.value.legend.data = map.keys;
    Object.keys(map).forEach(key => {
      caseCausePie.value.series[0].data.push({
        value: map[key],
        name: key
      })
    })
  });
  // const caseCauseChart = echarts.init(caseCauseRef.value)  // 初始化echarts图，注意，这里的topLeft是响应式的dom，必须要加value，这里我经常忘记
  // caseCauseChart.setOption(caseTypePie.value)  // 设置option
  // caseCauseChart.resize()  // 这里也比较关键，在option后，最好是重绘一下图，可以解决百分比宽高的问题，也就是解决第2个问题
  // window.addEventListener('resize', () => {  // 窗口大小变化后，重绘图
  //   caseCauseChart.resize()
  // })
};

const getDocsData = async () => {
  // 使用 Promise.all 来同时执行所有异步函数
  const [totalCaseRes, totalLawRes] = await Promise.all([
    totalCase(),
    totalLaw(),
  ]);
  // 将获取到的数据赋值给相应的变量
  totalDocs.value = totalCaseRes.data + totalLawRes.data;
  // allDocsBarLine.value.yAxis.max = totalCaseRes.data;
}
const getProvince = () => {
  let resData = {};
  let total = 0;
  countProvinceCase().then((res) => {
    resData = res.data; // 存储返回的数据
    // console.log(resData)
    allProvinces.forEach((province) => {
      barData.value.push(resData[province] || 0);
      total += resData[province] || 0;
    })
    barData.value.forEach((province) => (
      lineData.value.push((province * 100 / totalDocs.value).toFixed(1))
    ))
  })
}

const initAllDocsBar = () => {
  // 案件类型柱状图
  getDocsData();
  getProvince();
}


// 渲染完毕生命周期函数
onMounted(() => {
  initTypeCasePie()
  initTypeLawPie()
  initProcessCasePie()
  initCauseCasePie();
  initAllDocsBar();
})
import chinaMap from "@/views/chinaMap";


</script>

<template>
  <div class="mx-auto">
    <!--    <el-tabs class="mx-auto w-full" lazy="false" tab-position="left">-->
    <!--    <h2 class="text-center font-bold text-2xl">司法案件信息统计</h2>-->
    <el-tabs v-model="defaultListTab" :tab-position="'top'" class="el-tabs mx-1" style=""
             @tab-click="handleTabClick">
      <el-tab-pane :name="1" label="数据中台">
        <el-card class="m-2">
          <el-row :gutter="10" class="mx-auto flex justify-evenly items-center">
            <div ref="caseTypeRef" class="chart-container">
              <v-chart :option="caseTypePie" class="chart"></v-chart>
            </div>
            <div ref="caseProcessRef" class="chart-container">
              <v-chart :option="caseProcessPie" auto-resize class="chart"></v-chart>
            </div>
            <div ref="caseCauseRef" class="chart-container">
              <v-chart :option="caseCausePie" auto-resize class="chart"></v-chart>
            </div>
            <div ref="lawTypeRef" class="chart-container">
              <v-chart :option="lawTypePie" class="chart"></v-chart>
            </div>
          </el-row>
          <!-- 中国地图 省级 一级页面 -->
          <el-row :gutter="20">
            <el-col :span="12">
              <china-map :title="'全国各省司法案件分布'"/>
            </el-col>
            <el-col :span="12">
              <!--          <el-calendar v-model="value"/>-->
              <v-chart :option="allDocsBarLine" autoresize class="m-5 mx-auto barChart"></v-chart>
            </el-col>
          </el-row>
        </el-card>
      </el-tab-pane>
      <el-tab-pane :name="2" label="案例看板">
        <IFrame
          allowtransparency
          class="htmlClass"
          frameborder="0"
          src="http://localhost:11210/public/dashboard/c9a2e5d0-1728-45e1-a104-cc13c188e6fa"
          width="100%"
        />
      </el-tab-pane>
      <el-tab-pane :name="3" label="法条看板">
        <IFrame
          allowtransparency
          class="htmlClass"
          frameborder="0"
          src="http://localhost:11210/public/dashboard/ec934840-aa7f-4764-922c-50856c451a0a"
          width="100%"
        />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<style lang="scss" scoped>
.container {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap; /* 允许项目在必要时换行 */
}

.chart-container {
  flex: 1; /* 平均分配空间 */
  padding: 10px; /* 添加一些间距 */
}

.chart {
  width: 100%;
  height: 200px; /* 你可以根据需要调整高度 */
}

.barChart {
  width: 100%;
  height: 375px; /* 你可以根据需要调整高度 */
}
</style>
