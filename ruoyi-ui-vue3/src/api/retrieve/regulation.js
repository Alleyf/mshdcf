import request from '@/utils/request'

// 查询法律法规列表
export function listRegulation(query) {
  return request({
    url: '/retrieve/law/list',
    method: 'get',
    params: {
      keyword: query.name,
      blurSearch: true
    }
  })
}


export function pageRegulation(query) {
  return request({
    url: '/retrieve/law/page',
    method: 'get',
    params: query
  })
}

// 查询法律法规详细
export function getRegulation(id) {
  return request({
    url: '/retrieve/law/' + id,
    method: 'get'
  })
}

// 获取司法案例词云
export function getRegulationWorldCloud(id) {
  return request({
    url: '/retrieve/law/worldCloud/' + id,
    method: 'get'
  })
}
