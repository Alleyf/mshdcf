import request from '@/utils/request'

// 查询法律法规列表
export function listRegulation(query) {
  return request({
    url: '/manage/regulation/list',
    method: 'get',
    params: query
  })
}

// 查询法律法规详细
export function getRegulation(id) {
  return request({
    url: '/manage/regulation/' + id,
    method: 'get'
  })
}

// 新增法律法规
export function addRegulation(data) {
  return request({
    url: '/manage/regulation',
    method: 'post',
    data: data
  })
}

// 修改法律法规
export function updateRegulation(data) {
  return request({
    url: '/manage/regulation',
    method: 'put',
    data: data
  })
}

// 批量智能处理保存司法案例
export function saveProcessRegulation(data) {
  return request({
    url: '/manage/regulation/process',
    method: 'put',
    data: data
  })
}

// 批量清洗保存司法案例
export function reviseRegulation(data) {
  return request({
    url: '/manage/regulation/revise',
    method: 'put',
    data: data
  })
}

// 批量挖掘保存司法案例
export function miningRegulation(data) {
  return request({
    url: '/manage/regulation/mining',
    method: 'put',
    data: data
  })
}

// 删除法律法规
export function delRegulation(id) {
  return request({
    url: '/manage/regulation/' + id,
    method: 'delete'
  })
}
