package com.ruoyi.common.core.utils.gpt;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.constant.GPTConstants;
import com.ruoyi.common.core.domain.GeminiGPTBo;
import com.ruoyi.common.core.enums.PromptTemplate;
import com.ruoyi.common.core.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * Gemini-Pro速度还行（使用cloudFlare代理的国内域名限制每月请求3000次）
 *
 * @author fcs
 * @date 2024/2/5 20:49
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Slf4j
@AutoConfiguration
public class GeminiUtils {

    @Value("${gpt.gemini.option:2}")
//    public static final int OPTION;
    public static final int OPTION = 2;
    @Value("${gpt.gemini.isProxy:false}")
//    public static final boolean isProxy;
    public static final boolean isProxy = false;
    /**
     * 代理
     */
    public static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(GPTConstants.PROXY_HOST, GPTConstants.PROXY_PORT));
    /**
     * 创建OkHttpClient时设置代理
     */
    public static OkHttpClient client = !isProxy ?
        new OkHttpClient.Builder()
            // 设置读取超时时间为60秒,超时时间过短导致结果尚未返回报错
            .readTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .build() :
        new OkHttpClient.Builder()
//            设置代理
            .proxy(proxy)
            // 设置读取超时时间为60秒
            .readTimeout(1, TimeUnit.MINUTES)
            .callTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .build();

    /**
     * 同步请求
     *
     * @param text 请求文本
     * @return String 请求结果
     */
    public static String chat(String text) {
        return syncGetResponse(text);
    }

    /**
     * 获取同步请求
     *
     * @param text 请求文本
     * @return String 请求结果
     */
    private static String syncGetResponse(String text) {
        try {
            Request request = getRequest(text);
//            获取请求信息
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = null;
                if (response.body() != null) {
                    if (GeminiUtils.OPTION == 1) {
                        body = response.body().string().trim();
                    } else {
                        // 解析 JSON 字符串
                        body = xiaKeParseResponse(response);
                    }
                }
                return StringUtils.isNotBlank(body) ? body : null;
            } else {
                log.error("Gemini请求失败：" + response.body().string());
//                log.error("Gemini请求失败：" + JSON.parseObject(response.body().string()).get("message"));
//                throw new ServiceException(response.message());
            }

        } catch (Exception e) {
            log.error("Gemini请求异常：" + e.getMessage());
            e.printStackTrace();
//            throw new ServiceException(e.getMessage());
        }
        return null;
    }

    /**
     * 异步请求
     *
     * @param text     请求文本
     * @param callback 回调
     */
    public static void chat(String text, Callback callback) {
        Request request = getRequest(text);
        // 发送异步请求
        client.newCall(request).enqueue(callback);
    }

    /**
     * 案例正文修正
     *
     * @param text 原案例文本
     * @return String 修正后的正文
     */
    public static String caseRevise(String text) {
        return syncGetResponse(PromptTemplate.CASEREVISE.getTemplate() + text);
    }

    /**
     * 法条正文修正
     *
     * @param text 原法条文本
     * @return String 修正后的正文
     */
    public static String lawRevise(String text) {
        return syncGetResponse(PromptTemplate.LAWREVISE.getTemplate() + text);
    }

    /**
     * 解析司法案例挖掘案例信息
     *
     * @param text 源文本
     * @return JsonString 解析后案例信息对象
     */
    public static String caseParse(String text) {
        text = PromptTemplate.CASEEXTRA.getTemplate() + text;
        return getJsonStrResponse(text);
    }

    /**
     * 解析法律法规挖掘语义信息
     *
     * @param text 源文本
     * @return JsonString 解析后法条信息对象
     */
    public static String lawParse(String text) {
        text = PromptTemplate.LAWEXTRA.getTemplate() + text;
        return getJsonStrResponse(text);
    }

    /**
     * 创建请求
     *
     * @param text 源文本
     * @return Request 请求对象
     */
    private static Request getRequest(String text) {
        Map<String, String> jsonRequest = getJsonRequest(GeminiUtils.OPTION, text);
        log.info("请求体内容: " + jsonRequest);
        // 创建RequestBody
        String bodyContent = jsonRequest.get("text");
        RequestBody body = RequestBody.create(bodyContent, MediaType.parse("application/json"));
        // 创建Request
        if (GeminiUtils.OPTION == 1) {
            return new Request.Builder()
                .url(jsonRequest.get("url"))
                .post(body)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36")
                .build();
        } else {
            return new Request.Builder()
                .url(jsonRequest.get("url"))
                .post(body)
                .addHeader("Authorization", GPTConstants.GEMINI_XIAKE_API_SECRET)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.51 Safari/537.36")
                .addHeader("Host", "chat.xiake.pro")
                .build();
        }
    }

    private static Map<String, String> getJsonRequest(int option, String text) {
        if (option == 1) {
            return new HashMap<String, String>(2) {
                {
                    put("url", GPTConstants.GEMINI_API_URL);
                    put("text", officialRequest(text));
                }
            };
        } else {
            return new HashMap<String, String>(2) {
                {
                    put("url", GPTConstants.GEMINI_XIAKE_API_URL);
                    put("text", xiaKeRequest(text));
                }
            };
        }
    }

    private static String xiaKeRequest(String text) {
        GeminiGPTBo geminiGPTBo = new GeminiGPTBo();
        geminiGPTBo.setContents(PromptTemplate.CASEEXTRA.getPrompt(), text);
        return JSON.toJSONString(geminiGPTBo);
    }

    private static String officialRequest(String text) {
        long timeStamp = Instant.now().toEpochMilli();
        AuthPayload payload = new AuthPayload(timeStamp, text);
        String signature = generateSignature(payload, "");

        JSONObject msgObj = new JSONObject();
        msgObj.put("text", text);
        JSONArray parts = new JSONArray();
        parts.add(msgObj);
        JSONArray messageLs = new JSONArray();
        JSONObject messageObj = new JSONObject();
        messageObj.put("role", "user");
        messageObj.put("parts", parts);
        messageLs.add(messageObj);

        JSONObject requestBody = new JSONObject();
        requestBody.put("messages", messageLs);
        requestBody.put("time", timeStamp);
        requestBody.put("pass", null);
        requestBody.put("sign", signature);
        return requestBody.toJSONString();
    }

    /**
     * 获取JSON字符串响应
     *
     * @param text 源文本
     * @return String 请求结果
     */
    private static String getJsonStrResponse(String text) {
        Request request = getRequest(text);
        try {
            // 执行同步请求
            Response response = client.newCall(request).execute();
            // 处理响应
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    String body;
                    if (GeminiUtils.OPTION == 1) {
                        body = response.body().string().trim();
                    } else {
                        // 解析 JSON 字符串
                        body = xiaKeParseResponse(response);
                    }
                    if (StringUtils.isNotEmpty(body)) {
                        if (body.contains("json")) {
                            body = body.substring(body.indexOf("{"), body.lastIndexOf("}") + 1);
                        }
                        JSONObject jsonObject = JSONObject.parseObject(body);
                        log.info(jsonObject.toJSONString());
//                        必须转化为json字符串才可以
                        return jsonObject.toJSONString();
                    }
                } else {
                    return null;
                }
            } else {
                log.error("Gemini请求失败: " + response.code() + "：" + response.message());
            }
        } catch (Exception e) {
            log.error("Gemini请求异常: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static String xiaKeParseResponse(Response response) throws IOException {
        // 这里挖掘报错,response.body().string()只能读取一次
        String responseBody = response.body().string();
//        log.info(responseBody);
        JSONObject resObj = JSON.parseObject(responseBody);
        JSONArray candidates = (JSONArray) resObj.get("candidates");
        JSONObject obj = (JSONObject) candidates.get(0);
        obj = (JSONObject) obj.get("content");
        JSONArray array = (JSONArray) obj.get("parts");
        obj = (JSONObject) array.get(0);
//         获取文本部分
        return (String) obj.get("text");
    }

    /**
     * 生成接口校验签名
     *
     * @param payload   载荷内容
     * @param secretKey 密钥
     * @return
     */
    private static String generateSignature(AuthPayload payload, String secretKey) {
        String timestamp = String.valueOf(payload.getT());
        String lastMessage = payload.getM();
        String signText = timestamp + ":" + lastMessage + ":" + secretKey;
        return digestMessage(signText);
    }

    /**
     * 生成签名
     *
     * @param message 要加密的消息
     * @return String 加密消息
     */
    private static String digestMessage(String message) {
        try {
            // 使用SHA-256算法
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            // 转换为十六进制字符串
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * 将字节数组转换为十六进制字符串
     *
     * @param bytes 字节数组
     * @return String 十六进制字符串
     */
    private static String bytesToHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    @Test
    public void test() {
//        String caseRes = caseRevise("+ \"(2023)辽0106财保154号申请人郭某某1委托代理人王某某1被申请人袁某某1申请人于2023年2月16日向本院申请财产保全，请求查封、扣押、冻结被申请人价值人民币*元的财产并提供保险公司诉讼财产保全责任保险保单保函作为担保。本院认为，申请人的申请符合法律规定，依照《中华人民共和国民事诉讼法》第一百零四条、一百零五条、第一百零六条之规定，裁定如下：依法冻结被申请人名下银行存款*元或查封、扣押同等价值的其他财产；申请人在人民法院采取保全措施后三十日内不依法提起诉讼或者申请仲裁的，本院将依法解除保全。本裁定立即执行。如不服本裁定，可以自收到在裁定书之日起五日内向本院申请复议一次，复议期间不停止本裁定的执行。审判员王洋二〇二三年二月一十六日法官助理殷健书记员鲍希秀");
//        log.info(caseRes);
//        log.info(chat("今天是2024-2-6，还有多久春节过年？"));
        String lawRes = lawParse("食盐加碘消除碘缺乏危害管理条例\n" +
            "  \n" +
            " (1994年8月23日中华人民共和国国务院令第163号发布　根据2017年3月1日《国务院关于修改和废止部分行政法规的决定》修订)\n" +
            " 第一章　总则\n" +
            " 第一条　为了消除碘缺乏危害，保护公民身体健康，制定本条例。\n" +
            " 第二条　碘缺乏危害，是指由于环境缺碘、公民摄碘不足所引起的地方性甲状腺肿、地方性克汀病和对儿童智力发育的潜在性损伤。\n" +
            " 第三条　国家对消除碘缺乏危害，采取长期供应加碘食盐(以下简称碘盐)为主的综合防治措施。\n" +
            " 第四条　国务院卫生行政部门负责碘缺乏危害防治和碘盐的卫生监督管理工作；国务院授权的盐业主管机构(以下简称国务院盐业主管机构)负责全国碘盐加工、市场供应的监督管理工作。\n" +
            " 第五条　各级人民政府应当将食盐加碘消除碘缺乏危害的工作纳入本地区国民经济和社会发展计划，并组织实施。\n" +
            " 县级以上人民政府有关部门应当按照职责分工，密切配合，共同做好食盐加碘消除碘缺乏危害工作。\n" +
            " 第六条　国家鼓励和支持在食盐加碘消除碘缺乏危害方面的科学研究和先进技术推广工作。\n" +
            " 对在食盐加碘消除碘缺乏危害工作中做出显著成绩的单位和个人，给予奖励。\n" +
            " 第二章　碘盐的加工、运输和储存\n" +
            " 第七条　从事碘盐加工的盐业企业，应当由省、自治区、直辖市人民政府盐业主管机构指定，并取得同级人民政府卫生行政部门卫生许可后，报国务院盐业主管机构批准。\n" +
            " 第八条　用于加工碘盐的食盐和碘酸钾必须符合国家卫生标准。\n" +
            " 碘盐中碘酸钾的加入量由国务院卫生行政部门确定。\n" +
            " 第九条　碘盐出厂前必须经质量检验，未达到规定含量标准的碘盐不得出厂。\n" +
            " 第十条　碘盐出厂前必须予以包装。碘盐的包装应当有明显标识，并附有加工企业名称、地址、加碘量、批号、生产日期和保管方法等说明。\n" +
            " 第十一条　碘盐为国家重点运输物资。铁路、交通部门必须依照省、自治区、直辖市人民政府盐业主管机构报送的年度、月度运输计划，及时运送。\n" +
            " 碘盐的运输工具和装卸工具，必须符合卫生要求，不得与有毒、有害物质同载、混放。\n" +
            " 第十二条　经营碘盐批发业务的企业和在交通不方便的地区经营碘盐零售业务的单位和个人，应当按照省、自治区、直辖市人民政府盐业主管机构的规定，保持合理的碘盐库存量。\n" +
            " 碘盐和非碘盐在储存场地应当分库或者分垛存放，做到防晒、干燥、安全、卫生。\n" +
            " 第十三条　碘剂的购置费用以及盐业企业因加碘而发生的各种费用，按照国家有关规定执行。\n" +
            " 第三章　碘盐的供应\n" +
            " 第十四条　省、自治区、直辖市人民政府卫生行政部门负责划定碘缺乏地区(以下简称缺碘地区)范围，经本级人民政府批准后，报国务院卫生行政部门、国务院盐业主管机构备案。\n" +
            " 第十五条　国家优先保证缺碘地区居民的碘盐供应；除高碘地区外，逐步实施向全民供应碘盐。\n" +
            " 对于经济区域和行政区域不一致的缺碘地区，应当按照盐业运销渠道组织碘盐的供应。\n" +
            " 在缺碘地区产生、销售的食品和副食品，凡需添加食用盐的，必须使用碘盐。\n" +
            " 第十六条　在缺碘地区销售的碘盐必须达到规定的含碘量，禁止非碘盐和不合格碘盐进入缺碘地区食用盐市场。\n" +
            " 对暂时不能供应碘盐的缺碘地区，经省、自治区、直辖市人民政府批准，可以暂时供应非碘盐；但是，省、自治区、直辖市人民政府卫生行政部门应当采取其他补碘的防治措施。\n" +
            " 对缺碘地区季节性家庭工业、农业、副业、建筑业所需的非碘盐和非食用盐，由县级以上人民政府盐业主管机构组织供应。\n" +
            " 第十七条　经营碘盐批发业务的企业，由省、自治区、直辖市人民政府盐业主管机构审批。\n" +
            " 碘盐批发企业应当从国务院盐业主管机构批准的碘盐加工企业进货。经营碘盐零售业务的单位和个人，应当从碘盐批发企业进货，不得从未经批准的单位和个人购进碘盐。\n" +
            " 第十八条　碘盐批发企业在从碘盐加工企业购进碘盐时，应当索取加碘证明，碘盐加工企业应当保证提供。\n" +
            " 第十九条　碘盐零售单位销售的碘盐应当为小包装，并应当符合本条例的有关规定。碘盐零售的管理办法由省、自治区、直辖市人民政府根据实际情况制定。\n" +
            " 第二十条　为防治疾病，在碘盐中同时添加其他营养强化剂的，应当符合《中华人民共和国食品安全法》的相关规定，并标明销售范围。\n" +
            " 因治疗疾病，不宜食用碘盐的，应当持当地县级人民政府卫生行政部门指定的医疗机构出具的证明，到当地人民政府盐业主管机构指定的单位购买非碘盐。\n" +
            " 第四章　监督和管理\n" +
            " 第二十一条　县级以上地方各级人民政府卫生行政部门负责对本地区食盐加碘消除碘缺乏危害的卫生监督和碘盐的卫生监督以及防治效果评估；县级以上地方各级人民政府盐业主管机构负责对本地区碘盐加工、市场供应的监督管理。\n" +
            " 第二十二条　县级以上各级人民政府卫生行政部门有权按照国家规定，向碘酸钾生产企业和碘盐加工、经营单位抽检样品，索取与卫生监测有关的资料，任何单位和个人不得拒绝、隐瞒或者提供虚假资料。\n" +
            " 第二十三条　卫生监督人员在实施卫生监督、监测时，应当主动出示卫生行政部门制发的监督证件；盐政人员在执行职务时，应当主动出示盐业主管机构制发的证件。\n" +
            " 第五章　罚则\n" +
            " 第二十四条　违反本条例的规定，擅自开办碘盐加工企业或者未经批准从事碘盐批发业务的，由县级以上人民政府盐业主管机构责令停止加工或者批发碘盐，没收全部碘盐和违法所得，可以并处该盐产品价值3倍以下的罚款。\n" +
            " 第二十五条　碘盐的加工企业、批发企业违反本条例的规定，加工、批发不合格碘盐的，由县级以上人民政府盐业主管机构责令停止出售并责令责任者按照国家规定标准对食盐补碘，没收违法所得，可以并处该盐产品价值3倍以下的罚款。情节严重的，对加工企业，由省、自治区、直辖市人民政府盐业主管机构报请国务院盐业主管机构批准后，取消其碘盐加工资格；对批发企业，由省、自治区、直辖市人民政府盐业主管机构取消其碘盐批发资格。\n" +
            " 第二十六条　违反本条例的规定，在缺碘地区的食用盐市场销售不合格碘盐或者擅自销售非碘盐的，由县级以上人民政府盐业主管机构没收其经营的全部盐产品和违法所得，可以并处该盐产品价值3倍以下的罚款；情节严重，构成犯罪的，依法追究刑事责任。\n" +
            " 第二十七条　违反本条例的规定，在碘盐的加工、运输、经营过程中不符合国家卫生标准的，由县级以上人民政府卫生行政部门责令责任者改正，可以并处该盐产品价值3倍以下的罚款。\n" +
            " 第二十八条　违反本条例的规定，出厂碘盐未予包装或者包装不符合国家卫生标准的，由县级以上人民政府卫生行政部门责令改正，可以并处该盐产品价值3倍以下的罚款。\n" +
            " 第二十九条　违反本条例的规定，在缺碘地区生产、销售的食品和副食品中添加非碘盐的，由县级以上人民政府卫生行政部门责令改正，没收违法所得，可以并处该产品价值1倍以下的罚款。\n" +
            " 第六章　附则\n" +
            " 第三十条　畜牧用盐适用本条例。\n" +
            " 第三十一条　省、自治区、直辖市人民政府可以根据本条例制定实施办法。\n" +
            " 第三十二条　经省、自治区、直辖市人民政府卫生行政部门、盐业主管机构确定为应当供应碘盐的非缺碘地区适用本条例第十五条第二款、第三款和第十六条第一款、第三款的规定。\n" +
            " 第三十三条　本条例自1994年10月1日起施行。1979年12月21日国务院批转的《食盐加碘防治地方性甲状腺肿暂行办法》同时废止。");
        log.info(lawRes);

    }

    /**
     * 载荷内部类
     */
    @Getter
    public static class AuthPayload {
        private final long t;
        private final String m;

        public AuthPayload(long timestamp, String message) {
            this.t = timestamp;
            this.m = message;
        }

    }
}
