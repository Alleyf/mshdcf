import request from '@/utils/request'

// 查询司法案例列表
export function listCase(query) {
  return request({
    url: '/manage/case/list',
    method: 'get',
    params: query
  })
}

// 查询司法案例详细
export function getCase(id) {
  return request({
    url: '/manage/case/' + id,
    method: 'get'
  })
}

// 新增司法案例
export function addCase(data) {
  return request({
    url: '/manage/case',
    method: 'post',
    data: data
  })
}

// 修改司法案例
export function updateCase(data) {
  return request({
    url: '/manage/case',
    method: 'put',
    data: data
  })
}

// 批量智能处理保存司法案例
export function saveProcessCase(data) {
  return request({
    url: '/manage/case/process',
    method: 'put',
    data: data
  })
}

// 批量清洗保存司法案例
export function saveReviseCase(data) {
  return request({
    url: '/manage/case/revise',
    method: 'put',
    data: data
  })
}

// 批量挖掘保存司法案例
export function saveMiningCase(data) {
  return request({
    url: '/manage/case/mining',
    method: 'put',
    data: data
  })
}

// 删除司法案例
export function delCase(id) {
  return request({
    url: '/manage/case/' + id,
    method: 'delete'
  })
}
