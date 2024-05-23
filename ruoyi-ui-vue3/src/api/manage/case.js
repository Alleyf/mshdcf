import request from '@/utils/request'

// 查询司法案件列表
export function listCase(query) {
  return request({
    url: '/manage/case/list',
    method: 'get',
    params: query
  })
}

// 查询司法案件详细
export function getCase(id) {
  return request({
    url: '/manage/case/' + id,
    method: 'get'
  })
}

// 新增司法案件
export function addCase(data) {
  return request({
    url: '/manage/case',
    method: 'post',
    data: data
  })
}

// 全量同步司法案件
export function syncAllCase(data) {
  return request({
    url: '/manage/case/syncAll',
    method: 'get'
  })
}

// 增量同步司法案件
export function syncIncCase(data) {
  return request({
    url: '/manage/case/syncInc',
    method: 'get'
  })
}

// 修改司法案件
export function updateCase(data) {
  return request({
    url: '/manage/case',
    method: 'put',
    data: data
  })
}

// 批量智能处理保存司法案件
export function saveProcessCase(data) {
  return request({
    url: '/manage/case/process',
    method: 'put',
    data: data
  })
}

// 批量清洗保存司法案件
export function saveReviseCase(data) {
  return request({
    url: '/manage/case/revise',
    method: 'put',
    data: data
  })
}

// 批量挖掘保存司法案件
export function saveMiningCase(data) {
  return request({
    url: '/manage/case/mining',
    method: 'put',
    data: data
  })
}

// 删除司法案件
export function delCase(id) {
  return request({
    url: '/manage/case/' + id,
    method: 'delete'
  })
}
