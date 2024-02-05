package com.ruoyi.manage;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.restful.HanLPClient;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.manage.domain.DocCase;
import com.ruoyi.manage.service.IDocCaseService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fcs
 * @date 2024/1/31 11:49
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@SpringBootTest
@Slf4j
public class DataProcessTest {

    @Resource
    private HanLPClient hanLP;

    @Resource
    private IDocCaseService docCaseService;

    public static void main(String[] args) {
        String data = "申请执行人：牟定县卫生和计划生育局。\uE47E法定代表人：普华，局长。被执行人：李某某1\uE127??牟定县卫生和计划生育局申请执行李某某1行政非诉一案，依据（2017）云2323行审22号行政裁定书，被执行人李某某1应缴纳社会抚养费26090元，因一直未履行完毕，该案于2017年10月17日终结本次执行程序。本院于2023年3月17日对该案恢复执行，在执行过程中，因国家生育政策发生改变，牟定县卫生和计划生育局于2023年3月17日向本院提出申请，不再要求被执行人李某某1缴纳社会抚养费。\uE014故本院依法终结执行，并解除对被执行人李某某1纳入失信被执行人名单及限制其高消费的执行措施。\uE0CD据此，依照《中华人民共和国民事诉讼法》第二百六十四条第（六）项之规定，裁定如下：终结云南省牟定县人民法院的（2023）云2323执恢39号案件的执行。本裁定书送达后即发生法律效力。审判员张**二〇二三年三月二十二日书记员窦靓\n";
        String cleanString = stripUnicode(data);
        String jsonStr = buildJsonArray(cleanString);
        System.out.println(cleanString);
        System.out.println(jsonStr);
    }

    /**
     * 去除异常unicode
     *
     * @param data
     * @return String
     */
    private static String stripUnicode(String data) {
        data = data.replace("?", "");
        StringBuilder cleanStringBuilder = new StringBuilder();
        for (char ch : data.toCharArray()) {
            if (Character.isUnicodeIdentifierPart(ch)) {
                cleanStringBuilder.append(ch);
            } else {
                if (!isPunctuation(ch) && !Character.isWhitespace(ch)) {
                    cleanStringBuilder.append('\n');
                } else {
                    cleanStringBuilder.append(ch);
                }
            }
        }
        String cleanString = cleanStringBuilder.toString();
        String[] temp = cleanString.split("。");
        temp[temp.length - 1] = "\n" + temp[temp.length - 1];
        cleanString = StringUtils.join(temp, "");
        return cleanString;
    }

    /**
     * 判断是否是标点符号
     *
     * @param ch
     * @return boolean
     */
    public static boolean isPunctuation(char ch) {
        // 根据Unicode标准，中英文标点符号的范围
        // CJK标点符号
        return ch >= '!' && ch <= '~' || ch >= 0x3000 && ch <= 0x303F || ch >= 0xFF01 && ch <= 0xFF0F || ch >= 0xFF1A && ch <= 0xFF20 || ch >= 0xFF3B && ch <= 0xFF40 || ch >= 0xFF5B && ch <= 0xFF65;
    }


    /**
     * 将文本转换为JSON数组
     *
     * @param text
     * @return String
     */
    public static String buildJsonArray(String text) {
        String[] paragraphs = text.split("\n");
        JSONArray array = new JSONArray();
        array.addAll(Arrays.asList(paragraphs));
        return array.toJSONString();
    }

    /**
     * 将文本转换为JSON对象
     *
     * @param text
     * @return String
     */
    public static String buildJsonObj(String text) {
        String[] paragraphs = text.split("\n");
        JSONObject json = new JSONObject();
        // 根据文本的段落内容，填充JSON对象的字段
        json.put("plea", paragraphs[0]); // 诉讼要求
//        json.put("type", "行政非诉"); // 案件类型
        json.put("plai", paragraphs[2]); // 原告诉述
        json.put("defe", paragraphs[3]); // 被告辩称
//        json.put("cause", "行政非诉案件"); // 案由
//        json.put("article", "《中华人民共和国民事诉讼法》第二百六十四条第（六）项"); // 法律依据
//        json.put("party", "牟定县卫生和计划生育局、李某某1"); // 当事人
        json.put("fact", paragraphs[4]); // 法院意见
//        json.put("note", "审判员张**，二〇二三年三月二十二日，书记员窦靓"); // 审判员和日期
        return json.toJSONString();
    }

    /**
     * 提取裁判文书的裁判员和日期
     *
     * @param text
     * @return String
     */
    public static String extractNote(String text) {
        String[] roles = {"审判员", "审判长", "执行员", "代理审判员"};
        for (String role : roles) {
            Pattern pattern = Pattern.compile(role + ".+?([\\r\\n]+)", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group().trim();
            }
        }
        return "";
    }

    @Test
    public void test() throws IOException {
//        HanLPClient hanLP = new HanLPClient("https://www.hanlp.com/api", "NDE4OEBiYnMuaGFubHAuY29tOjBBa0hKY2p3YVhUelVoRFY="); // auth不填则匿名，zh中文，mul多语种
        DocCase docCase = docCaseService.selectDocCaseByName("新疆巴音郭楞蒙古自治州住房和城乡建设局、新疆裕邦房地产开发有限公司行政案由执行实施执行裁定书");
        String summarization = hanLP.abstractiveSummarization(docCase.getContent().substring(0, 1000));
        prettyPrint(summarization);
    }

    void prettyPrint(Object object) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        log.info(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
    }
}
