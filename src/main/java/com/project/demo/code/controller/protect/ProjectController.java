package com.project.demo.code.controller.protect;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/project")
@Tag(name = "受保护的方法")
public class ProjectController {
    @GetMapping("get")
    @PreAuthorize("@hy.hasPermi('system:test:get')")
    public String get() {
        log.info("get");
        return "get";
    }

    @PreAuthorize("@hy.hasPermi('system:test:post')")
    @PostMapping("testPost")
    public String post() {
        log.info("post");
        return "post";
    }
}



