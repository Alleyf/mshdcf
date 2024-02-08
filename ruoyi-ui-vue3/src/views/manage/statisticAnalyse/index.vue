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
import VChart, {THEME_KEY} from "vue-echarts";
import {ref, provide, onMounted, watch} from "vue";
import {countCauseCase, countProcessCase, countTypeCase, countTypeLaw} from "@/api/manage/statisticAnalyse";
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

const caseTypePie = ref({
  title: {
    text: "司法案件类型统计",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: "vertical",
    left: "left",
    data: []
  },
  series: [
    {
      name: "案例类型",
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
const caseProcessPie = ref({
  title: {
    text: "案例审理进程统计",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
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
  title: {
    text: "法条类型统计",
    left: "center"
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} ({d}%)"
  },
  legend: {
    orient: "vertical",
    left: "right",
    data: []
  },
  series: [
    {
      name: "法条类型",
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


const caseTypeRef = ref(null);
const caseProcessRef = ref(null);
const caseCauseRef = ref(null);
const lawTypeRef = ref(null);
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


// 渲染完毕生命周期函数
onMounted(() => {
  initTypeCasePie()
  initTypeLawPie()
  initProcessCasePie()
  initCauseCasePie()
})


</script>

<template>
  <div class="mx-auto">
    <!--    <el-tabs class="mx-auto w-full" lazy="false" tab-position="left">-->
    <!--    <h2 class="text-center font-bold text-2xl">司法案例信息统计</h2>-->
    <el-card class="m-2">
      <el-row :gutter="10" class="m-5 mx-auto flex justify-evenly items-center">
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
    </el-card>

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
</style>
