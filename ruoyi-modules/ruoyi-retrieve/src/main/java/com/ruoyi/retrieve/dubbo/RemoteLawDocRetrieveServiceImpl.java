package com.ruoyi.retrieve.dubbo;

import com.ruoyi.retrieve.api.RemoteLawDocRetrieveService;
import com.ruoyi.retrieve.api.domain.LawDoc;
import com.ruoyi.retrieve.service.ILawDocService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fcs
 * @date 2024/2/1 11:22
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 法律法规dubbo服务实现类
 */
@Service
@DubboService(version = "1.0", group = "law")
@RequiredArgsConstructor
public class RemoteLawDocRetrieveServiceImpl implements RemoteLawDocRetrieveService {

    private final ILawDocService lawDocService;

    @Override
    public List<LawDoc> selectList() {
        return lawDocService.selectList();
    }

    /**
     * 添加es文档
     *
     * @param lawDoc es文档
     * @return 添加个数
     */
    @Override
    public Integer insert(LawDoc lawDoc) {
        return lawDocService.insert(lawDoc);
    }


    /**
     * 批量添加es文档
     *
     * @param entityList es文档
     * @return 添加个数
     */
    @Override
    public Integer insertBatch(List<LawDoc> entityList) {
        return lawDocService.insertBatch(entityList);
    }

    /**
     * 更新es文档
     *
     * @param lawDoc es文档
     * @return 更新个数
     */
    @Override
    public Integer update(LawDoc lawDoc) {
        return lawDocService.update(lawDoc);
    }

    /**
     * 删除es文档
     *
     * @param id es文档id
     * @return 删除个数
     */
    @Override
    public Integer delete(Long id) {
        return lawDocService.delete(id);
    }

    /**
     * 批量删除es文档
     *
     * @param ids es文档id
     * @return 删除个数
     */
    @Override
    public Integer deleteBatch(Long[] ids) {
        return lawDocService.deleteBatch(ids);
    }

    /**
     * 判断es文档是否存在
     *
     * @param id es文档id
     * @return 是否存在
     */
    @Override
    public Boolean exist(Long id) {
        return lawDocService.selectById(id) != null;
    }

    /**
     * 判断es文档是否存在
     *
     * @param name es文档名称
     * @return 是否存在
     */
    @Override
    public Boolean exist(String name) {
        return lawDocService.selectByName(name) != null;
    }
}
