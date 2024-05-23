package com.ruoyi.manage.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.utils.StringUtils;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据处理工具类
 *
 * @author fcs
 * @date 2024/2/8 12:04
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
public class ProcessUtils {
    /**
     * 去除司法案件异常unicode
     *
     * @param data 原始文本
     * @return String
     */
    public static String stripCaseUnicode(String data) {
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
        String cleanString = cleanStringBuilder.toString().replaceAll("\\n+", "\n").trim();
//        此处通过最后一句认定为文书记录不合理，尤其是法律法规没有记录信息无需切分获取
//        String[] temp = cleanString.split("。");
//        temp[temp.length - 1] = "\n" + temp[temp.length - 1];
//        cleanString = StringUtils.join(temp, "").replaceAll("\\n+", "\n").trim();
        return cleanString;
    }

    /**
     * 去除法律法规异常unicode
     *
     * @param data 原始文本
     * @return String
     */
    public static String stripLawUnicode(String data) {
        data = data.replace("?", "");
        StringBuilder cleanStringBuilder = new StringBuilder();
        for (char ch : data.toCharArray()) {
            if (Character.isUnicodeIdentifierPart(ch)) {
//              有效unicode字符码
                cleanStringBuilder.append(ch);
            } else {
                if (!isPunctuation(ch) && !Character.isWhitespace(ch)) {
//                    异常unicode码且不为空字符
                    cleanStringBuilder.append('\n');
                } else {
//                    正常标点符号
                    cleanStringBuilder.append(ch);
                }
            }
        }
        String cleanString = cleanStringBuilder.toString().replaceAll("\\n+", "\n").trim();
//        todo 此处通过最后一句认定为文书记录不合理，尤其是法律法规没有记录信息无需切分获取
//        String[] temp = cleanString.split("。");
//        temp[temp.length - 1] = "\n" + temp[temp.length - 1];
//        cleanString = StringUtils.join(temp, "");
        return cleanString;
    }

    /**
     * 判断是否是标点符号
     *
     * @param ch 原始字符
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
     * @param text 原始文本
     * @return String
     */
    public static String buildJsonArray(String text) {
        String[] paragraphs = text.split("\n");
        // 过滤掉空字符串
        paragraphs = Arrays.stream(paragraphs)
            .filter(s -> !s.isEmpty())
            .toArray(String[]::new);
        JSONArray array = new JSONArray();
        array.addAll(Arrays.asList(paragraphs));
        return array.toJSONString();
    }

    /**
     * 正则解析获取司法案件extra-JSON字符串
     *
     * @param text 原始文本
     * @return String
     */
    public static String buildCaseExtra(String text) {
        String[] paragraphs = text.split("\n");
        // 过滤掉空字符串
        paragraphs = Arrays.stream(paragraphs)
            .filter(s -> !s.isEmpty())
            .toArray(String[]::new);
        String filteredText = StringUtils.join(paragraphs, "\n");
        JSONObject json = new JSONObject();
        JSONObject partyObj = new JSONObject();
        // 根据文本的段落内容，填充JSON对象的字段
        // 定义正则表达式模式
        String pleaPattern = "要求";
        String plaintiffPattern = "申请执行人：(.*?)[\\，|\\。|\\；|\\n|\\：]";
        String defendantPattern = "被执行人：(.*?)[\\，|\\。|\\；|\\n|\\：]";
        String plaiPattern = "原告诉述.*?";
        String defePattern = "被告辩称.*?";
        String factPattern = "依法";
        String articlePattern = "依照(.*?)[\\，|\\。|\\；|\\n|\\：]";
        String notePattern = "(审判员|审判长|执行员|代理审判员|书记员).*";
        // 使用正则表达式提取第一个匹配项
        for (String paragraph : paragraphs) {
            if (paragraph.contains(pleaPattern)) {
                json.put("plea", paragraph);
            } else if (paragraph.matches(plaiPattern)) {
                json.put("plai", paragraph);
            } else if (paragraph.matches(defePattern)) {
                json.put("defe", paragraph);
            } else if (paragraph.contains(factPattern)) {
                json.put("fact", paragraph);
            }
        }
        extractParty(filteredText, plaintiffPattern, partyObj, "plaintiff");
        extractParty(filteredText, defendantPattern, partyObj, "defendant");
        json.put("party", partyObj);
        extractFirstMatch(filteredText, articlePattern, json, "article");
        extractLastNote(filteredText, notePattern, json, "note");
        return json.toJSONString();
    }


    /**
     * 正则提取裁判文书的语义字段
     *
     * @param text 原始文本
     * @return String
     */
    private static void extractFirstMatch(String text, String pattern, JSONObject json, String key) {
        Pattern compiledPattern = Pattern.compile(pattern, Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = compiledPattern.matcher(text);
        if (matcher.find()) {
            json.put(key, matcher.group(1));
        }
    }


    /**
     * 正则提取裁判文书的当事人信息
     *
     * @param text 原始文本
     * @return String
     */
    private static void extractParty(String text, String pattern, JSONObject json, String key) {
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        if (m.find()) {
            json.put(key, m.group(1));
        }
    }

    /**
     * 正则提取裁判文书的裁判员和日期
     *
     * @param text 原始文本
     * @return String
     */
    public static void extractLastNote(String text, String regex, JSONObject json, String key) {
        Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        // 从后向前查找
        boolean found = false;
        String result = "";
        while (matcher.find()) {
            result = matcher.group();
            found = true;
        }
        if (found) {
            json.put(key, result);
        }
    }
}
