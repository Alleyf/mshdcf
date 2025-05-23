package com.ruoyi.retrieve.controller;

import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.web.controller.BaseController;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.common.redis.utils.RedisUtils;
import com.ruoyi.retrieve.api.domain.LawDoc;
import com.ruoyi.retrieve.esmapper.LawDocMapper;
import com.ruoyi.retrieve.mq.producer.WorldCloudProducer;
import com.ruoyi.retrieve.service.ILawDocService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 法律法规检索
 *
 * @author fcs
 * @date 2024/2/1 12:50
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@RestController
@RequestMapping("/law")
@Validated
@Slf4j
@AllArgsConstructor
public class LawDocController extends BaseController {
    private final ILawDocService lawDocService;
    private final WorldCloudProducer worldCloudProducer;
    private final LawDocMapper lawDocMapper;

    /**
     * 创建索引
     */
    @GetMapping("/createIndex")
    public R<Void> createIndex() {
        // 1.初始化-> 创建索引(相当于mysql中的表)
        try {
            GetIndexResponse index = lawDocMapper.getIndex();
            if (ObjectUtil.isNotNull(index)) {
                return R.ok("索引已存在");
            }
        } catch (Exception e) {
            return toAjax(lawDocMapper.createIndex());
        }
        return toAjax(lawDocMapper.createIndex());
    }

    /**
     * 删除索引
     *
     * @return R
     */
    @DeleteMapping("/deleteIndex")
    public R<Void> deleteIndex() {
        // 指定要删除哪个索引
        String indexName = LawDoc.class.getSimpleName().toLowerCase();
        return toAjax(lawDocMapper.deleteIndex(indexName));
    }


    /**
     * 根据id查询
     *
     * @param id id
     * @return R
     */
    @GetMapping("/{id}")
    public R<Object> get(@PathVariable("id") Long id) {
        LawDoc lawDoc = lawDocService.selectById(id);
        if (lawDoc != null) {
            worldCloudProducer.sendMsg(lawDoc.getId(), lawDoc.getName(), lawDoc.getContent(), 0L);
        }
        return R.ok(lawDoc);
    }

    /**
     * 根据name查询
     *
     * @param name name
     * @return R
     */
    @GetMapping("/name/{name}")
    public R<Object> get(@PathVariable("name") String name) {
        LawDoc lawDoc = lawDocService.selectByName(name);
        if (lawDoc != null) {
            worldCloudProducer.sendMsg(lawDoc.getId(), lawDoc.getName(), lawDoc.getContent(), 0L);
        }
        return R.ok(lawDoc);
    }

    /**
     * 根据id查询法条词云
     *
     * @param id id
     * @return R
     */
    @GetMapping("/worldCloud/{id}")
    public R<String> worldCloud(@PathVariable("id") Long id) {
        String worldCloud;
        try {
            worldCloud = RedisUtils.getCacheObject(CacheConstants.REDIS_KEY_PREFIX + id);
        } catch (Exception e) {
            throw new ServiceException("该词云不存在");
        }
        return R.ok(worldCloud);
    }

    /**
     * 查询列表
     *
     * @param keyword    关键词
     * @param blurSearch 模糊搜索标志
     * @return R
     */
    @GetMapping("/list")
    public R<List<LawDoc>> list(String keyword, Boolean blurSearch) {
        return R.ok(lawDocService.selectList(keyword, blurSearch));
    }

    /**
     * 分页查询
     *
     * @param keyword   关键词
     * @param pageQuery 分页参数
     * @return TableDataInfo
     */
    @GetMapping("/pageByKeyword")
    public TableDataInfo<LawDoc> page(String keyword, PageQuery pageQuery) {
        return lawDocService.selectPage(keyword, pageQuery);
    }

    /**
     * 分页查询
     *
     * @param lawDoc    查询条件
     * @param pageQuery 分页参数
     * @return TableDataInfo
     */
    @GetMapping("/page")
    public TableDataInfo<LawDoc> page(LawDoc lawDoc, PageQuery pageQuery) {
        return lawDocService.selectPage(lawDoc, pageQuery);
    }

    /**
     * 新增
     *
     * @param lawDoc 新增对象
     * @return R
     */
    @PostMapping
    public R<Void> insert(@Validated @RequestBody LawDoc lawDoc) {
        return toAjax(lawDocService.insert(lawDoc));
    }

    /**
     * 修改
     *
     * @param lawDoc 修改对象
     * @return R
     */
    @PutMapping
    public R<Void> edit(@Validated @RequestBody LawDoc lawDoc) {
        return toAjax(lawDocService.update(lawDoc));
    }

    /**
     * 删除
     *
     * @param id 主键
     * @return R
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id) {
        return toAjax(lawDocService.delete(id));
    }

    /**
     * 批量删除
     *
     * @param ids 主键
     * @return R
     */
    @DeleteMapping("/batch/{ids}")
    public R<Void> deleteBatch(@PathVariable Long[] ids) {
        return toAjax(lawDocService.deleteBatch(ids));
    }


}
