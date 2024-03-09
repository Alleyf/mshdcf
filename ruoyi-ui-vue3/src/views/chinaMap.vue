<template>
  <!-- 中国地图 省级 一级页面 -->
  <div ref="mapContainer" class="m-5 mx-auto w-1 map">
  </div>
</template>

<script setup>
import {onMounted, ref, defineProps} from "vue";
import {countProvinceCase} from "@/api/manage/statisticAnalyse";
import {useRouter} from "vue-router";


import 'echarts/lib/chart/map'; // 引入地图图表类型
import * as echarts from 'echarts';
import chinaMap from '@/assets/json/china.json'

const router = useRouter();
const mapContainer = ref(null);
const data = ref([]);
const {title} = defineProps(["title"]);


const allProvinces = [
  "山东", "福建", "上海", "甘肃", "浙江", "广西", "重庆", "湖南", "江西", "河北", "河南", "西藏",
  "云南", "黑龙江", "吉林", "辽宁", "广东", "青海", "陕西", "新疆", "江苏", "北京", "重庆", "南海诸岛",
  "安徽", "湖北", "海南", "四川", "台湾", "天津", "香港", "澳门", "山西", "内蒙古", "宁夏", "贵州",
];
const option = ref({
  title: {
    top: 8,
    right: 8,
    text: title,
    left: 'center',
    textStyle: {
      color: '#262222',
    }
  },
  tooltip: {
    trigger: "item",
    formatter: "{a} <br/>{b} : {c} "
  },
  toolbox: {
    showTitle: true,
    show: true,
    left: 'left',
    top: 'top',
    feature: {
      dataView: {readOnly: true},
      restore: {},
      saveAsImage: {}

    },
  },
  backgroundColor: '#ffffff',
  visualMap: {
    type: 'piecewise',
    pieces: [{
      max: 30,
      label: '较少',
      color: '#2c9a42'
    },
      {
        min: 30,
        max: 60,
        label: '正常',
        color: '#d08a00'
      },
      {
        min: 60,
        label: '较多',
        color: '#c23c33'
      },
    ],
    color: '#262222',
    textStyle: {
      color: '#262222',
    },
    visibility: 'off'
  },
  geo: {
    map: 'china',
    aspectScale: 0.75,
    layoutCenter: ["50%", "65%"], //地图位置
    layoutSize: '118%',
    roam: true,
    emphasis: {
      areaColor: '#2a333d'
    },
    itemStyle: {
      borderWidth: 2, // 设置边框宽度
    },
    regions: [{
      name: '南海诸岛',
      itemStyle: {
        areaColor: 'rgba(0, 10, 52, 1)',
        borderColor: 'rgba(0, 10, 52, 1)'
      },
      emphasis: {
        areaColor: 'rgba(0, 10, 52, 1)',
        borderColor: 'rgba(0, 10, 52, 1)'
      }
    }],
    z: 2
  },
  series: [
    {
      name: "案例数量",
      type: 'map',
      map: 'china',
      tooltip: {
        show: true
      },
      label: {
        show: true,
        color: '#FFFFFF',
        fontSize: 16
      },
      aspectScale: 0.75,
      layoutCenter: ["50%", "65%"], //地图位置
      layoutSize: '118%',
      roam: true,
      emphasis: {areaColor: 'rgba(147, 235, 248, 50)'},   //鼠标滑过高亮},
      itemStyle: {
        borderColor: 'rgba(147, 235, 248, 0.6)',
        borderWidth: 0.8,
        areaColor: {
          type: 'linear-gradient',
          x: 0,
          y: 1200,
          x2: 1000,
          y2: 0,
          colorStops: [{
            offset: 0,
            color: '#009DA1' // 0% 处的颜色
          }, {
            offset: 1,
            color: '#005B9E' // 50% 处的颜色
          }],
          global: true // 缺省为 false
        },
      },
      zlevel: 2,
      data: data.value
    },
  ]
});


const getProvinceMap = () => {
  echarts.registerMap('china', {geoJSON: chinaMap})
  var myChart = echarts.init(mapContainer.value, 'white');
  countProvinceCase().then((res) => {
    allProvinces.map((province) => (
        data.value.push({
          name: province,
          value: res.data[province] || 0
        })
    ))
    myChart.setOption(option.value);
    // console.log(data, option.value.series[0].data)
    //echarts 设置地图外边框以及多个geo实现缩放拖曳同步
    myChart.on('georoam', function (params) {
      var option = myChart.getOption(); //获得option对象
      if (params.zoom != null) { //捕捉到缩放时
        option.geo[0].zoom = option.series[0].zoom; //下层geo的缩放等级跟着上层的geo一起改变
        option.geo[0].center = option.series[0].center; //下层的geo的中心位置随着上层geo一起改变
      } else { //捕捉到拖曳时
        option.geo[0].center = option.series[0].center; //下层的geo的中心位置随着上层geo一起改变
      }
      myChart.setOption(option); //设置option
    });
  });

}

onMounted(() => {
  getProvinceMap();

});
</script>

<style lang="scss" scoped>
.map {
  width: 100%;
  height: 375px;
}
</style>
