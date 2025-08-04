package com.project.demo.code.tradingDetail.zhengzhou;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @Description
 * @Author Andy Fan
 * @Date 2025/8/1 16:51
 * @ClassName ZhengZhouService
 */
public class ZhengZhouService {
    private static final Class<String> HTML = null;

    public static void main(String[] args) {
        try {
            RestTemplate restTemplate = new RestTemplate();

            ObjectMapper objectMapper = new ObjectMapper();
            String url = "http://www.czce.com.cn/cn/DFSStaticFiles/Future/2025/20250801/FutureDataHolding.htm";

            // 动态生成 URL
            LocalDate today = LocalDate.now();
            String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String yearStr = today.format(DateTimeFormatter.ofPattern("yyyy"));
//            String url = String.format(BASE_URL, yearStr, dateStr);


            // 获取 HTML 内容
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            System.out.println(JSONUtil.toJsonStr(response));
            if (true) {
                return;
            }
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new HttpClientErrorException(response.getStatusCode(), "Failed to fetch data");
            }

            String htmlContent = response.getBody();
            if (htmlContent == null || htmlContent.trim().isEmpty()) {
                throw new RuntimeException("Empty HTML response");
            }

//            // 解析 HTML
//            List<CommodityData> commodities = parseHtml(htmlContent);
//            logger.info("Parsed {} commodities/contracts", commodities.size());
//
//            // 打印解析结果
//            for (CommodityData commodity : commodities) {
//                logger.info("Commodity/Contract: {}, Date: {}", commodity.name, commodity.date);
//                for (FuturesData data : commodity.rows) {
//                    logger.info("{}", data);
//                }
//            }


            // 获取JSON字符串
            if (ObjectUtil.isEmpty(url)) {
                System.out.println("\nurl jsonResponse is empty");
            }
            String jsonResponse = restTemplate.getForObject(url, HTML);
            System.out.println("\n-----jsonResponse:");
            System.out.println(JSONUtil.toJsonStr(jsonResponse));
        } catch (HttpClientErrorException e) {
            throw new RuntimeException("HTTP error fetching SHFE data: " + e.getStatusCode());
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error fetching SHFE data: " + e.getMessage());
        }
    }
}
