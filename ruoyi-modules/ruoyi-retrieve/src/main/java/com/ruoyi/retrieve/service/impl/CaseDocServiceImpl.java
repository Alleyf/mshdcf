package com.ruoyi.retrieve.service.impl;

import org.dromara.easyes.core.biz.EsPageInfo;
import org.dromara.easyes.core.biz.SAPageInfo;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryChainWrapper;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.dromara.easyes.core.core.EsWrappers;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.WorldCloudUtils;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.retrieve.api.domain.CaseDoc;
import com.ruoyi.retrieve.esmapper.CaseDocMapper;
import com.ruoyi.retrieve.service.ICaseDocService;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fcs
 * @date 2024/1/31 16:09
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Service
@Slf4j
public class CaseDocServiceImpl implements ICaseDocService {

    @Resource
    private CaseDocMapper caseDocMapper;

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
     * @param caseDoc 实体
     * @return 插入条数
     */
    @Override
    public Integer insert(CaseDoc caseDoc) {
//        设置词云
//        String wordCloud = getWordCloud(caseDoc.getName(), caseDoc.getContent());
//        caseDoc.setWordCloud(wordCloud);
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
        // TODO: 2024/2/2 更新和新建司法案例和法律法规均报错？
        //        设置词云
//        String wordCloud = getWordCloud(caseDoc.getName(), caseDoc.getContent());
//        caseDoc.setWordCloud(wordCloud);
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
//        System.out.println("id=" + id);
//        LambdaEsQueryChainWrapper<CaseDoc> lqw = EsWrappers.lambdaChainQuery(caseDocMapper).eq(ObjectUtil.isNotNull(id), CaseDoc::getMysqlId, id);
//        return caseDocMapper.selectOne(lqw);
        return caseDocMapper.selectById(id);
    }

