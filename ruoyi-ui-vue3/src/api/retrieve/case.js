import request from '@/utils/request'


// 查询司法案例详细
export function getCase(id) {
  return request({
    url: '/retrieve/case/' + id,
    method: 'get'
  })
}

// 查询司法案例列表
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



