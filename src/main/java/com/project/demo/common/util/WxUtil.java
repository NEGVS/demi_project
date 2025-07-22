package com.project.demo.common.util;


import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class WxUtil {

    private static final Integer wxRoomId = 7940;

    public static final HashMap<String, String> wxRootMap = new HashMap<>();

    static {
        // userWxId wxid_w8qn6uvcudm522
        wxRootMap.put("7938", "书月");
        wxRootMap.put("7939", "书画");
        wxRootMap.put("7940", "书棋");
        wxRootMap.put("7941", "书柠");
        wxRootMap.put("7942", "书屿");
        wxRootMap.put("7943", "诗意");
        wxRootMap.put("7953", "书栀");
        wxRootMap.put("7980", "书聆");
    }


    public static void main(String[] args) {
        WxUtil wxUtil = new WxUtil();
//        List<String> active = wxUtil.active(7940, 2);
        List<String> dive = wxUtil.corn(7940, 3,1);

//        System.out.println(active);
        System.out.println(dive);
    }

    /**
     * 统计自己的活跃有多少
     *
     * @param wxRoomId 房价号码
     * @param type     1 日活跃  2 月活跃
     * @return 结果
     */
    public Integer activeCount(Integer wxRoomId, Integer type,String userWxIdParam) {
        // 创建支持 Cookie 的 HttpClient
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            // 第一次 GET 请求
            String firstUrl = "https://space.robotcy.cn:18082/#/?wxRoomId=" + wxRoomId;
            HttpGet firstRequest = new HttpGet(firstUrl);
            firstRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            firstRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

//            log.info("发送第一次请求: " + firstUrl);
            try (CloseableHttpResponse response = httpClient.execute(firstRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("第一次请求失败，状态码: " + statusCode);
                }

                // 打印响应头中的 Set-Cookie
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header header : headers) {
//                    log.info("原始 Cookie 头: " + header.getValue());
                }

                // 打印存储的 Cookie
//                log.info("存储的 Cookies: ");
                cookieStore.getCookies().forEach(cookie ->
                        log.info(cookie.getName() + "=" + cookie.getValue())
                );

            }
            // 第二次 POST 请求
            String url = "https://space.robotcy.cn:18082/mini_api/wxRoom/selectRoomUser";
            HttpPost postRequest = new HttpPost(url);

            // 构造 JSON 参数
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("pageSize", 250);
            jsonParams.put("pageNum", 1);
            jsonParams.put("wxRoomId", wxRoomId);
            jsonParams.put("type", 10);
            jsonParams.put("hytype", type);
            postRequest.setEntity(new StringEntity(jsonParams.toString(), "UTF-8"));

            // 设置请求头
            postRequest.setHeader("Content-Type", "application/json;charset=UTF-8");
            postRequest.setHeader("User-Agent", "Mozilla/5.0");

//            log.info("发送请求: " + url);
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("请求失败，状态码: " + response.getStatusLine().getStatusCode());
                }

                // 获取响应内容
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                ObjectMapper objectMapper = new ObjectMapper();
                // 解析 JSON 对象并提取 data 数组
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                int code = jsonNode.path("code").asInt();
                String msg = jsonNode.path("msg").asText();
                JsonNode list = jsonNode.path("data").path("list");
                if (code != 200) {
                    throw new RuntimeException("操作失败，消息: " + msg);
                }
                for (JsonNode node : list) {
                    int monthMsgCount = node.path("monthMsgCount").asInt();
                    int msgCount = node.path("msgCount").asInt();
                    String nickName = node.path("nickName").asText();
                    // 解析 Unicode 后的名称
                    String parsedNickName = new WxUtil().parseUnicode(nickName); // 解析 Unicode

                    String userWxId = node.path("userWxId").asText();
                    if (StrUtil.isNotBlank(userWxId) && userWxId.equals(userWxIdParam)) {
                        if (type == 1) {
                            System.out.println(parsedNickName + " " + msgCount);
                            // 日活跃
                            return msgCount;
                        }
                        if (type == 2) {
                            System.out.println(parsedNickName + " " + monthMsgCount);
                        }
                        return monthMsgCount;
                    }
                }
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.info("关闭失败");
            }
        }
    }

    // msgCount 日活跃
    // monthMsgCount 月活跃

    /**
     * 获取活跃度
     *
     * @param wxRoomId 房间ID
     * @param type     1 日活跃  2 月活跃
     * @return 结果
     */
    public List<String> active(Integer wxRoomId, Integer type) {
        // 创建支持 Cookie 的 HttpClient
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            // 第一次 GET 请求
            String firstUrl = "https://space.robotcy.cn:18082/#/?wxRoomId=" + wxRoomId;
            HttpGet firstRequest = new HttpGet(firstUrl);
            firstRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            firstRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

            log.info("发送第一次请求: " + firstUrl);
            try (CloseableHttpResponse response = httpClient.execute(firstRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("第一次请求失败，状态码: " + statusCode);
                }

                // 打印响应头中的 Set-Cookie
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header header : headers) {
                    log.info("原始 Cookie 头: " + header.getValue());
                }

                // 打印存储的 Cookie
                log.info("存储的 Cookies: ");
                cookieStore.getCookies().forEach(cookie ->
                        log.info(cookie.getName() + "=" + cookie.getValue())
                );

            }
            // 第二次 POST 请求
            String url = "https://space.robotcy.cn:18082/mini_api/wxRoom/selectRoomUser";
            HttpPost postRequest = new HttpPost(url);

            // 构造 JSON 参数
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("pageSize", 250);
            jsonParams.put("pageNum", 1);
            jsonParams.put("wxRoomId", wxRoomId);
            jsonParams.put("type", 10);
            jsonParams.put("hytype", type);
            postRequest.setEntity(new StringEntity(jsonParams.toString(), "UTF-8"));

            // 设置请求头
            postRequest.setHeader("Content-Type", "application/json;charset=UTF-8");
            postRequest.setHeader("User-Agent", "Mozilla/5.0");

            log.info("发送请求: " + url);
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("请求失败，状态码: " + response.getStatusLine().getStatusCode());
                }

                // 获取响应内容
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                ObjectMapper objectMapper = new ObjectMapper();
                // 解析 JSON 对象并提取 data 数组
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                int code = jsonNode.path("code").asInt();
                String msg = jsonNode.path("msg").asText();
                JsonNode list = jsonNode.path("data").path("list");
                if (code != 200) {
                    throw new RuntimeException("操作失败，消息: " + msg);
                }
                List<ActiveVO> activeVOList = new ArrayList<>();
                List<String> result = new ArrayList<>();
                for (JsonNode node : list) {
                    int id = node.path("id").asInt();
                    int msgCount = node.path("msgCount").asInt();
                    int monthMsgCount = node.path("monthMsgCount").asInt();
                    String nickName = node.path("nickName").asText();
                    // 解析 Unicode 后的名称
                    String parsedNickName = parseUnicode(nickName); // 解析 Unicode

                    ActiveVO activeVO = new ActiveVO();
                    activeVO.setId(id);
                    activeVO.setMsgCount(msgCount);
                    activeVO.setMonthMsgCount(monthMsgCount);
                    activeVO.setNickName(parsedNickName);
                    // 日活跃
                    if (type == 1) {
                        // 超过 1 的添加
                        if (msgCount >= 100) {
                            activeVOList.add(activeVO);
                        }
                    }
                    // 月活跃
                    if (type == 2) {
                        // 超过 8888 的添加
                        if (monthMsgCount >= 3000) {
                            activeVOList.add(activeVO);
                        }
                    }
                }
                if (type == 1) {
                    // 日活跃
                    // 根据msgCount排序，从大到小
                    activeVOList.sort((o1, o2) -> o2.getMsgCount() - o1.getMsgCount());
                }
                if (type == 2) {
                    // 月活跃
                    activeVOList.sort((o1, o2) -> o2.getMonthMsgCount() - o1.getMonthMsgCount());
                }
                // 获取前10名
                for (int row = 0; row < activeVOList.size(); row++) {
                    // 日活跃
                    if (type == 1) {
                        result.add(row + 1 + "【" + activeVOList.get(row).getMsgCount() + "】" + activeVOList.get(row).getNickName());
                    }
                    // 月活跃
                    if (type == 2) {
                        // 计算金额 月活跃 / 8888 向下取整数
                        if (activeVOList.get(row).getMonthMsgCount() > 8888) {
                            int money = activeVOList.get(row).getMonthMsgCount() / 8888;
                            result.add(row + 1 + "【" + activeVOList.get(row).getMonthMsgCount() + "】" + activeVOList.get(row).getNickName() + " " + money * 33.44 + "元");
                        } else {
                            result.add(row + 1 + "【" + activeVOList.get(row).getMonthMsgCount() + "】" + activeVOList.get(row).getNickName());
                        }
                    }
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.info("关闭失败");
            }
        }
    }

    /**
     * 金币统计（前十）
     *
     * @param wxRoomId 房间ID
     * @param ranking  排名
     * @param type     0：金币  1：魅力
     * @return 数据
     */
    public List<String> corn(Integer wxRoomId, Integer ranking, Integer type) {
        ranking--;
        // 创建支持 Cookie 的 HttpClient
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            // 第一次 GET 请求
            String firstUrl = "https://space.robotcy.cn:18082/#/?wxRoomId=" + wxRoomId;
            HttpGet firstRequest = new HttpGet(firstUrl);
            firstRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            firstRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

//            log.info("发送第一次请求: " + firstUrl);
            try (CloseableHttpResponse response = httpClient.execute(firstRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("第一次请求失败，状态码: " + statusCode);
                }

                // 打印响应头中的 Set-Cookie
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header header : headers) {
                    log.info("原始 Cookie 头: " + header.getValue());
                }

                // 打印存储的 Cookie
                log.info("存储的 Cookies: ");
                cookieStore.getCookies().forEach(cookie ->
                        log.info(cookie.getName() + "=" + cookie.getValue())
                );

                // 获取响应内容（可能包含重定向或 JavaScript）
//                String responseBody = EntityUtils.toString(response.getEntity());
//                log.info("第一次请求响应: \n" + responseBody.substring(0, Math.min(responseBody.length(), 200)) + "...");
            }
            // 第二次 POST 请求
            String url = "https://space.robotcy.cn:18082/mini_api/wxRoom/selectRoomUser";
            HttpPost postRequest = new HttpPost(url);

            // 构造 JSON 参数
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("pageSize", 250);
            jsonParams.put("pageNum", 1);
            jsonParams.put("wxRoomId", wxRoomId);
            jsonParams.put("type", type);
            jsonParams.put("hytype", "");
            postRequest.setEntity(new StringEntity(jsonParams.toString(), "UTF-8"));

            // 设置请求头
            postRequest.setHeader("Content-Type", "application/json;charset=UTF-8");
            postRequest.setHeader("User-Agent", "Mozilla/5.0");

