package com.ruoyi.common.core.utils.gpt;

import org.junit.Test;

/**
 * GTP工具类，实现了LawProcess接口。
 *
 * @author csFan
 * @version 1.0
 * @description 包括案例和法律条文的解析与修订。
 * @date 2024/3/11 10:57
 */
public class GTPUtils implements LawProcess {

    /**
     * 案例修订。
     * 通过对输入的文本进行处理，返回修订后的文本。
     *
     * @param text 待修订的案例文本字符串。
     * @return 修订后的文本字符串。
     */
    @Override
    public String caseRevise(String text) {
        return GeminiUtils.caseRevise(text);
    }

    /**
     * 案例解析。
     * 通过对输入的文本进行处理，返回解析后的文本。
     *
     * @param text 待解析的案例文本字符串。
     * @return 解析后的文本字符串。
     */
    @Override
    public String caseParse(String text) {
        return GeminiUtils.caseParse(text);
    }

    /**
     * 法律修订。
     * 通过对输入的文本进行处理，返回修订后的文本。
     *
     * @param text 待修订的法律条文文本字符串。
     * @return 修订后的文本字符串。
     */
    @Override
    public String lawRevise(String text) {
        return GeminiUtils.lawRevise(text);
    }

    /**
     * 法律解析。
     * 通过对输入的文本进行处理，返回解析后的文本。
     *
     * @param text 待解析的法律条文文本字符串。
     * @return 解析后的文本字符串。
     */
    @Override
    public String lawParse(String text) {
        return GeminiUtils.lawParse(text);
    }

    /**
     * 测试方法。
     * 用于测试案例和法律条文的解析与修订功能。
     */
    @Test
    public void test() {
        caseParse("+ \"(2023)辽0106财保154号申请人郭某某1委托代理人王某某1被申请人袁某某1申请人于2023年2月16日向本院申请财产保全，请求查封、扣押、冻结被申请人价值人民币*元的财产并提供保险公司诉讼财产保全责任保险保单保函作为担保。本院认为，申请人的申请符合法律规定，依照《中华人民共和国民事诉讼法》第一百零四条、一百零五条、第一百零六条之规定，裁定如下：依法冻结被申请人名下银行存款*元或查封、扣押同等价值的其他财产；申请人在人民法院采取保全措施后三十日内不依法提起诉讼或者申请仲裁的，本院将依法解除保全。本裁定立即执行。如不服本裁定，可以自收到在裁定书之日起五日内向本院申请复议一次，复议期间不停止本裁定的执行。审判员王洋二〇二三年二月一十六日法官助理殷健书记员鲍希秀");
    }
}
