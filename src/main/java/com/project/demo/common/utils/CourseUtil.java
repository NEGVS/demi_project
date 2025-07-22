package com.project.demo.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.demo.code.domain.ChapterAnswers;
import com.project.demo.code.mapper.ChapterAnswersMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程工具类
 */
@Slf4j
@Component
public class CourseUtil {

    @Resource
    private ChapterAnswersMapper chapterAnswersMapper;

    // 创建两个map，存储多选的选项，判断的对错
    private static final List<String> selectList = new ArrayList<>();
    private static final List<String> judgmentList = new ArrayList<>();

    static {
        // 给26个英文字母加上顺序和下标
        for (int i = 0; i < 26; i++) {
            selectList.add(String.valueOf((char) ('A' + i)));
        }
        judgmentList.add("正确");
        judgmentList.add("错误");
    }

    /**
     * 获取课程作业列表
     *
     * @param courseId  课程ID
     * @param pageIndex 页码
     */
    public String getCourseDaAn(String courseId, String pageIndex, String token) {
        Map<String, String> sendMap = new HashMap<>();
        // 课程ID
        sendMap.put("courseId", courseId);
        // 页码
        sendMap.put("pageIndex", pageIndex);
        // 状态码 (作用未知)
        sendMap.put("stateCode", "");
        return doPost("https://www.learnin.com.cn/app/user/student/course/space/topic/appStudentCourseTopic/loadTopicListData", sendMap, token);
    }

    /**
     * 解析作业列表数据,最后返回需要去自动提交作业的章节作业ID
     *
     * @param json      课程作业列表
     * @param pageIndex 页码
     * @param courseId  课程ID
     */
    public List<Map<String, String>> explainDaAn(String json, Integer pageIndex, String courseId, String token) {
        List<Map<String, String>> list = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        // 是否需要去分页查询，默认不需要
        boolean isPage = false;
        try {
            // 开始解析数据
            JsonNode rootNode = mapper.readTree(json);
            // 获取课程章节作业列表
            JsonNode topics = rootNode.path("topics");
            // 获取总页数
            String totalCount = rootNode.path("totalCount").asText();
            // 判断总页数
            if (StrUtil.isNotBlank(totalCount)) {
                // 判断总页数
                int total = Integer.parseInt(totalCount);
                // 页面大于10，并且当前页面是1，则需要分页查询
                if (total > 10 && pageIndex == 1) {
                    isPage = true;
                }
            }

            // 解析json数据。并组装数据（保存数据）
            for (JsonNode topic : topics) {
                Map<String, String> map = new HashMap<>();
                // 作业ID
                String id = topic.path("id").asText();
                // 作业分数
                String topicScore = topic.path("topicScore").asText();
                // 作业抬头
                String topicTitle = topic.path("topicTitle").asText();
                // 章节抬头
                String chapterTitle = topic.path("chapterTitle").asText();
                // 作业类型
                String topicType = topic.path("topicType").asText();
                // 作业状态
                String state = topic.path("state").asText();
                // 期末大作业直接跳过，需要手动处理 ||
                if (topicType.equals("card") || state.equals("01")) {
                    log.info("期末大作页直接跳过");
                    continue;
                }
                // 章节抬头 | 作业抬头  如果包含 仅一次提交机会  仅一次 提交 机会 的直接跳过不做处理
                if (StrUtil.isBlank(topicTitle) || StrUtil.isBlank(chapterTitle)){
                    // 作业抬头为空，直接跳过不做处理
                    log.info("作业名称为空，作业ID：{}，作业抬头：{}，章节抬头：{}",id,topicTitle,chapterTitle);
                    continue;
                }
                if (topicTitle.contains("仅一次提交机会") || topicTitle.contains("提交") || topicTitle.contains("机会")
                ||chapterTitle.contains("仅一次提交机会") || chapterTitle.contains("提交") || chapterTitle.contains("机会")
                ){
                    log.info("作业名称包含规则，作业ID：{}，作业抬头：{}，章节抬头：{}",id,topicTitle,chapterTitle);
                    // 跳过不做处理
                    continue;
                }
                log.info("作业名称争产，作业ID：{}，作业抬头：{}，章节抬头：{}",id,topicTitle,chapterTitle);
                // 分数为100的跳过 , 并且获取其答案存储在数据库中
                if (topicScore.equals("100.0") || topicScore.equals("100")) {
                    Map<String, String> tempMap = new HashMap<>();
                    // 作业ID
                    tempMap.put("id", id);
                    // 作业抬头
                    tempMap.put("topicTitle", topicTitle);
                    // 解析作业答案
                    explainDaAn(tempMap, courseId, token);
                    log.info("作业满分，不需要重新提交");
                    continue;
                }
                // 作业未提交
                if (state.equals("11")) {
                    // 需要去提交一次，答案随便写
                    submit(courseId , id, token);
                    // 然后直接返回
                    return explainDaAn(getCourseDaAn(courseId, pageIndex + "", token), pageIndex, courseId, token);
                }
                // 作业ID
                map.put("id", id);
                // 作业抬头
                map.put("topicTitle", topicTitle);
                list.add(map);
            }
            if (isPage) {
                pageIndex++;
                list.addAll(explainDaAn(getCourseDaAn(courseId, pageIndex + "", token), pageIndex, courseId, token));
            }
            return list;
        } catch (Exception e) {
            log.info("解析JSON出错：" + e.getMessage());
        }
        return list;
    }

