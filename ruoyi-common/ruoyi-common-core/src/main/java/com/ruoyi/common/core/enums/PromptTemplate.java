package com.ruoyi.common.core.enums;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * GPT提示模板
 *
 * @author fcs
 * @date 2024/2/5 23:16
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Getter
public enum PromptTemplate {

    /**
     * 司法案例信息挖掘模板
     */
    CASE("case", "返回结构：\n" +
        "{\n" +
        "\"keyword\": \"\",\n" +
        "\"summary\": \"\",\n" +
        "\"plea\": \"\",\n" +
        "\"label\": \"\",\n" +
        "\"plai\": \"\",\n" +
        "\"defe\": \"\",\n" +
        "\"article\": \"xx\",\n" +
        "\"party\": {\n" +
        "\"plaintiff\": \"\",\n" +
        "\"defendant\": \"\"\n" +
        "},\n" +
        "\"fact\": \"\",\n" +
        "\"note\": \"\"\n" +
        "}\n" +
        "\n" +
        "字段说明：\n" +
        "keyword(关键词，至多4个司法要素相关的词语，必要)\n" +
        "summary(摘要总结至少100字，必要)\n" +
        "plea(诉讼要求，必要)\n" +
        "label(案件类型，必要)\n" +
        "plai(原告诉述，必要)\n" +
        "defe(被告辩称，必要)\n" +
        "article(法律依据 第xx条，必要)\n" +
        "party(当事人，即plaintiff（原告），defendant（被告），必要)\n" +
        "fact(法院意见，如:认定如下../本院裁定如下...等，必要)\n" +
        "note（审判员xxx，日期xxx，书记员xxx，必要）\n" +
        "\n" +
        "参考示例：\n" +
        "{\n" +
        "\"keyword\": \"行政处罚、冻结、划拨、银行存款、动产、不动产、民事诉讼法\",\n" +
        "\"summary\": \"\n" +
        "本案为申请执行人瓜州县消防救援大队与被执行人瓜州县三元物业服务有限公司之间的行政非诉执行案件。被执行人未履行生效法律文书确定的给付义务，法院根据相关法律规定裁定冻结、划拨被执行人银行存款，扣留、提取其应当履行部分的收入，或查封、扣押同等价值的财产。\",\n" +
        "\"plea\": \"瓜州县消防救援大队要求瓜州县三元物业服务有限公司交纳12500元案款和88元执行费\",\n" +
        "\"label\": \"行政非诉执行案\",\n" +
        "\"plai\": \"瓜州县消防救援大队认为瓜州县三元物业服务有限公司未按规定交纳应缴费用\",\n" +
        "\"defe\": \"瓜州县三元物业服务有限公司认为其无违规行为，不应交纳相关费用\",\n" +
        "\"article\": \"\n" +
        "《中华人民共和国民事诉讼法》第二百四十九条、第二百五十条、第二百五十一条；《最高人民法院关于适用《中华人民共和国民事诉讼法》的解释》第四百八十五条\",\n" +
        "\"party\": {\n" +
        "\"plaintiff\": \"瓜州县消防救援大队\",\n" +
        "\"defendant\": \"瓜州县三元物业服务有限公司\"\n" +
        "},\n" +
        "\"fact\": \"\n" +
        "因被执行人瓜州县三元物业服务有限公司拒不履行生效法律文书确定的给付义务，法院裁定冻结、划拨被执行人瓜州县三元物业服务有限公司银行存款人民币12588元，银行存款不足清偿的，扣留、提取被执行人应当履行部分的收入或查封、扣押其同等价值的财产。\",\n" +
        "\"note\": \"审判员陈典二〇二三年三月二十三日书记员丁宇婕\"\n" +
        "}\n" +
        "将下述目标文本（司法案例文书正文内容）根据上述字段说明进行提取、修正，对为空字段值的按照理解生成补充对应内容，如果确实没有或未提到则对应字段值为空字符串，最后严格按照上述返回结构的json纯文本返回给我" +
        "\n" + "目标文本： " + "\n"),

    /**
     * 法律法规信息挖掘模板
     */
    LAW("law", "law"),
    ;

    private final String name;
    private final String template;

    PromptTemplate(String name, String template) {
        this.name = name;
        this.template = template;
    }
}
