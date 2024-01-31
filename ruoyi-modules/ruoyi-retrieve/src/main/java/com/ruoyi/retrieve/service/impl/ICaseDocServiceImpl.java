package com.ruoyi.retrieve.service.impl;

import cn.easyes.core.biz.EsPageInfo;
import cn.easyes.core.conditions.select.LambdaEsQueryChainWrapper;
import cn.easyes.core.conditions.select.LambdaEsQueryWrapper;
import cn.easyes.core.core.EsWrappers;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.esmapper.CaseDocMapper;
import com.ruoyi.retrieve.service.ICaseDocService;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author fcs
 * @date 2024/1/31 16:09
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Service
public class ICaseDocServiceImpl implements ICaseDocService {

    @Resource
    private CaseDocMapper caseDocMapper;


    /**
     * 插入
     *
     * @param caseDoc 实体
     * @return 插入条数
     */
    @Override
    public Integer insert(CaseDoc caseDoc) {
        return caseDocMapper.insert(caseDoc);
    }

    /**
     * 根据id更新
     *
     * @param caseDoc 实体
     * @return 更新条数
     */
    @Override
    public Integer update(CaseDoc caseDoc) {
        return caseDocMapper.updateById(caseDoc);
    }

    /**
     * 根据id删除
     *
     * @param id id
     * @return 删除条数
     */
    @Override
    public Integer delete(Long id) {
        return caseDocMapper.deleteById(id);
    }

    /**
     * 批量删除
     *
     * @param ids id集合
     * @return 删除条数
     */
    @Override
    public Integer deleteBatch(Long[] ids) {
        return caseDocMapper.deleteBatchIds(Arrays.asList(ids));
    }


    /**
     * 根据id查询
     *
     * @param id id
     * @return 分页数据
     */
    @Override
    public CaseDoc selectById(Long id) {
        return caseDocMapper.selectById(id);
    }

    @Override
    public CaseDoc selectByName(String name) {
        LambdaEsQueryChainWrapper<CaseDoc> lqw = EsWrappers.lambdaChainQuery(caseDocMapper).eq(CaseDoc::getName, name);
        return caseDocMapper.selectOne(lqw);
    }

    /**
     * 根据关键字精确查询
     *
     * @param keyword    关键字
     * @param blurSearch 是否模糊查询
     * @return 分页数据
     */
    @Override
    public CaseDoc selectOne(String keyword, Boolean blurSearch) {
        if (blurSearch) {
            return EsWrappers.lambdaChainQuery(caseDocMapper)
                .match(CaseDoc::getName, keyword)
                .or()
                .match(CaseDoc::getContent, keyword)
                .one();
        }
        return EsWrappers.lambdaChainQuery(caseDocMapper)
            .eq(CaseDoc::getName, keyword)
            .or()
            .match(CaseDoc::getContent, keyword)
            .one();
    }

    /**
     * 根据关键字模糊查询
     *
     * @param keyword    关键字
     * @param blurSearch 是否模糊查询
     * @return 分页数据
     */
    @Override
    public List<CaseDoc> selectList(String keyword, Boolean blurSearch) {
        if (blurSearch) {
            return EsWrappers.lambdaChainQuery(caseDocMapper)
                .match(CaseDoc::getName, keyword)
                .or()
                .match(CaseDoc::getContent, keyword)
                .list();
        }
        return EsWrappers.lambdaChainQuery(caseDocMapper)
            .eq(CaseDoc::getName, keyword)
            .or()
            .match(CaseDoc::getContent, keyword)
            .list();
    }

    /**
     * 根据关键字模糊查询
     *
     * @param keyword   关键字
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    @Override
    public TableDataInfo<CaseDoc> selectPage(String keyword, PageQuery pageQuery) {
        LambdaEsQueryChainWrapper<CaseDoc> lqw = EsWrappers.lambdaChainQuery(caseDocMapper)
            .match(CaseDoc::getName, keyword)
            .or()
            .match(CaseDoc::getContent, keyword)
            .sortByScore(SortOrder.DESC);
        // 物理分页
        EsPageInfo<CaseDoc> page = caseDocMapper.pageQuery(lqw, pageQuery.getPageNum(), pageQuery.getPageSize());
        return TableDataInfo.build(page.getList(), page.getTotal());
    }

    /**
     * 根据关键字模糊查询
     *
     * @param caseDoc   查询条件
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    @Override
    public TableDataInfo<CaseDoc> selectPage(CaseDoc caseDoc, PageQuery pageQuery) {
//        构造查询条件
        LambdaEsQueryWrapper<CaseDoc> lqw = buildQueryWrapper(caseDoc);
        // 物理分页
        EsPageInfo<CaseDoc> page = caseDocMapper.pageQuery(lqw, pageQuery.getPageNum(), pageQuery.getPageSize());
        return TableDataInfo.build(page.getList());
    }

    @Override
    public Integer insertBatch(List<CaseDoc> entityList) {
        return caseDocMapper.insertBatch(entityList);
    }

    /**
     * 构造查询条件
     *
     * @param bo 查询条件
     * @return 查询条件
     */
    private LambdaEsQueryWrapper<CaseDoc> buildQueryWrapper(CaseDoc bo) {
        LambdaEsQueryWrapper<CaseDoc> lqw = EsWrappers.lambdaQuery(CaseDoc.class);
        // 必须指定一种排序规则,且排序字段值必须唯一 此处我选择用id进行排序 实际可根据业务场景自由指定,不推荐用创建时间,因为可能会相同
        lqw.match(StringUtils.isNotEmpty(bo.getName()), CaseDoc::getName, bo.getName())
            .or()
            .match(StringUtils.isNotEmpty(bo.getContent()), CaseDoc::getContent, bo.getContent())
            .eq(StringUtils.isNotEmpty(bo.getCourt()), CaseDoc::getCourt, bo.getCourt())
            .eq(StringUtils.isNotEmpty(bo.getNumber()), CaseDoc::getNumber, bo.getNumber())
            .eq(StringUtils.isNotEmpty(bo.getCause()), CaseDoc::getCause, bo.getCause())
            .eq(StringUtils.isNotEmpty(bo.getType()), CaseDoc::getType, bo.getType())
            .eq(StringUtils.isNotEmpty(bo.getProcess()), CaseDoc::getProcess, bo.getProcess())
            .eq(StringUtils.isNotEmpty(bo.getJudgeDate()), CaseDoc::getJudgeDate, bo.getJudgeDate())
            .eq(StringUtils.isNotEmpty(bo.getPubDate()), CaseDoc::getPubDate, bo.getPubDate())
            .eq(StringUtils.isNotEmpty(bo.getLegalBasis()), CaseDoc::getLegalBasis, bo.getLegalBasis())
            .eq(StringUtils.isNotEmpty(bo.getParty()), CaseDoc::getParty, bo.getParty())
            .sortByScore(SortOrder.DESC);
        return lqw;
    }
}
