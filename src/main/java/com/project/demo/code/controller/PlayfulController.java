package com.project.demo.code.controller;

import com.project.demo.common.Result;
import com.project.demo.code.domain.DemoLoveTalk;
import com.project.demo.code.service.DemoLoveTalkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

/**
 * 奇奇怪怪的
 */
@Tag(name = "奇奇怪怪的")
@RestController
@Slf4j
@RequestMapping("/paly")
public class PlayfulController {

    @Resource
    DemoLoveTalkService demoLoveTalkService;

    @GetMapping("/random")
    @Operation(description = "土味情话（随机）", summary = "土味情话")
    public Result<String> random() {
        try {
            return demoLoveTalkService.randomLoveTalk();
        }catch (Exception e){
            return demoLoveTalkService.randomLoveTalk();
        }
    }
}
