import request from '@/utils/request'

// 查询在线用户列表
export function getCache() {
  return request({
    url: '/system/monitor/cache/info',
    method: 'get'
  })
}
