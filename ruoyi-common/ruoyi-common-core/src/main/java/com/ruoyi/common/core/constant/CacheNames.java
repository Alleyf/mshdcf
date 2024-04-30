package com.ruoyi.common.core.constant;

/**
 * 缓存组名称常量
 * <p>
 * key 格式为 cacheNames#ttl#maxIdleTime#maxSize
 * <p>
 * ttl 过期时间 如果设置为0则不过期 默认为0
 * maxIdleTime 最大空闲时间 根据LRU算法清理空闲数据 如果设置为0则不检测 默认为0
 * maxSize 组最大长度 根据LRU算法清理溢出数据 如果设置为0则无限长 默认为0
 * <p>
 * 例子: test#60s、test#0#60s、test#0#1m#1000、test#1h#0#500
 *
 * @author csFan
 */
public interface CacheNames {

    /**
     * 演示案例
     */
    String DEMO_CACHE = "demo:cache#60s#10m#20";

    /**
     * 系统配置
     */
    String SYS_CONFIG = "sys_config";

    /**
     * 数据字典
     */
    String SYS_DICT = "sys_dict";

    /**
     * 用户账户
     */
    String SYS_USER_NAME = "sys_user_name#30d";

    /**
     * 部门
     */
    String SYS_DEPT = "sys_dept#30d";

    /**
     * OSS内容
     */
    String SYS_OSS = "sys_oss#30d";

    /**
     * OSS配置
     */
    String SYS_OSS_CONFIG = "sys_oss_config";

    /**
     * 在线用户
     */
    String ONLINE_TOKEN = "online_tokens";

    // 省份编号常量（缓存15分钟）
    String PROVINCE_NUMBER = "statistical_analysis:province_number#15m";

    // 案件类型常量
    String CASE_TYPE = "statistical_analysis:case_type#15m";

    // 适用法律类型常量
    String LAW_TYPE = "statistical_analysis:law_type#15m";

    // 案件起因常量
    String CASE_CAUSE = "statistical_analysis:case_cause#15m";

    // 案件处理过程常量
    String CASE_PROCESS = "statistical_analysis:case_process#15m";

    // 新增案件数量常量
    String NEW_CASES = "statistical_analysis:new_cases#15m";

    // 新增法律条文常量
    String NEW_LAWS = "statistical_analysis:new_laws#15m";
    String CASE_TOTAL = "statistical_analysis:case_total#3m#10m#20";
    String LAW_TOTAL = "statistical_analysis:case_total#3m#10m#20";
}
