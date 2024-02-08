package com.ruoyi.manage.mapper;

import com.ruoyi.common.mybatis.core.mapper.BaseMapperPlus;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 法律法规Mapper接口
 *
 * @author alleyf
 * @date 2024-01-27
 */
public interface LawRegulationMapper extends BaseMapperPlus<LawRegulationMapper, LawRegulation, LawRegulationVo> {

    /**
     * 按照类型分组查询法律法规
     *
     * @return Map
     */
    @Select("SELECT type, Count(*) as count FROM law_regulation group by type")
    List<Map<String, Object>> countByType();
}
