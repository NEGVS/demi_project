package com.project.demo.code.controller.authority;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.project.demo.code.command.system.UpdateBaseInfoCommand;
import com.project.demo.code.command.system.UserCommand;
import com.project.demo.code.domain.common.MyUserDetails;
import com.project.demo.code.service.UserService;
import com.project.demo.common.Result;
import com.project.demo.common.annotations.RepeatSubmit;
import com.project.demo.common.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "登录、注册")
@RestController
@Slf4j
@RequestMapping("/authority/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/login")
    @Operation(description = "登录接口，返回token", summary = "登录")
    public Result<Object> login(@RequestBody UserCommand command) {
        return userService.login(command);
    }

    @PostMapping("/register")
    @Operation(description = "注册", summary = "注册")
    public Result<Object> register(@RequestBody UserCommand command, HttpServletRequest request) {
        log.info("注册请求参数：{}", JSONUtil.toJsonStr(command));
        return userService.register(command, request);
    }

    /**
     * 修改用户信息
     *
     * @param command 用户输入
     * @return 处理结果
     */
    @PostMapping("/updateBaseInfo")
    @Operation(description = "修改用户信息", summary = "修改用户信息")
    public Result<Object> updateBaseInfo(@RequestBody UpdateBaseInfoCommand command) {
        log.info("修改用户信息请求参数：{}", JSONUtil.toJsonStr(command));
        return userService.updateBaseInfo(command);
    }

    @PostMapping("/getUserInfo")
    @RepeatSubmit(expireSeconds = 8)
    @Operation(description = "获取用户信息", summary = "获取用户信息")
    public Result<Object> getUserInfo() {
        if (SecurityUtils.isAuthenticated()) {
            MyUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
            if (ObjectUtil.isNotEmpty(userDetails)) {
                return Result.success(userDetails);
            }
        }
        return Result.error("未登录");
    }
}
