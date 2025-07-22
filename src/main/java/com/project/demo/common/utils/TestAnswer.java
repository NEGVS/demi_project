package com.project.demo.common.utils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;

public class TestAnswer {
    public static void main(String[] args) {
        try {
            String json = new String(Files.readAllBytes(Paths.get("D:\\workspace\\AndyFan\\demo_project\\src\\main\\resources\\tset\\awner.json")));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(json);
            for (JsonNode node : jsonNode) {
                JsonNode childInfo = node.path("child_info");
                if (!childInfo.isEmpty()) {
                    for (JsonNode child : childInfo) {
                        // 子题目
                        String childContent = child.path("content").asText();
                        // 解析
                        String childAnalysis = child.path("analysis").asText();
                        System.out.println("子题目：" + childContent);
                        System.out.println("子题目解析：" + childAnalysis);
                        int j = 0;
                        for (JsonNode childOption : child.path("option")) {
                            // 子题目答案选项
                            // 答案选项
                            String optionContent = childOption.path("describ").asText();
                            System.out.println("子题目选项：" + (++j) + "：" + optionContent);
                        }
                    }
                } else {
                    // 题目
                    String content = node.path("content").asText();
                    // 解析
                    String analysis = node.path("analysis").asText();
                    System.out.println("题目：" + content);
                    System.out.println("解析：" + analysis);
                    int i = 0;
                    for (JsonNode option : node.path("option")) {
                        // 答案选项
                        String optionContent = option.path("describ").asText();
                        System.out.println("主题目选项：" + (++i) + "：" + optionContent);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("解析失败");
        }
    }
}
