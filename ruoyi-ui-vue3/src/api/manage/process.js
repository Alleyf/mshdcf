import request from '@/utils/request'

// 批量清洗司法案例
export function reviseCase(id) {
  return request({
    url: `/process/case/revise/${id}`,
    method: 'get',
  })
}

// 批量挖掘司法案例
export function miningCase(id) {
  return request({
    url: `/process/case/extractOne/${id}`,
    method: 'get',
  })
}

// 批量清洗法条
export function reviseLaw(id) {
  return request({
    url: `/process/law/revise/${id}`,
    method: 'get',
  })
}

// 批量挖掘法条
export function miningLaw(id) {
  return request({
    url: `/process/law/extractOne/${id}`,
    method: 'get',
  })
}

