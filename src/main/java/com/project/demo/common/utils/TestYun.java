package com.project.demo.common.utils;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.poi.ss.formula.functions.T;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TestYun {
    public static void main(String[] args) {

        String url = "https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.question_list/1.0.0?exam_id=ex_670008eb6202c_fy8WPlo7&uexam_id=uexam_672b34162af1f_Xl8fvZsGVW";


//        String data = doGet(url, null);
        // 读取文件内容
        String data = readFileToString("E:\\idea_room\\demo_project\\src\\main\\resources\\tset\\问题.json");

        Map<String, List<String>> result = new LinkedHashMap<>();

        // 解析json数据
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(data);
            // 获取课程章节作业列表
            JsonNode dataNode = rootNode.path("data");
            JsonNode questionList = dataNode.path("question_list");
            for (JsonNode jsonNode : questionList) {
                String id = getId(jsonNode);
                List<String> ids = new ArrayList<>();
                if (result.containsKey(id)) {
                    ids = result.get(id);
                    ids.add(id);
                } else {
                    ids.add(id);
                    result.put(id, ids);
                }
                JsonNode childInfo = jsonNode.path("child_info");
                if (childInfo != null) {
                    if (result.containsKey(id)) {
                        ids = result.get(id);
                        // 解析子题目
                        for (JsonNode child : childInfo) {
                            ids.add(getId(child));
                        }
                    } else {
                        ids.add(id);
                        result.put(id, ids);
                    }
                }
            }
            System.out.println(result.size());

//            System.out.println(JSONUtil.toJsonStr(result.get("qs_66f9100a17a38_gm3jlwfm0")));


//            List<String> urls = new ArrayList<>();
//            for (Map.Entry<String, List<String>> entry : result.entrySet()) {
//                List<String> values = entry.getValue();
//                for (String value : values) {
//                    String detailUrl = "https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.question_detail/1.0.0?exam_id=ex_670008eb6202c_fy8WPlo7&uexam_id=uexam_672b34162af1f_Xl8fvZsGVW&qid=" + value;                    urls.add(detailUrl);
//                }
//            }
//            System.out.println(urls.size());
//
//            List<String> list = new ArrayList<>();
//            for (String oneUrl : urls) {
//                String resultJson = doGet(oneUrl, null);
//                JsonNode jsonNode = objectMapper.readTree(resultJson);
//                analysisProblem(jsonNode, list);
//            }
//            try (FileWriter fileWriter = new FileWriter("output.txt")) {
//                fileWriter.write(JSONUtil.toJsonStr(list));
//                System.out.println("写入成功");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFileToString(String filePath) {
        Path path = Paths.get(filePath);
        String content = "";
        try {
            content = Files.readString(path);
        } catch (IOException e) {
            e.printStackTrace(); // 打印异常堆栈跟踪
        }
        return content;
    }

    public static String getId(JsonNode json) {
        return json.path("id").asText();
    }

    public static void analysisProblem(JsonNode json, List<String> list) {
        Map<String, Object> problemMap = new LinkedHashMap<>();
        JsonNode data = json.path("data");
        String id = data.get("id").asText();
        String content = data.get("content").asText().replace("<p>", "").replace("</p>", "");
        // 解析参考解答
        String analysis = data.path("analysis").asText();

        problemMap.put("id", id);
        problemMap.put("content", content);
        problemMap.put("analysis", analysis);
        // 选项内容
        JsonNode option = data.path("option");
        List<Map<String, String>> answer = new ArrayList<>();
        for (JsonNode jsonNode : option) {
            // 选项内容
            String content1 = jsonNode.get("content").asText();
            // 选项ID
            String answerId = jsonNode.get("id").asText();
            Map<String, String> map = new LinkedHashMap<>();
            // 每一个选项ID
            map.put("answerId", answerId);
            // 每一个选项内容
            map.put("content", content1);
            answer.add(map);
        }
        problemMap.put("option", answer);

        System.out.println(JSONUtil.toJsonStr(problemMap));
        list.add(JSONUtil.toJsonStr(problemMap));
    }


    /**
     * GET请求
     *
     * @param url   请求地址
     * @param param 请求参数
     * @return 响应结果
     */
    public static String doGet(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";

        try {
            // 构建请求参数
            StringBuilder paramBuilder = new StringBuilder();
            if (param != null) {
                for (Map.Entry<String, String> entry : param.entrySet()) {
                    if (paramBuilder.length() != 0) {
                        paramBuilder.append("&");
                    }
                    paramBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                    paramBuilder.append("=");
                    paramBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                }
            }

            // 创建Http GET请求
            String fullUrl = url + (paramBuilder.length() > 0 ? "?" + paramBuilder.toString() : "");
            HttpGet httpGet = new HttpGet(fullUrl);
            httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
            // 设置 cookie
            httpGet.addHeader("Cookie", "XIAOEID=5ebe9df7f12eea2e09ebd0421ccac417; anonymous_user_key=dV9hbm9ueW1vdXNfNjcwZjIzMGI4NjM4NV9EaEJHVGJwdVVL; sensorsdata2015jssdkcross=%7B%22%24device_id%22%3A%221929320e8e1823-069ae73904c47e-4c657b58-2073600-1929320e8e21009%22%7D; sa_jssdk_2015_appuicj0pg96394_pc_xiaoe-tech_com=%7B%22distinct_id%22%3A%22u_wework_647d3bd971e5d_Dff12dBr5WK8mSdf%22%2C%22first_id%22%3A%221929320e8e1823-069ae73904c47e-4c657b58-2073600-1929320e8e21009%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%7D%7D; cookie_channel=2000; cookie_referer=https%3A%2F%2Flogin.work.weixin.qq.com%2F; shop_version_type=171; LANGUAGE_appuicj0pg96394=cn; channel=2000; cookie_session_id=012c4a2ee36b93de310099f91bfcfbb0; pc_user_key=9e980df8c54e208bbe974862f146a276; xenbyfpfUnhLsdkZbX=0; show_user_icon=1; app_id=\"appuicj0pg96394\"; appId=\"appuicj0pg96394\"; userInfo={\"app_id\":\"appuicj0pg96394\",\"birth\":null,\"can_modify_phone\":true,\"universal_union_id\":null,\"user_id\":\"u_wework_647d3bd971e5d_Dff12dBr5WK8mSdf\",\"wx_account\":\"\",\"wx_avatar\":\"https://wework.qpic.cn/wwpic3az/937186_1r0Vq7StTfuf19R_1702610055/0\",\"wx_gender\":0,\"phone\":null,\"pc_user_key\":\"9e980df8c54e208bbe974862f146a276\",\"permission_visit\":0,\"permission_comment\":0,\"permission_buy\":0,\"pwd_isset\":false,\"channels\":[{\"type\":\"wechat\",\"active\":0},{\"type\":\"qq\",\"active\":0}],\"area_code\":\"86\"}; shopInfo={\"base\":{\"shop_id\":\"appuicj0pg96394\",\"merchant_id\":\"mchlwtXbFgVyzVA\",\"shop_name\":\"ä»\u0081è\u0081”äº‘è¯¾å ‚\",\"shop_logo\":\"https://wechatapppro-1252524126.file.myqcloud.com/appuicj0pg96394/image/b_u_clbdh05ophndf2abjjng/206qhtlq1pskus.png\",\"main_type\":null,\"use_https\":0,\"footer_logo\":\"\",\"profile\":\"\",\"use_collection\":1,\"wx_app_type\":1,\"create_time\":\"2023-01-28 09:57:03\",\"extra\":\"\",\"check_name\":null,\"shop_tag\":0,\"is_authorized\":0,\"platform_key\":\"training\"},\"domain\":{\"h5_url\":\"https://appuicj0pg96394.h5.xiaoeknow.com\",\"pc_common_url\":\"https://appuicj0pg96394.pc.xiaoe-tech.com\",\"pc_custom_url\":\"\",\"shop_id\":\"appuicj0pg96394\",\"h5_url_ver\":0,\"pc_custom_domain\":\"\",\"pc_cname_domain\":\"appuicj0pg96394.pc-cname.xiaoe-tech.com\"},\"pc\":{\"shop_id\":\"appuicj0pg96394\",\"is_valid\":1,\"is_enable\":1,\"enabled_at\":\"1674871024\",\"pc_shop_logo\":\"https://wechatapppro-1252524126.file.myqcloud.com/appuicj0pg96394/image/b_u_clbdh05ophndf2abjjng/4faxsnlqg9khj8.png\",\"record\":\"\",\"gwab_code\":\"å·¥å•†ç½‘ç›‘ ç”µå\u00AD\u0090æ ‡è¯†\",\"gwab_link\":\"\",\"gswj_link\":\"\",\"micro_page_seo\":null,\"content_page_seo\":null,\"record_number\":null,\"record_number_url\":null,\"cert_expires_at\":\"\",\"hover_menu_status\":0,\"hover_menu_config\":null,\"enable_un_login_menu\":0,\"enable_un_login_try_course\":0,\"enable_un_login_free_course\":0,\"enable_course_detail_module\":0,\"language\":\"cn\",\"is_without_auth\":1},\"version\":{\"shop_id\":\"appuicj0pg96394\",\"version_type\":171,\"expire_time\":\"2025-04-11 19:17:03\",\"pre_version\":0,\"rights_type\":0},\"status\":{\"shop_id\":\"appuicj0pg96394\",\"is_sealed\":0,\"is_expired\":0,\"sealed_reason\":\"\"},\"live\":{\"shop_id\":\"appuicj0pg96394\",\"performer_avatar\":\"http://wechatapppro-1252524126.file.myqcloud.com/app8WEMULSx3308/image/4a28c83ecad16a0da0468ad0cecb60aa.png\",\"performer_name\":\"å°\u008Fé¹…é€šåŠ©æ‰‹\",\"live_introduction\":\"æ¬¢è¿Žè¿›å…¥ç›´æ’\u00ADé—´ï¼š\\n1ã€\u0081è¯·è‡ªè¡Œè°ƒèŠ‚æ‰‹æœºéŸ³é‡\u008Fè‡³å\u0090ˆé€‚çš„çŠ¶æ€\u0081ã€‚\\n2ã€\u0081ç›´æ’\u00ADç•Œé\u009D¢æ˜¾ç¤ºè®²å¸ˆå\u008F‘å¸ƒçš„å†…å®¹ï¼Œå\u0090¬ä¼—å\u008F‘è¨€å\u008F¯ä»¥åœ¨è®¨è®ºåŒºè¿›è¡Œæˆ–ä»¥å¼¹å¹•å½¢å¼\u008FæŸ¥çœ‹ã€‚\",\"is_watermark\":null,\"watermark_id\":\"\",\"watermark_url\":\"\",\"watermark_location\":null,\"watermark_glass\":null,\"watermark_size\":null,\"show_reward\":\"\",\"screen_record_cfg\":\"{\\\"font_color\\\":\\\"#FFFFFF\\\",\\\"font_size\\\":\\\"12px\\\",\\\"transparency\\\":\\\"1\\\",\\\"desc\\\":\\\"\\\",\\\"desc_switch\\\":0,\\\"switch\\\":1}\",\"is_default\":0,\"enable_web_rtc\":1,\"danmu\":null,\"show_live_in_e_mini\":1,\"is_privacy_protection\":0,\"watermark_position_x\":0,\"watermark_position_y\":0,\"watermark_width\":0,\"watermark_height\":0,\"watermark_processed_url\":\"\",\"is_open_msg_bubble\":1,\"msg_bubble_type\":1,\"is_jump_full_screen\":1,\"horse_race_lamp\":0},\"footer\":{\"id\":6783,\"app_id\":\"appuicj0pg96394\",\"template_code\":\"default\",\"data\":{\"copyright\":\"ä»\u0081è\u0081”é›†å›¢\"},\"in_use\":1,\"created_at\":\"2023-12-25 17:24:29\",\"updated_at\":\"2023-12-25 17:24:29\"},\"seo\":{\"default_seo_title\":\"ä»\u0081è\u0081”äº‘è¯¾å ‚\",\"default_seo_description\":\"è¯šä¿¡ åˆ›æ–° ç®€å\u008D• å¿«ä¹\u0090 åˆ©ä»– å\u0090‘å†…æ±‚\"},\"version_type\":171,\"expire_time\":\"2025-04-11 19:17:03\",\"expire_day\":156,\"shop_type\":\"1\",\"is_web_sdk\":false}");

            // 执行http请求
            response = httpClient.execute(httpGet);
            resultString = EntityUtils.toString(response.getEntity(), "utf-8");
//            log.info("请求返回数据：{}", resultString);
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

    /**
     * post请求
     *
     * @param url   请求地址
     * @param param 请求参数
     */
    public String doPost(String url, Map<String, String> param) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            // 设置 cookie
            httpPost.addHeader("Cookie", "XIAOEID=5ebe9df7f12eea2e09ebd0421ccac417; anonymous_user_key=dV9hbm9ueW1vdXNfNjcwZjIzMGI4NjM4NV9EaEJHVGJwdVVL; sensorsdata2015jssdkcross=%7B%22%24device_id%22%3A%221929320e8e1823-069ae73904c47e-4c657b58-2073600-1929320e8e21009%22%7D; sa_jssdk_2015_appuicj0pg96394_pc_xiaoe-tech_com=%7B%22distinct_id%22%3A%22u_wework_647d3bd971e5d_Dff12dBr5WK8mSdf%22%2C%22first_id%22%3A%221929320e8e1823-069ae73904c47e-4c657b58-2073600-1929320e8e21009%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%7D%7D; shop_version_type=171; LANGUAGE_appuicj0pg96394=cn; cookie_channel=2000; cookie_referer=https%3A%2F%2Flogin.work.weixin.qq.com%2F; pc_user_key=62f5b3ae8cb92e5441f066273872c684; xenbyfpfUnhLsdkZbX=0; show_user_icon=1; userInfo={\"address\":null,\"app_id\":\"appuicj0pg96394\",\"birth\":null,\"can_modify_phone\":true,\"company\":null,\"job\":null,\"universal_union_id\":null,\"user_id\":\"u_wework_647d3bd971e5d_Dff12dBr5WK8mSdf\",\"wx_account\":\"\",\"wx_avatar\":\"https://wework.qpic.cn/wwpic3az/937186_1r0Vq7StTfuf19R_1702610055/0\",\"wx_gender\":0,\"phone\":null,\"pc_user_key\":\"62f5b3ae8cb92e5441f066273872c684\",\"permission_visit\":0,\"permission_comment\":0,\"permission_buy\":0,\"pwd_isset\":false,\"channels\":[{\"type\":\"wechat\",\"active\":0},{\"type\":\"qq\",\"active\":0}],\"area_code\":\"86\"}; app_id=\"appuicj0pg96394\"; channel=homepage; cookie_session_id=4PZzWroclidS9Ver1aAhSEOofz2G79Ig");

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


    public static T test(Class<?> clazz){

        if (clazz instanceof Object) {

        }

        return null;
    }

}
