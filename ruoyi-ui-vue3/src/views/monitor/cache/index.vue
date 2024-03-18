<template>
  <div class="p-2">
    <el-row>
      <el-col :span="24" class="card-box">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; align-items: center;">
              <Icon class="text-2xl" icon="pajamas:monitor"/>
              <span style="margin-left: 8px; vertical-align: middle;">基本信息</span>
            </div>
          </template>

          <div class="el-table el-table--enable-row-hover el-table--medium">
            <table style="width: 100%">
              <tbody>
              <tr>
                <td class="el-table__cell is-leaf">
                  <div class="cell">Redis版本</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.redis_version }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">运行模式</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{
                      cache.info.redis_mode === "standalone" ? "单机" : "集群"
                    }}
                  </div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">端口</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.tcp_port }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">客户端数</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.connected_clients }}</div>
                </td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf">
                  <div class="cell">运行时间(天)</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.uptime_in_days }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">使用内存</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.used_memory_human }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">使用CPU</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{
                      parseFloat(cache.info.used_cpu_user_children).toFixed(2)
                    }}
                  </div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">内存配置</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.maxmemory_human }}</div>
                </td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf">
                  <div class="cell">AOF是否开启</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.aof_enabled === "0" ? "否" : "是" }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">RDB是否成功</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.rdb_last_bgsave_status }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">Key数量</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.dbSize" class="cell">{{ cache.dbSize }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">网络入口/出口</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">
                    {{ cache.info.instantaneous_input_kbps }}kps/{{ cache.info.instantaneous_output_kbps }}kps
                  </div>
                </td>
              </tr>
              <tr>
                <td class="el-table__cell is-leaf">
                  <div class="cell">系统总内存</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.total_system_memory_human }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">内存碎片率</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.mem_fragmentation_ratio }}</div>
                </td>
                <!--                系统类型-->
                <td class="el-table__cell is-leaf">
                  <div class="cell">操作系统</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.os }}</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div class="cell">服务架构</div>
                </td>
                <td class="el-table__cell is-leaf">
                  <div v-if="cache.info" class="cell">{{ cache.info.arch_bits }}位</div>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="card-box">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; align-items: center;">
              <Icon class="text-2xl" icon="svg-spinners:blocks-wave"/>
              <span style="vertical-align: middle;">命令统计</span>
            </div>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <div id="commandstats" ref="commandstats" style="height: 420px"/>
          </div>
        </el-card>
      </el-col>

      <el-col :span="12" class="card-box">
        <el-card shadow="hover">
          <template #header>
            <div style="display: flex; align-items: center;">
              <Icon class="text-2xl" icon="svg-spinners:clock"/>
              <span style="vertical-align: middle;">内存信息</span>
            </div>
          </template>
          <div class="el-table el-table--enable-row-hover el-table--medium">
            <div id="usedmemory" ref="usedmemory" style="height: 420px"/>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script name="Cache" setup>
import {getCache} from '@/api/monitor/cache';
import * as echarts from 'echarts';
import {getCurrentInstance, onBeforeUnmount, onMounted, ref} from "vue";
import {Icon} from '@iconify/vue';


const cache = ref({});
const commandstats = ref();
const usedmemory = ref();
let chartInstances = []; // 用于存储 ECharts 实例的数组

const getList = async () => {
  const {proxy} = getCurrentInstance();
  proxy.$modal.loading("正在加载缓存监控数据，请稍候！");
  try {
    const res = await getCache();
    proxy.$modal.closeLoading();
    cache.value = res.data;
    initEcharts(commandstats, 'commandstats', res.data.commandStats);
    initEcharts(usedmemory, 'usedmemory', res.data.info);
  } catch (error) {
    console.error('Error fetching cache data:', error);
  }
};

const initEcharts = (ref, chartId, seriesData) => {
  const chartInstance = echarts.init(document.getElementById(chartId), "macarons");
  chartInstances.push(chartInstance); // 将实例添加到数组中
  if (chartId === 'commandstats') {
    chartInstance.setOption({
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)',
        backgroundColor: 'rgba(0, 0, 0, 0.75)',
        textStyle: {
          color: '#fff', // 字体颜色设置为白色
          fontSize: 16, // 字体大小设置为14px
        },
        padding: 10, // 提示框的内边距
        borderColor: '#333', // 提示框边框颜色
        borderWidth: 1, // 提示框边框宽度
        shadowBlur: 10, // 提示框阴影的模糊大小
        shadowColor: 'rgba(0, 0, 0, 0.5)' // 提示框阴影颜色
      },
      toolbox: {
        show: true,
        feature: {
          dataView: {show: true, readOnly: false},
          restore: {show: true},
          saveAsImage: {show: true}
        }
      },
      series: [
        {
          name: '命令',
          type: 'pie',
          roseType: 'radius',
          itemStyle: {
            borderRadius: 8,
            borderColor: '#fff', // 扇区边框颜色
            borderWidth: 2 // 扇区边框宽度
          },
          radius: [15, 160],
          center: ['50%', '50%'],
          data: seriesData,
          label: {
            show: true,
            formatter: '{b}: {c} ({d}%)', // 自定义标签内容
            position: 'outside', // 标签位置
            color: '#000', // 标签字体颜色
            fontSize: 16 // 标签字体大小
          },
          animationEasing: 'cubicInOut', // 动画效果
          animationDuration: 1000 // 动画持续时间
        }
      ]
    });
  } else {
    chartInstance.setOption({
      tooltip: {
        formatter: "{b} <br/>{a} : " + seriesData.used_memory_human
      },
      series: [
        {
          name: "峰值",
          type: "gauge",
          min: 0,
          max: 100,
          radius: '90%', // 增加仪表盘的半径
          axisLine: {
            lineStyle: {
              width: 20, // 增加轴线宽度
            }
          },
          splitLine: {
            length: 15, // 增加刻度线的长度
            distance: 30, // 调整刻度线间距
            lineStyle: {
              width: 2, // 刻度线宽度
              color: '#999', // 刻度线颜色
            }
          },
          axisLabel: {
            distance: 20, // 调整标签与轴线的距离
            fontSize: 14, // 标签字体大小
            color: '#333', // 标签颜色
          },
          detail: {
            fontSize: 24, // 详细信息的字体大小
            formatter: seriesData.used_memory_human
          },
          data: [
            {
              value: parseFloat(seriesData.used_memory_human),
              name: "内存消耗"
            }
          ]
        }
      ]
    })

  }
};

onMounted(() => {
  getList();
  window.addEventListener('resize', handleResize); // 添加 resize 事件监听器
});

const handleResize = () => {
  chartInstances.forEach(chartInstance => {
    chartInstance.resize();
  });
};

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize); // 移除 resize 事件监听器
});
</script>
