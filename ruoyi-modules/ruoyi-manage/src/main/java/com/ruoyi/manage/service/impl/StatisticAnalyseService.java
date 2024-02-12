package com.ruoyi.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计分析业务实现类
 *
 * @author fcs
 * @date 2024/2/8 12:25
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description 进行统计分析<br />
 * todo <p>1. 司法案例和法律法规数据总量</p>
 * <p>2. 司法案例和法律法规数据量增量（模糊查询新建日期大于等于前一天的所有数据量）</p>
 * <p>3. 全国各省案件数量，各文书类型数量，各根案由数量，各审判程序数量，各个数据来源数量</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class StatisticAnalyseService {
    private final DocCaseMapper docCaseMapper;
    private final LawRegulationMapper lawRegulationMapper;

    /**
     * 从案号中提取省份
     *
     * @param caseNumber 案号
     * @return String-省份
     */
    private static String extractProvinceFromCaseNumber(String caseNumber) {
        if (caseNumber == null) {
            return null;
        }
        char provinceAbbr = caseNumber.charAt(6);
        // 这里需要一个映射来将省份代码转换为省份简称
        // 例如：(2023)辽06民终351号，省份代码是"辽"，对应的省份简称是"辽宁"
        // 你可以根据需要添加更多的省份映射
        switch (provinceAbbr) {
            case '辽':
                return "辽宁";
            case '京':
                return "北京";
            case '沪':
                return "上海";
            case '津':
                return "天津";
            case '渝':
                return "重庆";
            case '冀':
                return "河北";
            case '晋':
                return "山西";
            case '蒙':
                return "内蒙古";
            case '吉':
                return "吉林";
            case '黑':
                return "黑龙江";
            case '苏':
                return "江苏";
            case '浙':
                return "浙江";
            case '皖':
                return "安徽";
            case '闽':
                return "福建";
            case '赣':
                return "江西";
            case '鲁':
                return "山东";
            case '豫':
                return "河南";
            case '鄂':
                return "湖北";
            case '湘':
                return "湖南";
            case '粤':
                return "广东";
            case '桂':
                return "广西";
            case '琼':
                return "海南";
            case '川':
                return "四川";
            case '贵':
                return "贵州";
            case '云':
                return "云南";
            case '藏':
                return "西藏";
            case '陕':
                return "陕西";
            case '甘':
                return "甘肃";
            case '青':
                return "青海";
            case '宁':
                return "宁夏";
            case '新':
                return "新疆";
            case '港':
                return "香港";
            case '澳':
                return "澳门";
            case '台':
                return "台湾";
            // 更多省份...
            default:
                return null;
        }
    }

    private Map<String, Object> initProvinceMap(String province) {
        HashMap<String, Object> provinceMap = new HashMap<>(1);
        provinceMap.put("name", province);
        provinceMap.put("value", 0);
        return provinceMap;
    }

    /**
     * 统计全国各省案件数量
     *
     * @return Map<String, Integer>-全国各省案件数量
     */
    public Map<String, Integer> countCasesByProvince() {
        List<DocCase> docCases = docCaseMapper.selectList();
        String[] caseNumbers = docCases.stream().map(DocCase::getNumber).toArray(String[]::new);
        Map<String, Integer> provinceCases = new HashMap<>(34);
        for (String caseNumber : caseNumbers) {
            String province = extractProvinceFromCaseNumber(caseNumber);
            if (province != null) {
                provinceCases.put(province, provinceCases.getOrDefault(province, 0) + 1);
            }
        }
        return provinceCases;
    }

    /**
     * 各案例文书类型数量
     *
     * @return Map<String, Integer>-各文书类型数量
     */
    public Map<String, Long> countCasesByType() {
        List<Map<String, Object>> typeCases = docCaseMapper.countByType();
        Map<String, Long> types = new HashMap<>(6);
//        log.info("{}", typeCases);
        typeCases.forEach(map -> {
            if (map.containsKey("type")) {
                types.put((String) map.get("type"), (Long) map.get("count"));
            }
        });
        return types;
//        List<DocCase> docCases = docCaseMapper.selectList();
//        String[] types = docCases.stream().map(DocCase::getType).toArray(String[]::new);
//        Map<String, Integer> typeCases = new HashMap<>(6);
//        for (String type : types) {
//            typeCases.put(type, typeCases.getOrDefault(type, 0) + 1);
//        }
//        return typeCases;
    }

    /**
     * 各法条类型数量
     *
     * @return Map<String, Integer>-各法条类型数量
     */
    public Map<String, Long> countLawsByType() {
        List<Map<String, Object>> typeLaws = lawRegulationMapper.countByType();
        Map<String, Long> types = new HashMap<>(6);
//        log.info("{}", typeLaws);
        typeLaws.forEach(map -> {
            if (map.containsKey("type")) {
                types.put((String) map.get("type"), (Long) map.get("count"));
            }
        });
        return types;
    }


    /**
     * 各案例根案由类型数量
     *
     * @return Map<String, Integer>-各根案由类型数量
     */
    public Map<String, Integer> countCasesByRootCase() {
        List<DocCase> docCases = docCaseMapper.selectList();
        String[] rootCauses = docCases.stream().map(DocCase::getCause).toArray(String[]::new);
        Map<String, Integer> rootCauseCases = new HashMap<>(5);
        for (String rootCause : rootCauses) {
            rootCauseCases.put(rootCause, rootCauseCases.getOrDefault(rootCause, 0) + 1);
        }
        return rootCauseCases;
    }

    /**
     * 各案例审判程序类型数量
     *
     * @return Map<String, Integer>-各审判程序类型数量
     */
    public Map<String, Integer> countCasesByProcess() {
        List<DocCase> docCases = docCaseMapper.selectList();
        String[] processes = docCases.stream().map(DocCase::getProcess).toArray(String[]::new);
        Map<String, Integer> processCases = new HashMap<>(5);
        for (String process : processes) {
            processCases.put(process, processCases.getOrDefault(process, 0) + 1);
        }
        return processCases;
    }


    /**
     * 获取平台收集案件总数
     *
     * @return Long-所有案件数量
     */
    public Long caseTotal() {
        return docCaseMapper.selectCount(null);
    }

    /**
     * 获取平台收集法律法规总数
     *
     * @return Long-所有法律法规数量
     */
    public Long lawTotal() {
        return lawRegulationMapper.selectCount(null);
    }

    /**
     * 获取平台收集日案件增量
     *
     * @return Long-日增量案件数量
     */
    public Long caseIncrement() {
        // 计算昨天的日期
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        // 格式化日期字符串
        String formattedDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LambdaQueryWrapper<DocCase> lqw = new LambdaQueryWrapper<>();
        lqw.ge(DocCase::getCreateTime, formattedDate);
        lqw.or().ge(DocCase::getUpdateTime, formattedDate);
        return docCaseMapper.selectCount(lqw);
    }

    /**
     * 获取平台收集日法律法规增量
     *
     * @return Long-日增量法律法规数量
     */
    public Long lawIncrement() {
        // 计算昨天的日期
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        // 格式化日期字符串
        String formattedDate = yesterday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LambdaQueryWrapper<LawRegulation> lqw = new LambdaQueryWrapper<>();
        lqw.ge(LawRegulation::getCreateTime, formattedDate);
        lqw.or().ge(LawRegulation::getUpdateTime, formattedDate);
        return lawRegulationMapper.selectCount(lqw);
    }

    public List<DocCaseVo> selectNewTenCases() {
        LambdaQueryWrapper<DocCase> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(DocCase::getCreateTime);
//        获取前10条数据
        lqw.last("limit 10");
        return docCaseMapper.selectVoList(lqw);
    }

    public List<LawRegulationVo> selectNewTenLaws() {
        LambdaQueryWrapper<LawRegulation> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(LawRegulation::getCreateTime);
//        获取前10条数据
        lqw.last("limit 10");
        return lawRegulationMapper.selectVoList(lqw);
    }
}
