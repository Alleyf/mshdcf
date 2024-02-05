package com.ruoyi.manage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.domain.bo.LawRegulationBo;
import com.ruoyi.common.mybatis.core.page.PageQuery;
import com.ruoyi.common.mybatis.core.page.TableDataInfo;
import com.ruoyi.retrieve.api.domain.LawDoc;

import java.util.Collection;
import java.util.List;

/**
 * 法律法规Service接口
 *
 * @author alleyf
 * @date 2024-01-27
 */
public interface ILawRegulationService extends IService<LawRegulation> {

    /**
     * 查询法律法规
     */
    LawRegulationVo queryById(Long id);

    /**
     * 查询法律法规列表
     */
    TableDataInfo<LawRegulationVo> queryPageList(LawRegulationBo bo, PageQuery pageQuery);

    /**
     * 查询法律法规列表
     */
    List<LawRegulationVo> queryList(LawRegulationBo bo);

    /**
     * 修改法律法规
     */
    Boolean insertByBo(LawRegulationBo bo);

    /**
     * 批量插入
     */
    Boolean insertBatch();

    /**
     * 修改法律法规
     */
    Boolean updateByBo(LawRegulationBo bo);

    /**
     * 校验并批量删除法律法规信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据名称查询法律法规
     */
    LawRegulation selectLawRegulationByName(String name);
}
