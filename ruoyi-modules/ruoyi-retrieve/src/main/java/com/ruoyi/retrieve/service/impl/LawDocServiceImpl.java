package com.ruoyi.retrieve.service.impl;

import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.biz.SAPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryChainWrapper;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.core.EsWrappers;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.WorldCloudUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.api.domain.LawDoc;
import com.ruoyi.retrieve.api.domain.LawDoc;
import com.ruoyi.retrieve.esmapper.LawDocMapper;
import com.ruoyi.retrieve.esmapper.LawDocMapper;
import com.ruoyi.retrieve.service.ILawDocService;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fcs
 * @date 2024/2/1 12:32
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Service
public class LawDocServiceImpl implements ILawDocService {

    @Resource
    private LawDocMapper lawDocMapper;

    /**
     * 获取词云
     *
     * @param name 名称
     * @param text 内容
     * @return 词云
     */
    public String getWordCloud(String name, String text) {
        return WorldCloudUtils.genWorldCloud(name, text);
    }

    /**
     * 插入
     *
     * @param lawDoc 实体
     * @return 插入条数
     */
    @Override
    public Integer insert(LawDoc lawDoc) {
        //        设置词云
//        String wordCloud = getWordCloud(lawDoc.getName(), lawDoc.getContent());
//        lawDoc.setWordCloud(wordCloud);
        return lawDocMapper.insert(lawDoc);
    }

    /**
     * 根据id更新
     *
     * @param lawDoc 实体
     * @return 更新条数
     */
    @Override
    public Integer update(LawDoc lawDoc) {
        //        设置词云
//        String wordCloud = getWordCloud(lawDoc.getName(), lawDoc.getContent());
//        lawDoc.setWordCloud(wordCloud);
        return lawDocMapper.updateById(lawDoc);
    }

    /**
     * 根据id删除
     *
     * @param id id
     * @return 删除条数
     */
    @Override
    public Integer delete(Long id) {
        return lawDocMapper.deleteById(id);
    }

    /**
     * 批量删除
     *
     * @param ids id集合
     * @return 删除条数
     */
    @Override
    public Integer deleteBatch(Long[] ids) {
        return lawDocMapper.deleteBatchIds(Arrays.asList(ids));
    }


    /**
     * 根据id查询
     *
     * @param id id
     * @return 分页数据
     */
    @Override
    public LawDoc selectById(Long id) {
        return lawDocMapper.selectById(id);
    }

    //    @Override
//    public LawDoc selectByName(String name) {
//        return EsWrappers.lambdaChainQuery(lawDocMapper).eq(StringUtils.isNotEmpty(name), LawDoc::getName, name).one();
//    }
    @Override
    public LawDoc selectByName(String name) {
        // 使用Easy-Es的链式调用来构建查询条件
        LambdaEsQueryWrapper<LawDoc> queryWrapper = new LambdaEsQueryWrapper<>();

        // 添加查询条件，如果name不为空，则按name进行精确匹配
        if (StringUtils.isNotEmpty(name)) {
            queryWrapper.eq(LawDoc::getName, name);
        }

        // 执行查询并返回结果
        return lawDocMapper.selectOne(queryWrapper);
    }

    /**
     * 根据关键字精确查询
     *
     * @param keyword    关键字
     * @param blurSearch 是否模糊查询
     * @return 分页数据
     */
    @Override
    public LawDoc selectOne(String keyword, Boolean blurSearch) {
        if (blurSearch) {
            return fuzzySearchList(keyword).get(0);
        }
        return preciseSearchList(keyword).get(0);
    }

    /**
     * 根据关键字精确查询案例名称
     *
     * @param keyword 关键字
     * @return List<LawDoc>
     */
    public List<LawDoc> preciseSearchList(String keyword) {
        return EsWrappers.lambdaChainQuery(lawDocMapper)
            .eq(StringUtils.isNotEmpty(keyword), LawDoc::getName, keyword)
            .or()
            .match(StringUtils.isNotEmpty(keyword), LawDoc::getContent, keyword)
            .list();
    }

    /**
     * 根据关键字模糊查询案例名称
     *
     * @param keyword 关键字
     * @return List<LawDoc>
     */
    public List<LawDoc> fuzzySearchList(String keyword) {
        return EsWrappers.lambdaChainQuery(lawDocMapper)
            .match(StringUtils.isNotEmpty(keyword), LawDoc::getName, keyword)
            .or()
            .match(StringUtils.isNotEmpty(keyword), LawDoc::getContent, keyword)
            .list();
    }

    /**
     * 根据关键字模糊查询
     *
     * @param keyword    关键字
     * @param blurSearch 是否模糊查询
     * @return 分页数据
     */
    @Override
    public List<LawDoc> selectList(String keyword, Boolean blurSearch) {
        if (ObjectUtil.isNull(blurSearch)) {
            blurSearch = true;
        }
        if (blurSearch) {
            return fuzzySearchList(keyword);
        }
        return preciseSearchList(keyword);
    }

    /**
     * 查询法条列表
     *
     * @return {@link List }<{@link LawDoc }>
     */
    @Override
    public List<LawDoc> selectList() {
        return EsWrappers.lambdaChainQuery(lawDocMapper).select(LawDoc::getId).list();
    }

