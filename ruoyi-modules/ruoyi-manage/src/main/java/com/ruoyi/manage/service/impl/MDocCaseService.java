package com.ruoyi.manage.service.impl;

import com.ruoyi.manage.domain.mo.MDocCase;
import com.ruoyi.manage.repository.MDocCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * 提供文档案例管理服务的实现类。
 */
@Service
@RequiredArgsConstructor
public class MDocCaseService {

    // 注入文档案例仓库，用于数据操作
    private final MDocCaseRepository mdocCaseRepository;

    /**
     * 根据用户名获取文档案例。
     *
     * @param username 用户名
     * @return 返回匹配的文档案例对象，如果没有找到返回null。
     */
    public MDocCase getDocCaseByName(String username) {
        return mdocCaseRepository.findByName(username);
    }

    public List<MDocCase> getDocCaseList() {
        return mdocCaseRepository.findAll();
    }

    /**
     * 保存或更新文档案例。
     *
     * @param mdocCase 文档案例对象
     * @return 返回保存或更新后的文档案例对象。
     */
    public MDocCase saveDocCase(MDocCase mdocCase) {
        
        return mdocCaseRepository.save(mdocCase);
    }

    /**
     * 根据ID获取文档案例。
     *
     * @param id 文档案例ID
     * @return 返回匹配的文档案例对象，如果没有找到抛出异常。
     */
    public MDocCase getDocCaseById(Long id) {
        return mdocCaseRepository.findById(id).get();
    }

    /**
     * 根据ID删除文档案例。
     *
     * @param id 文档案例ID
     */
    public void deleteDocCase(Long id) {
        mdocCaseRepository.deleteById(id);
    }

    /**
     * 批量删除方法
     *
     * @param ids 需要删除的实体的ID数组。这是一个Long类型的数组，其中的每个元素代表一个需要被删除的实体的ID。
     */
    public void deleteBatch(Long[] ids) {
        // 通过repository的deleteAllById方法，根据提供的ID数组批量删除记录
        mdocCaseRepository.deleteAllById(Arrays.asList(ids));
    }
}

