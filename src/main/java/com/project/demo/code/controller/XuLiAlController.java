package com.project.demo.code.controller;

import cn.hutool.core.util.StrUtil;
import com.project.demo.common.Result;
import com.project.demo.common.utils.XuLiAlUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "蓄力AL，签到、分享")
@RestController
@Slf4j
@RequestMapping("/xuli")
public class XuLiAlController {
    @GetMapping("/run")
    @Operation(description = "蓄力AL，签到、分享", summary = "任务处理")
    public Result<String> run(String token, String url) {
        try {
            if (StrUtil.isNotBlank(token)){
                XuLiAlUtil.TOKEN = token;
            }
            if (StrUtil.isNotBlank(url)) {
                XuLiAlUtil.url = url;
            }
            XuLiAlUtil.sign(token);
            XuLiAlUtil.click(token);
            XuLiAlUtil.click(token);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
        return Result.success("ok");
    }
}
