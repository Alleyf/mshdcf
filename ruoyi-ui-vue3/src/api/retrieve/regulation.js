import request from '@/utils/request'

// 查询法律法规列表
export function listRegulation(query) {
  return request({
    url: '/retrieve/regulation/list',
    method: 'get',
    params: query
  })
}

// 查询法律法规详细
export function getRegulation(id) {
  return request({
    url: '/retrieve/regulation/' + id,
    method: 'get'
  })
}

// 新增法律法规
export function addRegulation(data) {
  return request({
    url: '/retrieve/regulation',
    method: 'post',
    data: data
  })
}

// 修改法律法规
export function updateRegulation(data) {
  return request({
    url: '/retrieve/regulation',
    method: 'put',
    data: data
  })
}

// 删除法律法规
export function delRegulation(id) {
  return request({
    url: '/retrieve/regulation/' + id,
    method: 'delete'
  })
}