    @Override
    public CaseDoc selectByName(String name) {
        LambdaEsQueryChainWrapper<CaseDoc> lqw = EsWrappers.lambdaChainQuery(caseDocMapper).eq(StringUtils.isNotEmpty(name), CaseDoc::getName, name);
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
            return fuzzySearchList(keyword).get(0);
        }
        return preciseSearchList(keyword).get(0);
    }

    /**
     * 根据关键字精确查询案例名称
     *
     * @param keyword 关键字
     * @return List<CaseDoc>
     */
    public List<CaseDoc> preciseSearchList(String keyword) {
        return EsWrappers.lambdaChainQuery(caseDocMapper)
            .eq(StringUtils.isNotEmpty(keyword), CaseDoc::getName, keyword)
            .or()
            .match(StringUtils.isNotEmpty(keyword), CaseDoc::getContent, keyword)
            .list();
    }

    /**
     * 根据关键字模糊查询案例名称
     *
     * @param keyword 关键字
     * @return List<CaseDoc>
     */
    public List<CaseDoc> fuzzySearchList(String keyword) {
        return EsWrappers.lambdaChainQuery(caseDocMapper)
            .match(StringUtils.isNotEmpty(keyword), CaseDoc::getName, keyword)
            .or()
            .match(StringUtils.isNotEmpty(keyword), CaseDoc::getContent, keyword)
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
    public List<CaseDoc> selectList(String keyword, Boolean blurSearch) {
        System.out.println(keyword);
        if (ObjectUtil.isNull(blurSearch)) {
            blurSearch = true;
        }
        if (blurSearch) {
            return fuzzySearchList(keyword);
        }
        return preciseSearchList(keyword);
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
        System.out.println("keyword:" + keyword + " pageQuery:" + pageQuery);
//        keyword不能为null
        LambdaEsQueryChainWrapper<CaseDoc> lqw = EsWrappers.lambdaChainQuery(caseDocMapper)
            .match(StringUtils.isNotEmpty(keyword), CaseDoc::getName, keyword)
            .or()
            .match(StringUtils.isNotEmpty(keyword), CaseDoc::getContent, keyword)
            .sortByScore(SortOrder.DESC);
        // 物理分页
        validatePageQuery(pageQuery);
        int from = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        EsPageInfo<CaseDoc> page = caseDocMapper.pageQuery(lqw, from, pageQuery.getPageSize());
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
     * @param caseDoc   查询条件
     * @param pageQuery 分页参数
     * @return 分页数据
     */
    @Override
    public TableDataInfo<CaseDoc> selectPage(CaseDoc caseDoc, PageQuery pageQuery) {
//        构造查询条件
        LambdaEsQueryWrapper<CaseDoc> lqw = buildQueryWrapper(caseDoc);
//        校验分页参数
        validatePageQuery(pageQuery);
//        // 方法1：物理分页
//        int from = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
//        EsPageInfo<CaseDoc> page = caseDocMapper.pageQuery(lqw, from, pageQuery.getPageSize());
////        去除unicode异常字符
//        List<CaseDoc> collect = page.getList().stream().peek(doc -> doc.setContent(doc.getContent().replaceAll("\\\\u\\S{4}", ""))).collect(Collectors.toList());
//        return TableDataInfo.build(collect);

//        方法2：search_after
        lqw.size(pageQuery.getPageSize());
        // 必须指定一种排序规则,且排序字段值必须唯一 此处我选择用id进行排序 实际可根据业务场景自由指定,不推荐用创建时间,因为可能会相同
        lqw.orderByDesc(CaseDoc::getId);
        // 第一页
        SAPageInfo<CaseDoc> saPageInfo = caseDocMapper.searchAfterPage(lqw, null, pageQuery.getPageSize());
        List<CaseDoc> result = saPageInfo.getList();
//        去除unicode异常字符
        result = result.stream().peek(doc -> doc.setContent(doc.getContent().replaceAll("\\\\ue[0-9a-fA-F]{3}", "|\n"))).collect(Collectors.toList());
        long total = saPageInfo.getTotal();
        for (int i = 0; i < pageQuery.getPageNum() - 1; i++) {
            // 获取下一页
            List<Object> nextSearchAfter = saPageInfo.getNextSearchAfter();
            SAPageInfo<CaseDoc> next = caseDocMapper.searchAfterPage(lqw, nextSearchAfter, pageQuery.getPageSize());
            result = next.getList();
            total = next.getTotal();
        }
        return TableDataInfo.build(result, total);
    }

    /**
     * 批量插入
     *
     * @param entityList 实体集合
     * @return 结果
     */
    @Override
    public Integer insertBatch(List<CaseDoc> entityList) {
//        log.info("批量新增rpc索引案例：{}", entityList);
        if (ObjectUtil.isEmpty(entityList)) {
            return 0;
        }
//        for (int i = 0; i < entityList.size(); i++) {
//            String wordCloud = getWordCloud(entityList.get(i).getName(), entityList.get(i).getContent());
//            entityList.get(i).setWordCloud(wordCloud);
//        }
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
            .match(CaseDoc::getContent, StringUtils.isNotEmpty(bo.getContent()) ? bo.getContent() : bo.getName())
            .or()
            .match(CaseDoc::getStripContent, StringUtils.isNotEmpty(bo.getContent()) ? bo.getContent() : bo.getName())
            .like(StringUtils.isNotEmpty(bo.getCourt()), CaseDoc::getCourt, bo.getCourt())
            .like(StringUtils.isNotEmpty(bo.getNumber()), CaseDoc::getNumber, bo.getNumber())
            .eq(StringUtils.isNotEmpty(bo.getCause()), CaseDoc::getCause, bo.getCause())
            .eq(StringUtils.isNotEmpty(bo.getType()), CaseDoc::getType, bo.getType())
            .like(StringUtils.isNotEmpty(bo.getProcess()), CaseDoc::getProcess, bo.getProcess())
            .ge(StringUtils.isNotEmpty(bo.getJudgeDate()), CaseDoc::getJudgeDate, bo.getJudgeDate())
            .le(StringUtils.isNotEmpty(bo.getPubDate()), CaseDoc::getPubDate, bo.getPubDate())
            .like(StringUtils.isNotEmpty(bo.getLegalBasis()), CaseDoc::getLegalBasis, bo.getLegalBasis())
            .like(StringUtils.isNotEmpty(bo.getParty()), CaseDoc::getParty, bo.getParty())
            .sortByScore(SortOrder.DESC);
        return lqw;
    }
}
