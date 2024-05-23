import request from '@/utils/request'


// 查询司法案件详细
export function getCase(id) {
  return request({
    url: '/retrieve/case/' + id,
    method: 'get'
  })
}

// 获取司法案件词云
export function getCaseWorldCloud(id) {
  return request({
    url: '/retrieve/case/worldCloud/' + id,
    method: 'get'
  })
}

// 查询司法案件列表
export function listCase(query) {
  return request({
    url: '/retrieve/case/list',
    method: 'get',
    params: {
      keyword: query.name,
      blurSearch: true
    }

  })
}

export function pageCase(query) {
  return request({
    url: '/retrieve/case/page',
    method: 'get',
    params: query
  })
}



