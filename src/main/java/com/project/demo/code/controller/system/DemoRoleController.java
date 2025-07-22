package com.project.demo.code.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.command.system.role.RoleIdCommand;
import com.project.demo.code.command.system.role.RoleQueryCommand;
import com.project.demo.code.domain.DemoRole;
import com.project.demo.code.service.DemoRoleService;
import com.project.demo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 角色信息表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2024年12月25日 16:44:06
 */
@RestController
@RequestMapping("/system/role")
@Tag(name = "角色")
public class DemoRoleController {
    @Resource
    private DemoRoleService roleService;
    /**
     * 查询角色列表
     */
    @PostMapping("/list")
    @Operation(description = "查询角色列表(分页)", summary = "查询角色列表(分页)")
    public Result<IPage<DemoRole>> list(@RequestBody RoleQueryCommand command) {
        return roleService.selectList(command);
    }

    /**
     * 新增角色
     */
    @PostMapping("/add")
    @Operation(description = "新增角色", summary = "新增角色")
    public Result<String> add(@RequestBody DemoRole menu) {
        return roleService.add(menu);
    }

    /**
     * 更新角色
     */
    @PostMapping("/edit")
    @Operation(description = "编辑角色", summary = "编辑角色")
    public Result<String> edit(@RequestBody DemoRole menu) {
        return roleService.edit(menu);
    }

    /**
     * 删除角色
     */
    @PostMapping("/deleted")
    @Operation(description = "删除角色", summary = "删除角色")
    public Result<String> deleted(@RequestBody RoleIdCommand command) {
        return roleService.deleted(command);
    }

    /**
     * 获取角色详情
     */
    @PostMapping("/getMenuById")
    @Operation(description = "获取角色详情", summary = "获取角色详情")
    public Result<DemoRole> getMenuById(@RequestBody RoleIdCommand command) {
        return roleService.getMenuById(command);
    }
}
