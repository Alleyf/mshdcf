package com.ruoyi.common.core.utils.gpt;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.constant.GPTConstants;
import com.ruoyi.common.core.domain.MoonShotBo;
import com.ruoyi.common.core.enums.PromptTemplate;
import com.ruoyi.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * moonShot-gpt（只有15块体验额度，用完即止，谨慎使用）
 *
 * @author fcs
 * @date 2024/2/24 23:42
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description todo 取代GeminiPro
 */
@Slf4j
public class MoonShotGPTUtils {

    /**
     * 代理
     */
    public static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(GPTConstants.PROXY_HOST, GPTConstants.PROXY_PORT));
    /**
     * 创建OkHttpClient时设置代理
     */
    public static OkHttpClient client = new OkHttpClient.Builder()
//            设置代理
//            .proxy(proxy)
        // 设置读取超时时间为60秒
        .readTimeout(60, TimeUnit.SECONDS)
        // 设置写入超时时间为60秒
        .writeTimeout(60, TimeUnit.SECONDS)
        // 设置连接超时时间为60秒
        .connectTimeout(60, TimeUnit.SECONDS)
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
                    // 解析 JSON 字符串
                    body = moonShotParseResponse(response);
                }
                return StringUtils.isNotBlank(body) ? body : null;
            } else {
                log.error("moonShot请求失败：" + response.body().string());
//                log.error("moonShot请求失败：" + JSON.parseObject(response.body().string()).get("message"));
//                throw new ServiceException(response.message());
            }

        } catch (Exception e) {
            log.error("moonShot请求异常：" + e.getMessage());
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
        Map<String, String> jsonRequest = getJsonRequest(text);
        log.info("请求体内容: " + jsonRequest);
        // 创建RequestBody
        RequestBody body = RequestBody.create(jsonRequest.get("text"), MediaType.get("application/json; charset=utf-8"));
        // 创建Request
        return new Request.Builder()
            .url(jsonRequest.get("url"))
            .post(body)
            .addHeader("Authorization", GPTConstants.Local_MoonShot_API_SECRET)
            .addHeader("Content-Type", "application/json")
            .build();
    }

    private static Map<String, String> getJsonRequest(String text) {
        return new HashMap<String, String>(2) {
            {
                put("url", GPTConstants.Local_MoonShot_API_URL);
                put("text", moonShotRequest(text));
            }
        };
    }

    private static String moonShotRequest(String text) {
        MoonShotBo moonShotGPTBo = new MoonShotBo();
        moonShotGPTBo.setContext(PromptTemplate.LAWEXTRA.getPrompt(), text);
        return JSON.toJSONString(moonShotGPTBo);
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
                    // 解析 JSON 字符串
                    body = moonShotParseResponse(response);
                    if (StringUtils.isNotEmpty(body)) {
                        JSONObject jsonObject = JSONObject.parseObject(body);
                        log.info(jsonObject.toJSONString());
//                        必须转化为json字符串才可以
                        return jsonObject.toJSONString();
                    }
                } else {
                    return null;
                }
            } else {
                log.error("moonShot请求失败: " + response.code() + "：" + response.message());
            }
        } catch (Exception e) {
            log.error("moonShot请求异常: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static String moonShotParseResponse(Response response) throws IOException {
        JSONObject resObj = JSON.parseObject(response.body().string().trim());
        log.info(resObj.toJSONString());
        JSONArray choices = (JSONArray) resObj.get("choices");
        JSONObject firstChoice = (JSONObject) choices.get(0);
        JSONObject message = (JSONObject) firstChoice.get("message");
        // 获取文本部分
        return (String) message.get("content");
    }

    @Test
    public void test() {
        String chat = caseRevise("+ \"(2023)辽0106财保154号申请人郭某某1委托代理人王某某1被申请人袁某某1申请人于2023年2月16日向本院申请财产保全，请求查封、扣押、冻结被申请人价值人民币*元的财产并提供保险公司诉讼财产保全责任保险保单保函作为担保。本院认为，申请人的申请符合法律规定，依照《中华人民共和国民事诉讼法》第一百零四条、一百零五条、第一百零六条之规定，裁定如下：依法冻结被申请人名下银行存款*元或查封、扣押同等价值的其他财产；申请人在人民法院采取保全措施后三十日内不依法提起诉讼或者申请仲裁的，本院将依法解除保全。本裁定立即执行。如不服本裁定，可以自收到在裁定书之日起五日内向本院申请复议一次，复议期间不停止本裁定的执行。审判员王洋二〇二三年二月一十六日法官助理殷健书记员鲍希秀");
        log.info(chat);

    }
}