    /**
     * 获取章节作业答案
     *
     * @param map      作业ID信息
     * @param courseId 课程ID
     */
    public List<List<Integer>> explainDaAn(Map<String, String> map, String courseId, String token) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<List<Integer>> resultList = new ArrayList<>();
        // 遍历list，打印数据,分别请求数据。
        Map<String, String> sendMap = new HashMap<>();
        // 课程ID
        sendMap.put("courseId", courseId);
        // 章节ID
        sendMap.put("topicId", map.get("id"));
        // 获取答案
        String result = doPost("https://www.learnin.com.cn/app/user/student/course/space/topic/appStudentCourseTopic/loadTopicData", sendMap, token);
        JsonNode jsonNode = mapper.readTree(result);
        // 解析json数据。并组装数据（保存数据）
        for (JsonNode node : jsonNode.path("topic").path("topicItems")) {
            JsonNode childList = node.path("childList");
            resultList = explainDaAn(childList,courseId , map);
        }
        return resultList;
    }

    /**
     * 提交作业答案
     *
     * @param courseId 课程ID
     * @param topicId  章节ID
     * @param lists    答案列表
     */
    public void reloadTop(String courseId, String topicId, List<List<Integer>> lists, String token) throws Exception {
        // 重新答题
        String json = reloadTop(courseId, topicId, token);
        Map<String, String> jsonMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        // 将重新答题获取的最新的json进行解析
        JsonNode rootNode = mapper.readTree(json);
        JsonNode topic = rootNode.path("topic");
        // 解析json数据。并组装数据（保存数据）
        String studentStoreTopicId = topic.get("studentStoreTopicId").asText();
        // 请求参数
        jsonMap.put("submitTopic", "true");
        // 课程ID
        jsonMap.put("courseId", courseId);
        // 章节ID
        jsonMap.put("topicId", topicId);
        // 学生主体ID
        jsonMap.put("studentStoreTopicId", studentStoreTopicId);

        JsonNode topicItems = topic.get("topicItems");
        for (JsonNode topicItem : topicItems) {
            // 章节ID
            String id = topicItem.get("id").asText();
            // 解析答案，组装成保存所需要的参数
            JsonNode childList = topicItem.get("childList");
            Map<String, Object> parentMap = new HashMap<>();
            // 题目ID
            parentMap.put("id", id);
            List<Map<String, Object>> childListMap = new ArrayList<>();
            parentMap.put("childList", childListMap);
            int index = 0;
            for (JsonNode child : childList) {
                Map<String, Object> childMap = new HashMap<>();
                String questionTypeCode = child.get("questionTypeCode").asText();
                String childId = child.get("id").asText();
                // 获取题目类型questionTypeCode
                childMap.put("id", childId);
                childMap.put("attachments", "");
                childMap.put("topicType", questionTypeCode);
                childMap.put("answer", CollUtil.join(lists.get(index), ","));
                childListMap.add(childMap);
                index++;
            }
            List<Object> testlist = CollUtil.newArrayList(parentMap);
            jsonMap.put("allChoiceTopics", JSONUtil.toJsonStr(testlist));
        }
        doPost("https://www.learnin.com.cn/app/user/student/course/space/topic/appStudentCourseTopic/saveOrSubmitTopicData", jsonMap, token);
    }

    /**
     * 提交作业(第一次没有答案)
     * @param courseId 课程ID
     * @param topicId 章节ID
     * @param token token
     */
    public void submit(String courseId, String topicId, String token) throws Exception {
        // 重新答题
        String json = reloadTop(courseId, topicId, token);
        Map<String, String> jsonMap = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        // 将重新答题获取的最新的json进行解析
        JsonNode rootNode = mapper.readTree(json);
        JsonNode topic = rootNode.path("topic");
        // 解析json数据。并组装数据（保存数据）
        String studentStoreTopicId = topic.get("studentStoreTopicId").asText();
        // 请求参数
        jsonMap.put("submitTopic", "true");
        // 课程ID
        jsonMap.put("courseId", courseId);
        // 章节ID
        jsonMap.put("topicId", topicId);
        // 学生主体ID
        jsonMap.put("studentStoreTopicId", studentStoreTopicId);

        JsonNode topicItems = topic.get("topicItems");
        int index = 0;
        for (JsonNode topicItem : topicItems) {
            // 章节ID
            String id = topicItem.get("id").asText();
            // 解析答案，组装成保存所需要的参数
            JsonNode childList = topicItem.get("childList");
            Map<String, Object> parentMap = new HashMap<>();
            // 题目ID
            parentMap.put("id", id);
            List<Map<String, Object>> childListMap = new ArrayList<>();
            parentMap.put("childList", childListMap);
            for (JsonNode child : childList) {
                Map<String, Object> childMap = new HashMap<>();
                String questionTypeCode = child.get("questionTypeCode").asText();
                String childId = child.get("id").asText();
                // 获取题目类型questionTypeCode
                childMap.put("id", childId);
                childMap.put("attachments", "");
                childMap.put("topicType", questionTypeCode);
                LambdaQueryWrapper<ChapterAnswers> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ChapterAnswers::getTopicId,topicId).eq(ChapterAnswers::getCourseId,courseId).eq(ChapterAnswers::getIndexChapter,index++);
                ChapterAnswers chapterAnswers = chapterAnswersMapper.selectOne(queryWrapper);
                if (chapterAnswers != null) {
                    childMap.put("answer", chapterAnswers.getAnswer());
                }else {
                    childMap.put("answer", "0");
                }
                childListMap.add(childMap);
            }
            List<Object> testlist = CollUtil.newArrayList(parentMap);
            jsonMap.put("allChoiceTopics", JSONUtil.toJsonStr(testlist));
        }
        doPost("https://www.learnin.com.cn/app/user/student/course/space/topic/appStudentCourseTopic/saveOrSubmitTopicData", jsonMap, token);
    }


    /**
     * 重新答题
     *
     * @param courseId 课程ID
     * @param topicId  章节ID
     */
    public String reloadTop(String courseId, String topicId, String token) {
        Map<String, String> jsonMap = new HashMap<>();
        jsonMap.put("courseId", courseId);
        jsonMap.put("topicId", topicId);
        return doPost("https://www.learnin.com.cn/app/user/student/course/space/topic/appStudentCourseTopic/loadRedoTopicData", jsonMap, token);
    }

    /**
     * 获取答案
     *
     * @param rootNode jsonNode
     */
    public List<List<Integer>> explainDaAn(JsonNode rootNode,String courseId ,Map<String,String> map) {
        List<List<Integer>> resultList = new ArrayList<>();
        int index = 1;
        for (JsonNode jsonNode : rootNode) {
            // 题目描述
            String questionTitle = jsonNode.get("questionTitle").asText();
            // 题目类型
            String questionTypeCode = jsonNode.get("questionTypeCode").asText();
            String questionTypeName = jsonNode.get("questionTypeName").asText();
            // 是否是选择题
            boolean isSelect = false;
            // 是否是判断题
            boolean isJudge = false;
            if (questionTypeCode.equals("single") || questionTypeCode.equals("multiple")) {
                isSelect = true;
            }
            if (questionTypeCode.equals("judgment")) {
                isJudge = true;
            }
            log.info("第{} {}题：{}", index, questionTypeName, questionTitle);
            // 获取optionList数组节点
            JsonNode optionList = jsonNode.get("optionList");
            ChapterAnswers chapterAnswers = new ChapterAnswers();
            chapterAnswers.setCourseId(courseId);
            chapterAnswers.setIndexChapter(index);
            chapterAnswers.setTopicId(map.get("id"));
            chapterAnswers.setTopicTitle(map.get("topicTitle"));
            if (optionList != null && optionList.isArray()) {
                // 遍历optionList中的每个选项
                List<Integer> answerList = new ArrayList<>();
                for (int i = 0; i < optionList.size(); i++) {
                    JsonNode option = optionList.get(i);
                    // 获取isAnswer属性值
                    boolean isAnswer = option.get("isAnswer").asBoolean();
                    // 如果isAnswer为true，则输出该选项的下标
                    if (isAnswer) {
                        answerList.add(i);
                    }
                }
                resultList.add(answerList);
                List<String> list = new ArrayList<>();
                for (Integer i : answerList) {
                    if (isSelect) {
                        list.add(selectList.get(i));
                    }
                    if (isJudge) {
                        list.add(judgmentList.get(i));
                    }
                }
                String answerTemp = CollUtil.join(list, ",");
                chapterAnswers.setAnswer(answerTemp);

                // 判断数据库中是否存在该条数据，不存在则插入
                LambdaQueryWrapper<ChapterAnswers> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ChapterAnswers::getTopicId, map.get("id")).eq(ChapterAnswers::getCourseId, courseId).eq(ChapterAnswers::getIndexChapter, index);
                ChapterAnswers temp = chapterAnswersMapper.selectOne(queryWrapper);
                if (temp == null) {
                    chapterAnswersMapper.insert(chapterAnswers);
                }
                log.info("答案：{}", answerTemp);
            } else {
                // 答案
                String referenceAnswer = jsonNode.get("referenceAnswer").asText();
                log.info("答案：{}", referenceAnswer);
                chapterAnswers.setAnswer(referenceAnswer);

                // 判断数据库中是否存在该条数据，不存在则插入
                LambdaQueryWrapper<ChapterAnswers> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(ChapterAnswers::getTopicId, map.get("id")).eq(ChapterAnswers::getCourseId, courseId).eq(ChapterAnswers::getIndexChapter, index);
                ChapterAnswers temp = chapterAnswersMapper.selectOne(queryWrapper);
                if (temp == null) {
                    chapterAnswersMapper.insert(chapterAnswers);
                }
            }
            index++;
        }
        return resultList;
    }

    /**
     * post请求
     *
     * @param url   请求地址
     * @param param 请求参数
     * @param token token
     */
    public String doPost(String url, Map<String, String> param, String token) {
        // 创建Httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String resultString = "";
        try {
            // 创建Http Post请求
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.addHeader("Authorization", token);
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