    /**
     * 根据关键字模糊查询
     *
     * @param keyword   关键字
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    @Override
    public TableDataInfo<LawDoc> selectPage(String keyword, PageQuery pageQuery) {
        LambdaEsQueryChainWrapper<LawDoc> lqw = EsWrappers.lambdaChainQuery(lawDocMapper)
            .match(StringUtils.isNotEmpty(keyword), LawDoc::getName, keyword)
            .or()
            .match(StringUtils.isNotEmpty(keyword), LawDoc::getContent, keyword)
            .sortByScore(SortOrder.DESC);
        // 物理分页
        validatePageQuery(pageQuery);
        int from = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        EsPageInfo<LawDoc> page = lawDocMapper.pageQuery(lqw, from, pageQuery.getPageSize());
        return TableDataInfo.build(page.getList(), page.getTotal());
    }

    private void validatePageQuery(PageQuery pageQuery) {
        if (ObjectUtil.isNull(pageQuery.getPageSize())) {
            pageQuery.setPageSize(PageQuery.DEFAULT_PAGE_SIZE);
        }
        if (ObjectUtil.isNull(pageQuery.getPageNum())) {
            pageQuery.setPageNum(1);
        }
    }

    /**
     * 根据关键字模糊查询
     *
     * @param lawDoc    查询条件
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    @Override
    public TableDataInfo<LawDoc> selectPage(LawDoc lawDoc, PageQuery pageQuery) {
//        构造查询条件
        LambdaEsQueryWrapper<LawDoc> lqw = buildQueryWrapper(lawDoc);
        //       校验分页参数
        validatePageQuery(pageQuery);
        lqw.size(pageQuery.getPageSize());
        // 必须指定一种排序规则,且排序字段值必须唯一 此处我选择用id进行排序 实际可根据业务场景自由指定,不推荐用创建时间,因为可能会相同
        lqw.orderByDesc(LawDoc::getId);
        // 第一页
        SAPageInfo<LawDoc> saPageInfo = lawDocMapper.searchAfterPage(lqw, null, pageQuery.getPageSize());
        List<LawDoc> result = saPageInfo.getList();
//        去除unicode异常字符
//        result = result.stream().peek(doc -> doc.setContent(doc.getContent().replaceAll("\\\\u\\e[0-9a-fA-F]{3}", "|\n"))).collect(Collectors.toList());
        long total = saPageInfo.getTotal();
        for (int i = 0; i < pageQuery.getPageNum() - 1; i++) {
            // 获取下一页
            List<Object> nextSearchAfter = saPageInfo.getNextSearchAfter();
            SAPageInfo<LawDoc> next = lawDocMapper.searchAfterPage(lqw, nextSearchAfter, pageQuery.getPageSize());
            result = next.getList();
            total = next.getTotal();
        }
        return TableDataInfo.build(result, total);
    }

    @Override
    public Integer insertBatch(List<LawDoc> entityList) {
        if (ObjectUtil.isEmpty(entityList)) {
            return 0;
        }
        return lawDocMapper.insertBatch(entityList);
    }

    /**
     * 构造查询条件
     *
     * @param bo 查询条件
     * @return 查询条件
     */
    private LambdaEsQueryWrapper<LawDoc> buildQueryWrapper(LawDoc bo) {
        LambdaEsQueryWrapper<LawDoc> lqw = EsWrappers.lambdaQuery(LawDoc.class);
        // 必须指定一种排序规则,且排序字段值必须唯一 此处我选择用id进行排序 实际可根据业务场景自由指定,不推荐用创建时间,因为可能会相同
        lqw.match(StringUtils.isNotEmpty(bo.getName()), LawDoc::getName, bo.getName())
            .or()
            .match(LawDoc::getContent, StringUtils.isNotEmpty(bo.getContent()) ? bo.getContent() : bo.getName())
            .or()
            .match(LawDoc::getStripContent, StringUtils.isNotEmpty(bo.getContent()) ? bo.getContent() : bo.getName())
            .like(StringUtils.isNotEmpty(bo.getField()), LawDoc::getField, bo.getField())
            .eq(StringUtils.isNotEmpty(bo.getReviseNum()), LawDoc::getReviseNum, bo.getReviseNum())
            .eq(StringUtils.isNotEmpty(bo.getType()), LawDoc::getType, bo.getType())
            .eq(ObjectUtil.isNotEmpty(bo.getSourceId()), LawDoc::getSourceId, bo.getSourceId())
            .eq(ObjectUtil.isNotEmpty(bo.getIsValidity()), LawDoc::getIsValidity, bo.getIsValidity())
            .like(StringUtils.isNotEmpty(bo.getStructure()), LawDoc::getStructure, bo.toString())
            .ge(ObjectUtil.isNotNull(bo.getReleaseDate()), LawDoc::getReleaseDate, bo.getReleaseDate())
            .le(ObjectUtil.isNotNull(bo.getExecuteDate()), LawDoc::getExecuteDate, bo.getExecuteDate())
            .like(StringUtils.isNotEmpty(bo.getReleaseOrganization()), LawDoc::getReleaseOrganization, bo.getReleaseOrganization())
//            .notSelect(LawDoc::getHighlightName, LawDoc::getHighlightContent, LawDoc::getHighlightStripContent, LawDoc::getMysqlId, LawDoc::getExtra)
            .notSelect(LawDoc::getHighlightStripContent, LawDoc::getMysqlId, LawDoc::getExtra)
            .sortByScore(SortOrder.DESC);
        return lqw;
    }
}