//            log.info("发送请求: " + url);
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("请求失败，状态码: " + response.getStatusLine().getStatusCode());
                }

                // 获取响应内容
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                ObjectMapper objectMapper = new ObjectMapper();
                // 解析 JSON 对象并提取 data 数组
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                // 解析 JSON 对象并提取 data 数组
                int code = jsonNode.path("code").asInt();
                String msg = jsonNode.path("msg").asText();
                JsonNode list = jsonNode.path("data").path("list");
                if (code != 200) {
                    throw new RuntimeException("操作失败，消息: " + msg);
                }
                List<ActiveVO> activeVOList = new ArrayList<>();
                List<String> result = new ArrayList<>();
                for (JsonNode node : list) {
                    int id = node.path("id").asInt();
                    String nickName = node.path("nickName").asText();
                    // 解析 Unicode 后的名称
                    String parsedNickName = parseUnicode(nickName); // 解析 Unicode
                    int corn = node.path("corn").asInt();
                    int charm = node.path("charm").asInt();

                    ActiveVO activeVO = new ActiveVO();
                    activeVO.setId(id);
                    activeVO.setNickName(parsedNickName);
                    activeVO.setCorn(corn);
                    activeVO.setCharm(charm);
                    // 金币大于1的参与排行榜
                    // 金币
                    if (type == 0){
                        if (corn >= 1) {
                            activeVOList.add(activeVO);
                        }
                    }
                    if (type == 1){
                        if (charm >= 1) {
                            activeVOList.add(activeVO);
                        }
                    }
                }
                if (type == 0){
                    // 金币排序
                    activeVOList.sort((o1, o2) -> o2.getCorn() - o1.getCorn());
                }
                if (type == 1){
                    // 魅力排序
                    activeVOList.sort((o1, o2) -> o2.getCharm() - o1.getCharm());
                }
                // 获取前10名
                for (int row = 0; row < activeVOList.size(); row++) {
                    if (row <= ranking) {
                        // 金币输出
                        if (type == 0){
                            result.add(row + 1 + " 【" + formatChineseNumber(activeVOList.get(row).getCorn()) + "】 " + activeVOList.get(row).getNickName());
                        }
                        if (type == 1){
                            result.add(row + 1 + " 【" + formatChineseNumber(activeVOList.get(row).getCharm()) + "】 " + activeVOList.get(row).getNickName());
                        }
                    }
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.info("关闭失败");
            }
        }
    }


    public List<String> dive(Integer wxRoomId, Integer days) {
        // 创建支持 Cookie 的 HttpClient
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
        try {
            // 第一次 GET 请求
            String firstUrl = "https://space.robotcy.cn:18082/#/?wxRoomId=" + wxRoomId;
            HttpGet firstRequest = new HttpGet(firstUrl);
            firstRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            firstRequest.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");

//            log.info("发送第一次请求: " + firstUrl);
            try (CloseableHttpResponse response = httpClient.execute(firstRequest)) {
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    throw new RuntimeException("第一次请求失败，状态码: " + statusCode);
                }

                // 打印响应头中的 Set-Cookie
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header header : headers) {
                    log.info("原始 Cookie 头: " + header.getValue());
                }

                // 打印存储的 Cookie
                log.info("存储的 Cookies: ");
                cookieStore.getCookies().forEach(cookie ->
                        log.info(cookie.getName() + "=" + cookie.getValue())
                );

                // 获取响应内容（可能包含重定向或 JavaScript）
//                String responseBody = EntityUtils.toString(response.getEntity());
//                log.info("第一次请求响应: \n" + responseBody.substring(0, Math.min(responseBody.length(), 200)) + "...");
            }
            // 第二次 POST 请求
            String url = "https://space.robotcy.cn:18082/mini_api/wxRoom/selectRoomUser";
            HttpPost postRequest = new HttpPost(url);

            // 构造 JSON 参数
            JSONObject jsonParams = new JSONObject();
            jsonParams.put("pageSize", 250);
            jsonParams.put("pageNum", 1);
            jsonParams.put("wxRoomId", wxRoomId);
            jsonParams.put("type", 13);
            jsonParams.put("hytype", "");
            postRequest.setEntity(new StringEntity(jsonParams.toString(), "UTF-8"));

            // 设置请求头
            postRequest.setHeader("Content-Type", "application/json;charset=UTF-8");
            postRequest.setHeader("User-Agent", "Mozilla/5.0");

