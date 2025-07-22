package com.project.demo.code.controller;

import com.alibaba.fastjson.JSONObject;
import com.project.demo.common.utils.HttpUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
public class WechatSignatureController {

    @Value("${wechat.appid}")
    private String appId;

    @Value("${wechat.secret}")
    private String appSecret;

    // 缓存Access Token和JSAPI Ticket（这里简化为直接获取，建议使用Redis或内存缓存）
    private String accessToken;
    private String jsapiTicket;
    private long tokenExpireTime;
    private long ticketExpireTime;

    // 获取Access Token
    private String getAccessToken() {
        if (accessToken == null || System.currentTimeMillis() > tokenExpireTime) {
            String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + appId + "&secret=" + appSecret;
            String response = HttpUtil.get(url);
            JSONObject json = JSONObject.parseObject(response);
            accessToken = json.getString("access_token");
            tokenExpireTime = System.currentTimeMillis() + (json.getLong("expires_in") - 200) * 1000; // 提前200秒刷新
        }
        return accessToken;
    }

    // 获取JSAPI Ticket
    private String getJsapiTicket() {
        if (jsapiTicket == null || System.currentTimeMillis() > ticketExpireTime) {
            String accessToken = getAccessToken();
            String url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + accessToken + "&type=jsapi";
            String response = HttpUtil.get(url);
            JSONObject json = JSONObject.parseObject(response);
            jsapiTicket = json.getString("ticket");
            ticketExpireTime = System.currentTimeMillis() + (json.getLong("expires_in") - 200) * 1000; // 提前200秒刷新
        }
        return jsapiTicket;
    }

    // 生成签名
    @GetMapping("/getSignature")
    public Map<String, String> getSignature(@RequestParam String url) {
        String nonceStr = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 16);
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String jsapiTicket = getJsapiTicket();

        // 构造签名字符串
        String signString = "jsapi_ticket=" + jsapiTicket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;

        String signature = sha1(signString);

        Map<String, String> result = new HashMap<>();
        result.put("appId", appId);
        result.put("timestamp", timestamp);
        result.put("nonceStr", nonceStr);
        result.put("signature", signature);
        result.put("url", url);

        return result;
    }

    // SHA1加密
    private String sha1(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.update(data.getBytes(StandardCharsets.UTF_8));
            byte[] hash = digest.digest();
            Formatter formatter = new Formatter();
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

// 简单HTTP工具类
class HttpUtil {
    public static String get(String url) {
        System.out.println("请求URL：" + url);
        System.out.println("响应结果：");
        return HttpUtils.doGet(url);
    }
}
