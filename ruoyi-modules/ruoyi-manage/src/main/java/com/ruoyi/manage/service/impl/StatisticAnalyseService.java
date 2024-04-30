package com.ruoyi.manage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ruoyi.common.core.constant.CacheNames;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.domain.LawRegulation;
import com.ruoyi.manage.domain.vo.DocCaseVo;
import com.ruoyi.manage.domain.vo.LawRegulationVo;
import com.ruoyi.manage.mapper.DocCaseMapper;
import com.ruoyi.manage.mapper.LawRegulationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    // 使用HashMap来存储省份代码和简称的映射关系
    private static final Map<Character, String> provinceCodeMap = new HashMap<>();

    static {
        provinceCodeMap.put('辽', "辽宁");
        provinceCodeMap.put('京', "北京");
        provinceCodeMap.put('沪', "上海");
        provinceCodeMap.put('津', "天津");
        provinceCodeMap.put('渝', "重庆");
        provinceCodeMap.put('冀', "河北");
        provinceCodeMap.put('晋', "山西");
        provinceCodeMap.put('蒙', "内蒙古");
        provinceCodeMap.put('吉', "吉林");
        provinceCodeMap.put('黑', "黑龙江");
        provinceCodeMap.put('苏', "江苏");
        provinceCodeMap.put('浙', "浙江");
        provinceCodeMap.put('皖', "安徽");
        provinceCodeMap.put('闽', "福建");
        provinceCodeMap.put('赣', "江西");
        provinceCodeMap.put('鲁', "山东");
        provinceCodeMap.put('豫', "河南");
        provinceCodeMap.put('鄂', "湖北");
        provinceCodeMap.put('湘', "湖南");
        provinceCodeMap.put('粤', "广东");
        provinceCodeMap.put('桂', "广西");
        provinceCodeMap.put('琼', "海南");
        provinceCodeMap.put('川', "四川");
        provinceCodeMap.put('贵', "贵州");
        provinceCodeMap.put('云', "云南");
        provinceCodeMap.put('藏', "西藏");
        provinceCodeMap.put('陕', "陕西");
        provinceCodeMap.put('甘', "甘肃");
        provinceCodeMap.put('青', "青海");
        provinceCodeMap.put('宁', "宁夏");
        provinceCodeMap.put('新', "新疆");
        provinceCodeMap.put('港', "香港");
        provinceCodeMap.put('澳', "澳门");
        provinceCodeMap.put('台', "台湾");
        // 更多省份...
    }

    private final DocCaseMapper docCaseMapper;
    private final LawRegulationMapper lawRegulationMapper;

    /**
     * 从案号中提取省份
     *
     * @param caseNumber 案号
     * @return String 省份
     */
    public static String extractProvinceFromCaseNumber(String caseNumber) {
        if (caseNumber == null || caseNumber.length() < 7) {
            // 检查caseNumber是否为null或长度不足
            return null;
        }
        char provinceAbbr = caseNumber.charAt(6);
        // 使用映射查找省份简称
        // 如果找不到匹配的省份，返回null
        // 可以在这里添加日志记录或其他错误处理
        return provinceCodeMap.get(provinceAbbr);
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
    @Cacheable(cacheNames = CacheNames.PROVINCE_NUMBER)
    public Map<String, Long> countCasesByProvince() {
        List<DocCaseVo> docCases = docCaseMapper.selectVoList(Wrappers.lambdaQuery(DocCase.class).select(DocCase::getNumber));
        // 使用HashMap进行省份统计，以提高性能
        Map<String, Long> provinceCases = new HashMap<>(34);
        docCases.forEach(docCase -> {
            String province = extractProvinceFromCaseNumber(docCase.getNumber());
            if (province != null) {
                provinceCases.put(province, provinceCases.getOrDefault(province, 0L) + 1L);
            }
        });

//        docCases.forEach(docCase -> {
//            String province = extractProvinceFromCaseNumber(docCase.getNumber());
//            if (province != null) {
//                docCase.setProvince(province);
//            }
//        });
//        Map<String, Long> provinceCases = docCases.stream()
//            .collect(Collectors.groupingBy(DocCaseVo::getProvince, Collectors.counting()));
//        provinceCases.entrySet().stream()
//            .collect(Collectors.toMap(
//                entry -> extractProvinceFromCaseNumber(entry.getKey()), // 将案号转换为省份
//                Map.Entry::getValue // 保持案件数量不变
//            ));
        return provinceCases;
    }

    /**
     * 各案例文书类型数量
     *
     * @return Map<String, Integer>-各文书类型数量
     */
    @Cacheable(cacheNames = CacheNames.CASE_TYPE)
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
    @Cacheable(cacheNames = CacheNames.LAW_TYPE)
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
    @Cacheable(cacheNames = CacheNames.CASE_CAUSE)
    public Map<String, Long> countCasesByRootCause() {
        List<DocCase> docCases = docCaseMapper.selectList(Wrappers.lambdaQuery(DocCase.class).select(DocCase::getCause));
        Map<String, Long> rootCauseCases = docCases.stream()
            .collect(Collectors.groupingBy(DocCase::getCause, Collectors.counting()));
//        String[] rootCauses = docCases.stream().map(DocCase::getCause).toArray(String[]::new);
//        Map<String, Integer> rootCauseCases = new HashMap<>(5);
//        for (String rootCause : rootCauses) {
//            rootCauseCases.put(rootCause, rootCauseCases.getOrDefault(rootCause, 0) + 1);
//        }
        return rootCauseCases;
    }

    /**
     * 各案例审判程序类型数量
     *
     * @return Map<String, Integer>-各审判程序类型数量
     */
    @Cacheable(cacheNames = CacheNames.CASE_PROCESS)
    public Map<String, Long> countCasesByProcess() {
        List<DocCase> docCases = docCaseMapper.selectList(Wrappers.lambdaQuery(DocCase.class).select(DocCase::getProcess));
        Map<String, Long> processCases = docCases.stream()
            .collect(Collectors.groupingBy(DocCase::getProcess, Collectors.counting()));
//        String[] processes = docCases.stream().map(DocCase::getProcess).toArray(String[]::new);
//        Map<String, Integer> processCases = new HashMap<>(5);
//        for (String process : processes) {
//            processCases.put(process, processCases.getOrDefault(process, 0) + 1);
//        }
        return processCases;
    }


    /**
     * 获取平台收集案件总数
     *
     * @return Long-所有案件数量
     */
    @Cacheable(cacheNames = CacheNames.CASE_TOTAL, sync = true)
    public Long caseTotal() {
        return docCaseMapper.selectCount(null);
    }

    /**
     * 获取平台收集法律法规总数
     *
     * @return Long-所有法律法规数量
     */
    @Cacheable(cacheNames = CacheNames.LAW_TOTAL)
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

    /**
     * 获取平台最新10条案件
     *
     * @return List<DocCaseVo>-最新10条案件
     */
    @Cacheable(cacheNames = CacheNames.NEW_CASES)
    public List<DocCaseVo> selectNewTenCases() {
        LambdaQueryWrapper<DocCase> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(DocCase::getCreateTime);
//        获取前10条数据
        lqw.last("limit 10");
        return docCaseMapper.selectVoList(lqw);
    }

    /**
     * 获取平台最新10条法律法规
     *
     * @return List<LawRegulationVo>-最新10条法律法规
     */
    @Cacheable(cacheNames = CacheNames.NEW_LAWS)
    public List<LawRegulationVo> selectNewTenLaws() {
        LambdaQueryWrapper<LawRegulation> lqw = new LambdaQueryWrapper<>();
        lqw.orderByDesc(LawRegulation::getCreateTime);
//        获取前10条数据
        lqw.last("limit 10");
        return lawRegulationMapper.selectVoList(lqw);
    }
}
