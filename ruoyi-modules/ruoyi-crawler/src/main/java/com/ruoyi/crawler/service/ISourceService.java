package com.ruoyi.crawler.service;

import com.ruoyi.crawler.domain.Source;
import com.ruoyi.crawler.domain.vo.SourceVo;
import com.ruoyi.crawler.domain.bo.SourceBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;

import java.util.Collection;
import java.util.List;

/**
 * 爬虫数据源Service接口
 *
 * @author alleyf
 * @date 2024-01-26
 */
public interface ISourceService {

    /**
     * 查询爬虫数据源
     */
    SourceVo queryById(Long id);

    /**
     * 查询爬虫数据源列表
     */
    TableDataInfo<SourceVo> queryPageList(SourceBo bo, PageQuery pageQuery);

    /**
     * 查询爬虫数据源列表
     */
    List<SourceVo> queryList(SourceBo bo);

    /**
     * 修改爬虫数据源
     */
    Boolean insertByBo(SourceBo bo);

    /**
     * 修改爬虫数据源
     */
    Boolean updateByBo(SourceBo bo);

    /**
     * 校验并批量删除爬虫数据源信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
