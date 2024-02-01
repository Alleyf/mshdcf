import request from '@/utils/request'

// 查询爬虫数据源列表
export function listSource(query) {
  return request({
    url: '/crawler/source/list',
    method: 'get',
    params: query
  })
}

// 查询爬虫数据源详细
export function getSource(id) {
  return request({
    url: '/crawler/source/' + id,
    method: 'get'
  })
}

// 新增爬虫数据源
export function addSource(data) {
  return request({
    url: '/crawler/source',
    method: 'post',
    data: data
  })
}

// 修改爬虫数据源
export function updateSource(data) {
  return request({
    url: '/crawler/source',
    method: 'put',
    data: data
  })
}

// 删除爬虫数据源
export function delSource(id) {
  return request({
    url: '/crawler/source/' + id,
    method: 'delete'
  })
}
