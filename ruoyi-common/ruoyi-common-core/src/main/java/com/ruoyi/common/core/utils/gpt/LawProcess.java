package com.ruoyi.common.core.utils.gpt;

/**
 * 司法数据处理接口
 *
 * @author csFan
 * @version 1.0
 * @description: 提供法律文档的修订和解析功能
 * @date 2024/3/11 11:53
 */
public interface LawProcess {
    /**
     * 案件修订
     *
     * @param text 待修订的案件文本
     * @return 修订后的案件文本
     */
    String caseRevise(String text);

    /**
     * 案件解析
     *
     * @param text 待解析的案件文本
     * @return JsonString 解析后的案件文本或相关信息
     */
    String caseParse(String text);

    /**
     * 法律修订
     *
     * @param text 待修订的法律文本
     * @return 修订后的法律文本
     */
    String lawRevise(String text);

    /**
     * 法条解析
     *
     * @param text 待解析的法律文本
     * @return JsonString 解析后的法律文本或相关信息
     */
    String lawParse(String text);
}
