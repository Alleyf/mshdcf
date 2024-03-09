package com.ruoyi.manage.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 司法案例Mapper接口
 *
 * @author alleyf
 * @date 2024-01-26
 */
public interface DocCaseMapper extends BaseMapperPlus<DocCaseMapper, DocCase, DocCaseVo> {

    /**
     * 按照类型分组查询司法案例列表
     *
     * @return 法案例列表
     */
    @Select("SELECT type, Count(*) as count FROM doc_case group by type")
    List<Map<String, Object>> countByType();
}
