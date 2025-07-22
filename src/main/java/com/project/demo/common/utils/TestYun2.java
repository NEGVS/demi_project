package com.project.demo.common.utils;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

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
public class TestYun2 {
    /**
     * 使用方式 题目链接，题目标题（可选）
     * 修改 264 行内容，从页面复制Cookie
     */
    public static void main(String[] args) {
        test("https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.question_list/1.0.0?exam_id=ex_676bd67c5663d_vhd8TSHo&uexam_id=uexam_676e44cc3200a_R0fFkxLDEC&is_analysis_page=1", "终极考试");
    }

    public static void test(String url, String title)
    {
        System.out.println(title);
        List<String> optionAnswer = new ArrayList<>();
        optionAnswer.add("A");
        optionAnswer.add("B");
        optionAnswer.add("C");
        optionAnswer.add("D");
        optionAnswer.add("E");
        optionAnswer.add("F");
        optionAnswer.add("G");
        String data = doGet(url, null);
        // 解析json数据
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(data);
            // 获取课程章节作业列表
            JsonNode dataNode = rootNode.path("data");
            JsonNode questionList = dataNode.path("question_list");
            // 问题
            for (JsonNode jsonNode : questionList) {
                String sort = jsonNode.path("sort").asText();
                JsonNode childInfo = jsonNode.path("child_info");
                // 问题ID
                String question_id = jsonNode.path("id").asText();
                // 问题正确答案集合
                List<String> temp_optionAnswer = new ArrayList<>();

                List<String> temp_answer = new ArrayList<>();
                // 子问题
                if (!childInfo.isEmpty()) {
                    int index = 1;
                    // 子问题循环
                    for (JsonNode node : childInfo) {
                        String question_detail_id = node.path("id").asText();
//                        System.out.println("题目序号: " + (sort + "-" + index) + " ,题目ID: " + question_detail_id);
                        System.out.println("题目序号: " + (sort + "-" + index) );
                        System.out.print("答案：");
                        index++;

                        JsonNode option = node.path("option");
                        Map<String,String> map = new LinkedHashMap<>();
                        int index1 = 0;
                        // 遍历选项
                        for (JsonNode node1 : option) {
                            String id = node1.path("id").asText();
                            String s = optionAnswer.get(index1);
                            map.put(id, s);
                            index1++;
                        }
                        // 遍历正确答案
                        JsonNode answer = node.path("correct_answer");
                        List<String> temp_detail_optionAnswer = new ArrayList<>();
                        List<String> temp_detail_answer = new ArrayList<>();
                        for (JsonNode node2 : answer) {
                            // 整理答案
                            for (JsonNode jsonNode1 : node2) {
                                String id = jsonNode1.asText();
                                String awner = map.get(id);
                                // 输出答案
                                System.out.print(awner);
                                if (!temp_detail_optionAnswer.contains(id))
                                    temp_detail_optionAnswer.add(id);
                                if (!temp_detail_answer.contains(awner))
                                    temp_detail_answer.add(awner);
                            }
                        }
                        System.out.println();
//                        // 一个问题的答案集合
//                        System.out.println(JSONUtil.toJsonStr(stringObjectMap));
//                        // 问题的答案选项
//                        System.out.println(JSONUtil.toJsonStr(temp_detail_answer));
//                        doGet("https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.question_detail/1.0.0?exam_id=&uexam_id=uexam_674ec8cc1ee3f_WqzCkpxnNR&qid=" + question_detail_id,null);
//                        // 去请求保存答案
//                        doPostJsonToken("https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.commit_qs/1.0.0",JSONUtil.toJsonStr(stringObjectMap));
                    }
                }else {
                    JsonNode option = jsonNode.path("option");
                    Map<String,String> map = new LinkedHashMap<>();
                    int index = 0;
                    for (JsonNode node : option) {
                        String id = node.path("id").asText();
                        String s = optionAnswer.get(index);
                        map.put(id, s);
                        index++;
                    }
                    JsonNode answer = jsonNode.path("correct_answer");
//                    System.out.println("题目序号: " + sort + " ,题目ID: " + question_id);
                    System.out.println("题目序号: " + sort );
                    System.out.print("答案：");
                    for (JsonNode node : answer) {
                        for (JsonNode jsonNode1 : node) {
                            String id = jsonNode1.asText();
                            String awner = map.get(id);
                            // 输出答案
                            System.out.print(awner);
                            temp_optionAnswer.add(id);
                            temp_answer.add(awner);
                        }
                    }
                    System.out.println();
//                    // 一个问题的答案集合
//                    System.out.println(JSONUtil.toJsonStr(stringObjectMap));
//                    // 问题的答案选项
//                    System.out.println(JSONUtil.toJsonStr(temp_answer));
//                    doGet("https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.question_detail/1.0.0?exam_id=&uexam_id=uexam_674ec8cc1ee3f_WqzCkpxnNR&qid=" + question_id,null);
//                    doPostJsonToken("https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.commit_qs/1.0.0",JSONUtil.toJsonStr(stringObjectMap));
                }

            }
//            Map<String , Object> resultMap = new LinkedHashMap<>();
//            resultMap.put("exam_id" , "ex_670a1abadeb68_yL83Kqko");
//            resultMap.put("participate_id" , "uexam_674ec8cc1ee3f_WqzCkpxnNR");
            // 用户ID
//            resultMap.put("user_id" , "u_wework_647d3bd971e5d_Dff12dBr5WK8mSdf");
//            resultMap.put("product_id" , "");
//            doPostJsonToken("https://appuicj0pg96394.pc.xiaoe-tech.com/xe.exam.commit/",JSONUtil.toJsonStr(resultMap));
        } catch (Exception e) {
            System.err.println("解析失败");
        }
    }

    @NotNull
    private static Map<String, Object> getStringObjectMap(List<String> optionAnswer,String querstion_id) {
        Map<String , Object> resultMap = new LinkedHashMap<>();
        resultMap.put("exam_id" , "ex_670a1abadeb68_yL83Kqko");
        resultMap.put("uexam_id" , "uexam_674ec8cc1ee3f_WqzCkpxnNR");

        Map<String, Object> answersMap = new LinkedHashMap<>();
        // 问题ID
        answersMap.put("question_id",querstion_id);
        answersMap.put("answer", optionAnswer);
        answersMap.put("audio_urls",new ArrayList<>());
        answersMap.put("img_urls",new ArrayList<>());

        resultMap.put("answers" , answersMap);
        return resultMap;
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
                    if (!paramBuilder.isEmpty()) {
                        paramBuilder.append("&");
                    }
                    paramBuilder.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
                    paramBuilder.append("=");
                    paramBuilder.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                }
            }

            // 创建Http GET请求
            String fullUrl = url + (!paramBuilder.isEmpty() ? "?" + paramBuilder.toString() : "");
            HttpGet httpGet = new HttpGet(fullUrl);
            httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
            // 设置 cookie
            httpGet.addHeader("Cookie", "XIAOEID=d426c31df24613bceed17c782e7a6a40; shop_version_type=171; anonymous_user_key=dV9hbm9ueW1vdXNfNjc2ZTQzOTZiODFmM19UQlJHNFNXT2pI; LANGUAGE_appuicj0pg96394=cn; sensorsdata2015jssdkcross=%7B%22%24device_id%22%3A%2219406b81097f72-03e91cd7051a39-4c657b58-1327104-19406b810985fb%22%7D; sajssdk_2015_new_user_appuicj0pg96394_pc_xiaoe-tech_com=1; channel=2000; cookie_channel=2000; cookie_referer=https%3A%2F%2Flogin.work.weixin.qq.com%2F; cookie_session_id=61710c02110d01e5d909d7511c8b1f6c; pc_user_key=3415a21312b816d9bef5945ecade0617; xenbyfpfUnhLsdkZbX=0; show_user_icon=1; app_id=\"appuicj0pg96394\"; sa_jssdk_2015_appuicj0pg96394_pc_xiaoe-tech_com=%7B%22distinct_id%22%3A%22u_wework_647d3bd971e5d_Dff12dBr5WK8mSdf%22%2C%22first_id%22%3A%2219406b81097f72-03e91cd7051a39-4c657b58-1327104-19406b810985fb%22%2C%22props%22%3A%7B%22%24latest_traffic_source_type%22%3A%22%E7%9B%B4%E6%8E%A5%E6%B5%81%E9%87%8F%22%2C%22%24latest_search_keyword%22%3A%22%E6%9C%AA%E5%8F%96%E5%88%B0%E5%80%BC_%E7%9B%B4%E6%8E%A5%E6%89%93%E5%BC%80%22%2C%22%24latest_referrer%22%3A%22%22%7D%7D; appId=\"appuicj0pg96394\"; userInfo={\"app_id\":\"appuicj0pg96394\",\"birth\":null,\"can_modify_phone\":true,\"universal_union_id\":\"oTHW5v1LidcKn-MVbgKpbSVGwYfY\",\"user_id\":\"u_wework_647d3bd971e5d_Dff12dBr5WK8mSdf\",\"wx_account\":\"\",\"wx_avatar\":\"https://wework.qpic.cn/wwpic3az/937186_1r0Vq7StTfuf19R_1702610055/0\",\"wx_gender\":0,\"phone\":null,\"pc_user_key\":\"3415a21312b816d9bef5945ecade0617\",\"permission_visit\":0,\"permission_comment\":0,\"permission_buy\":0,\"pwd_isset\":false,\"channels\":[{\"type\":\"wechat\",\"active\":1},{\"type\":\"qq\",\"active\":0}],\"area_code\":\"86\"}; shopInfo={\"base\":{\"shop_id\":\"appuicj0pg96394\",\"merchant_id\":\"mchlwtXbFgVyzVA\",\"shop_name\":\"ä»\u0081è\u0081”äº‘è¯¾å ‚\",\"shop_logo\":\"https://wechatapppro-1252524126.file.myqcloud.com/appuicj0pg96394/image/b_u_clbdh05ophndf2abjjng/206qhtlq1pskus.png\",\"main_type\":null,\"use_https\":0,\"footer_logo\":\"\",\"profile\":\"\",\"use_collection\":1,\"wx_app_type\":1,\"create_time\":\"2023-01-28 09:57:03\",\"extra\":\"\",\"check_name\":null,\"shop_tag\":0,\"is_authorized\":0,\"platform_key\":\"training\"},\"domain\":{\"h5_url\":\"https://appuicj0pg96394.h5.xiaoeknow.com\",\"pc_common_url\":\"https://appuicj0pg96394.pc.xiaoe-tech.com\",\"pc_custom_url\":\"\",\"shop_id\":\"appuicj0pg96394\",\"h5_url_ver\":0,\"pc_custom_domain\":\"\",\"pc_cname_domain\":\"appuicj0pg96394.pc-cname.xiaoe-tech.com\"},\"pc\":{\"shop_id\":\"appuicj0pg96394\",\"is_valid\":1,\"is_enable\":1,\"enabled_at\":\"1674871024\",\"pc_shop_logo\":\"https://wechatapppro-1252524126.file.myqcloud.com/appuicj0pg96394/image/b_u_clbdh05ophndf2abjjng/4faxsnlqg9khj8.png\",\"record\":\"\",\"gwab_code\":\"å·¥å•†ç½‘ç›‘ ç”µå\u00AD\u0090æ ‡è¯†\",\"gwab_link\":\"\",\"gswj_link\":\"\",\"micro_page_seo\":null,\"content_page_seo\":null,\"record_number\":null,\"record_number_url\":null,\"cert_expires_at\":\"\",\"hover_menu_status\":0,\"hover_menu_config\":null,\"enable_un_login_menu\":0,\"enable_un_login_try_course\":0,\"enable_un_login_free_course\":0,\"enable_course_detail_module\":0,\"language\":\"cn\",\"is_without_auth\":1},\"version\":{\"shop_id\":\"appuicj0pg96394\",\"version_type\":171,\"expire_time\":\"2025-04-11 19:17:03\",\"pre_version\":0,\"rights_type\":0},\"status\":{\"shop_id\":\"appuicj0pg96394\",\"is_sealed\":0,\"is_expired\":0,\"sealed_reason\":\"\"},\"live\":{\"shop_id\":\"appuicj0pg96394\",\"performer_avatar\":\"https://commonresource-1252524126.cdn.xiaoeknow.com/image/m3r55tc00o1k.png?imageView2/2/q/60/w/160/format/webp%7CimageMogr2/ignore-error/1\",\"performer_name\":\"ç›´æ’\u00ADåŠ©æ‰‹\",\"live_introduction\":\"æ¬¢è¿Žè¿›å…¥ç›´æ’\u00ADé—´ï¼š\\n1ã€\u0081è¯·è‡ªè¡Œè°ƒèŠ‚æ‰‹æœºéŸ³é‡\u008Fè‡³å\u0090ˆé€‚çš„çŠ¶æ€\u0081ã€‚\\n2ã€\u0081ç›´æ’\u00ADç•Œé\u009D¢æ˜¾ç¤ºè®²å¸ˆå\u008F‘å¸ƒçš„å†…å®¹ï¼Œå\u0090¬ä¼—å\u008F‘è¨€å\u008F¯ä»¥åœ¨è®¨è®ºåŒºè¿›è¡Œæˆ–ä»¥å¼¹å¹•å½¢å¼\u008FæŸ¥çœ‹ã€‚\",\"is_watermark\":null,\"watermark_id\":\"\",\"watermark_url\":\"\",\"watermark_location\":null,\"watermark_glass\":null,\"watermark_size\":null,\"show_reward\":\"\",\"screen_record_cfg\":\"{\\\"font_color\\\":\\\"#FFFFFF\\\",\\\"font_size\\\":\\\"12px\\\",\\\"transparency\\\":\\\"1\\\",\\\"desc\\\":\\\"\\\",\\\"desc_switch\\\":0,\\\"switch\\\":1}\",\"is_default\":0,\"enable_web_rtc\":1,\"danmu\":null,\"show_live_in_e_mini\":1,\"is_privacy_protection\":0,\"watermark_position_x\":0,\"watermark_position_y\":0,\"watermark_width\":0,\"watermark_height\":0,\"watermark_processed_url\":\"\",\"is_open_msg_bubble\":1,\"msg_bubble_type\":1,\"is_jump_full_screen\":1,\"horse_race_lamp\":0},\"footer\":{\"id\":6783,\"app_id\":\"appuicj0pg96394\",\"template_code\":\"default\",\"data\":{\"copyright\":\"ä»\u0081è\u0081”é›†å›¢\"},\"in_use\":1,\"created_at\":\"2023-12-25 17:24:29\",\"updated_at\":\"2023-12-25 17:24:29\"},\"seo\":{\"default_seo_title\":\"ä»\u0081è\u0081”äº‘è¯¾å ‚\",\"default_seo_description\":\"è¯šä¿¡ åˆ›æ–° ç®€å\u008D• å¿«ä¹\u0090 åˆ©ä»– å\u0090‘å†…æ±‚\"},\"version_type\":171,\"expire_time\":\"2025-04-11 19:17:03\",\"expire_day\":105,\"shop_type\":\"1\",\"is_web_sdk\":false}");

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
}
