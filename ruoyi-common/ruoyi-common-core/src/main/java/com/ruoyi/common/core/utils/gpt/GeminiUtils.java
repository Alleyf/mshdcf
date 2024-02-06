package com.ruoyi.common.core.utils.gpt;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.enums.PromptTemplate;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.utils.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Formatter;


/**
 * @author fcs
 * @date 2024/2/5 20:49
 * @site <a href="https://alleyf.github.io">getHelp</a>
 * @description
 */
@Slf4j
public class GeminiUtils {

    //    public static final String GEMINI_API_URL = "https://gemini-pro-chat-iota-silk.vercel.app/api/generate";

    /**
     * 请求地址
     */
    public static final String GEMINI_API_URL = "https://gemini.gxnas.eu.org/api/generate";
    /**
     * 代理地址
     */
    public static final String PROXY_HOST = "127.0.0.1";
    /**
     * 代理端口
     */
    public static final int PROXY_PORT = 7890;
    /**
     * 代理
     */
    public static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
    /**
     * 创建OkHttpClient时设置代理
     */
    public static OkHttpClient client = new OkHttpClient.Builder().proxy(proxy).build();

//    public static void main(String[] args) {
//        String chat = caseParse("+ \"(2023)辽0106财保154号申请人郭某某1委托代理人王某某1被申请人袁某某1申请人于2023年2月16日向本院申请财产保全，请求查封、扣押、冻结被申请人价值人民币*元的财产并提供保险公司诉讼财产保全责任保险保单保函作为担保。本院认为，申请人的申请符合法律规定，依照《中华人民共和国民事诉讼法》第一百零四条、一百零五条、第一百零六条之规定，裁定如下：依法冻结被申请人名下银行存款*元或查封、扣押同等价值的其他财产；申请人在人民法院采取保全措施后三十日内不依法提起诉讼或者申请仲裁的，本院将依法解除保全。本裁定立即执行。如不服本裁定，可以自收到在裁定书之日起五日内向本院申请复议一次，复议期间不停止本裁定的执行。审判员王洋二〇二三年二月一十六日法官助理殷健书记员鲍希秀");
//        log.info(chat);
////        log.info(chat("今天是2024-2-6，还有多久春节过年？"));
//
//    }


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
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String body = response.body().string();
                return StringUtils.isNotEmpty(body) ? body : "";
            } else {
                log.error("Gemini请求失败：" + response.message());
                throw new ServiceException(response.message());
            }

        } catch (IOException e) {
            log.error("Gemini请求失败：" + e.getMessage());
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        }
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
     * 解析司法案例挖掘案例信息
     *
     * @param text 源文本
     * @return JSONObject 解析后案例信息对象
     */
    public static String caseParse(String text) {

        text = PromptTemplate.CASE.getTemplate() + text;

        // 创建JSON请求体封装请求
        Request request = getRequest(text);
        try {
            // 执行同步请求
            Response response = client.newCall(request).execute();
            // 处理响应
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    String res = response.body().string().trim();
                    if (StringUtils.isNotEmpty(res)) {
                        JSONObject jsonObject = JSONObject.parseObject(res);
                        log.info(jsonObject.toJSONString());
//                        必须转化为json字符串才可以
                        return jsonObject.toJSONString();
                    }
                } else {
                    return null;
                }
            } else {
                log.error("请求失败: " + response.code());
            }
        } catch (IOException e) {
            log.error("Gemini请求失败: " + e.getMessage());
            e.printStackTrace();
            throw new ServiceException("Gemini请求失败: " + e.getMessage());
        }
        return null;
    }

    /**
     * 创建请求
     *
     * @param text 源文本
     * @return Request 请求对象
     */
    private static Request getRequest(String text) {
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
        String jsonRequest = requestBody.toJSONString();
        log.info("请求体内容: " + jsonRequest);
        // 创建RequestBody
        RequestBody body = RequestBody.create(jsonRequest, MediaType.get("application/json; charset=utf-8"));
        // 创建Request
        return new Request.Builder()
            .url(GEMINI_API_URL)
            .post(body)
            .build();
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

    /**
     * 解析法律法规挖掘语义信息
     *
     * @param content 源文本
     * @return JSONObject 解析后法条信息对象
     */
    public static String lawParse(String content) {
        return null;
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
