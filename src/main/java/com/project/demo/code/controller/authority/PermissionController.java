package com.project.demo.code.controller.authority;

import com.project.demo.code.command.system.rolemenu.PermissionExecuteCommand;
import com.project.demo.code.dto.rolemenu.RoleMenuDTO;
import com.project.demo.code.service.DemoMenuService;
import com.project.demo.code.service.DemoRoleService;
import com.project.demo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/authority/role")
@Tag(name = "角色权限")
public class PermissionController {

    @Resource
    private DemoMenuService menuService;

    @Resource
    private DemoRoleService roleService;

    /**
     * 查询角色对应的菜单权限
     */
    @Operation(description = "查询角色对应的菜单权限", summary = "查询角色对应的菜单权限")
    @PostMapping("/getMenuByRoleId")
    public Result<List<RoleMenuDTO>> getMenuByRoleId() {
        return menuService.getMenuByRoleId();
    }

    /**
     * 对某个角色添加权限
     */
    @Operation(description = "对某个角色添加权限", summary = "对某个角色添加权限")
    @PostMapping("/addMenuByRoleId")
    public Result<String> addMenuByRoleId(@RequestBody PermissionExecuteCommand command) {
        return roleService.addMenuByRoleId(command);
    }

    /**
     * 对某个角色移除权限
     */
    @Operation(description = "对某个角色移除权限", summary = "对某个角色移除权限")
    @PostMapping("/removeMenuByRoleId")
    public Result<String> removeMenuByRoleId(@RequestBody PermissionExecuteCommand command) {
        return roleService.removeMenuByRoleId(command);
    }
}
