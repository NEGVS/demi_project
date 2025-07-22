package com.project.demo.code.controller.test;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.project.demo.code.domain.common.CourseDTO;
import com.project.demo.common.Result;
import com.project.demo.common.utils.CourseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/course")
@Tag(name = "课程相关")
public class CourseController {

    @Resource
    private  CourseUtil courseUtil;
    /**
     * 获取课程信息
     */
    @PostMapping("/autoSubmit")
    @Operation(description = "自动提交课程作业", summary = "自动提交课程作业")
    public Result<String> test(@RequestBody CourseDTO dto) throws Exception {
        log.info("自动提交作业入参：{}", JSONUtil.toJsonStr(dto));
        try {
            // 获取课程列表
            String token = dto.getToken();
            // 获取课程ID
            String courseId = dto.getCourseId();

            if (StrUtil.isBlank(token) || StrUtil.isBlank(courseId)){
                return Result.error("课程ID或者token为空，请检查参数");
            }
            // 解析作业列表
            List<Map<String, String>> maps = courseUtil.explainDaAn(courseUtil.getCourseDaAn(courseId, "1", token), 1, courseId, token);
            log.info("重复提交的数据,一共有{}条数据{}", maps.size(), JSONUtil.toJsonStr(maps));
            for (Map<String, String> map : maps) {
                List<List<Integer>> lists = courseUtil.explainDaAn(map, courseId, token);
                courseUtil.reloadTop(courseId, map.get("id"),lists, token);
            }
            return Result.success("处理成功，请刷新课程作业页面查询数据是否正确");
        }catch (Exception e){
            log.info("自动提交课程作业错误：{}",e.getMessage());
            return Result.error("系统处理出错，请联系管理员");
        }
    }

}
