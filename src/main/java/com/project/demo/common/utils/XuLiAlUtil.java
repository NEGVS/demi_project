package com.project.demo.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 蓄力 AL 签到|分享
 */
@Slf4j
public class XuLiAlUtil {

    public static String TOKEN = "9f76274b400cf5bd6e64b3146942186a";
    
    public static String url = "https://ai.aixuli.com/api";

    public static void main(String[] args) throws JsonProcessingException {
        // 签到
        sign(null);
        // 分享链接点击
        click(null);
        click(null);
    }

    public static void sign(String token) {
        doPost(url + "/share/sign", null, StrUtil.isNotBlank(token) ? token : TOKEN);
    }

    public static void click(String token) throws JsonProcessingException {
        Map<String, String> map = new HashMap<>();
        map.put("share_id", share(token));
        String jsonStr = JSONUtil.toJsonStr(map);
        String s = doPostJsonToken(url + "/share/click", jsonStr, StrUtil.isNotBlank(token) ? token : TOKEN);
        log.info(s);
    }

    public static String share(String token) throws JsonProcessingException {
        String result = doPost(url + "/share/share?channel=4", null, StrUtil.isNotBlank(token) ? token : TOKEN);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(result);
        return jsonNode.path("data").path("share_id").asText();
    }

    public static String doPost(String url, Map<String, String> param, String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("token", token);
            // 创建参数列表
            if (param != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (String key : param.keySet()) {
                    paramList.add(new BasicNameValuePair(key, param.get(key)));
                }
                // 模拟表单
                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, StandardCharsets.UTF_8);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");

            log.info("请求返回数据：{}", resultString);
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        return resultString;
    }

    public static String doPostJsonToken(String url, String json, String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("token", token);
            // 创建请求内容
            if (json != null) {
                StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
                httpPost.setEntity(entity);
            }
            // 执行http请求
            response = httpClient.execute(httpPost);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
        } catch (Exception e) {
            log.info(e.getMessage());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.info(e.getMessage());
            }
        }
        return resultString;
    }
}