//            log.info("发送请求: " + url);
            try (CloseableHttpResponse response = httpClient.execute(postRequest)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("请求失败，状态码: " + response.getStatusLine().getStatusCode());
                }

                // 获取响应内容
                String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");

                ObjectMapper objectMapper = new ObjectMapper();
                // 解析 JSON 对象并提取 data 数组
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                // 解析 JSON 对象并提取 data 数组
                int code = jsonNode.path("code").asInt();
                String msg = jsonNode.path("msg").asText();
                JsonNode list = jsonNode.path("data");
                if (code != 200) {
                    throw new RuntimeException("操作失败，消息: " + msg);
                }

                List<String> result = new ArrayList<>();
                for (JsonNode node : list) {
                    int id = node.path("id").asInt();
                    int divingDays = node.path("divingDays").asInt();
                    String nickName = node.path("nickName").asText();
                    // 解析 Unicode 后的名称
                    String parsedNickName = parseUnicode(nickName); // 解析 Unicode
                    if (divingDays >= days) {
//                        log.info(parsedNickName + " " + divingDays);
                        result.add(parsedNickName + " " + divingDays);
                    }
                }
                return result;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                log.info("关闭失败");
            }
        }
    }

    // 解析 Unicode 转义序列
    public String parseUnicode(String input) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            if (i + 5 < input.length() && input.charAt(i) == '\\' && input.charAt(i + 1) == 'u') {
                String hex = input.substring(i + 2, i + 6);
                int codePoint = Integer.parseInt(hex, 16);
                i += 6;

                // 处理代理对（surrogate pair）
                if (Character.isHighSurrogate((char) codePoint) && i + 5 < input.length() &&
                        input.charAt(i) == '\\' && input.charAt(i + 1) == 'u') {
                    String lowHex = input.substring(i + 2, i + 6);
                    int lowCodePoint = Integer.parseInt(lowHex, 16);
                    result.appendCodePoint(Character.toCodePoint((char) codePoint, (char) lowCodePoint));
                    i += 6;
                } else {
                    result.append((char) codePoint);
                }
            } else {
                result.append(input.charAt(i));
                i++;
            }
        }
        return result.toString();
    }

    /**
     * 将数字格式化为中文大单位（亿、万），截断到两位小数，不进行四舍五入，并去掉末尾无效的零。
     * 例如：819774996 -> 8.19亿，12345 -> 1.23万，999 -> 999
     *
     * @param number 要格式化的数值
     * @return 格式化后的字符串
     */
    public static String formatChineseNumber(Integer number) {
        BigDecimal num = new BigDecimal(number);
        BigDecimal absNum = num.abs();
        String suffix = "";
        BigDecimal divisor = BigDecimal.ONE;

        // 根据数值大小确定单位和除数
        if (absNum.compareTo(new BigDecimal("100000000")) >= 0) {
            divisor = new BigDecimal("100000000");
            suffix = "亿";
        } else if (absNum.compareTo(new BigDecimal("10000")) >= 0) {
            divisor = new BigDecimal("10000");
            suffix = "万";
        }

        // 如果需要转换单位
        if (!suffix.isEmpty()) {
            // 先作高精度除法（保留足够多位不舍入），再截断到小数点后 2 位
            BigDecimal value = num.divide(divisor, 10, RoundingMode.DOWN)
                    .setScale(2, RoundingMode.DOWN);
            // 去掉末尾无效的零
            String formatted = value.stripTrailingZeros().toPlainString();
            return formatted + suffix;
        }

        // 不足万，直接返回原始数字
        return num.toPlainString();
    }

}


