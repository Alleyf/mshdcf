import request from '@/utils/request'

// 查询司法案例列表
export function listCase(query) {
  return request({
    url: '/retrieve/case/list',
    method: 'get',
    params: query
  })
}

// 查询司法案例详细
export function getCase(id) {
  return request({
    url: '/retrieve/case/' + id,
    method: 'get'
  })
}

// 新增司法案例
export function addCase(data) {
  return request({
    url: '/retrieve/case',
    method: 'post',
    data: data
  })
}

// 修改司法案例
export function updateCase(data) {
  return request({
    url: '/retrieve/case',
    method: 'put',
    data: data
  })
}

// 删除司法案例
export function delCase(id) {
  return request({
    url: '/retrieve/case/' + id,
    method: 'delete'
  })
}
