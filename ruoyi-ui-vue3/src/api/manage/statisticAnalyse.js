import request from '@/utils/request'

// 查询司法案件列表
export function totalCase() {
  return request({
    url: '/statistic/case/total',
    method: 'get',
  })
}

export function totalLaw() {
  return request({
    url: '/statistic/law/total',
    method: 'get',
  })
}

export function incrementCase() {
  return request({
    url: '/statistic/case/increment',
    method: 'get',
  })
}

export function incrementLaw() {
  return request({
    url: '/statistic/law/increment',
    method: 'get',
  })
}

export function countProvinceCase() {
  return request({
    url: '/statistic/case/count/province',
    method: 'get',
  })
}

export function countTypeCase() {
  return request({
    url: '/statistic/case/count/type',
    method: 'get',
  })
}

export function countProcessCase() {
  return request({
    url: '/statistic/case/count/process',
    method: 'get',
  })
}

export function countCauseCase() {
  return request({
    url: '/statistic/case/count/cause',
    method: 'get',
  })
}

export function countTypeLaw() {
  return request({
    url: '/statistic/law/count/type',
    method: 'get',
  })
}

/**
 * 获取前十的案件
 * @returns {*}
 */
export function rankTenCases() {
  return request({
    url: '/statistic/case/rankTen',
    method: 'get',
  })
}

/**
 * 获取前十的法条
 * @returns {*}
 */
export function rankTenLaws() {
  return request({
    url: '/statistic/law/rankTen',
    method: 'get',
  })
}


