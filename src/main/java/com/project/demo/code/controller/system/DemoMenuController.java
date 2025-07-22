package com.project.demo.code.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.command.system.menu.MenuIdCommand;
import com.project.demo.code.command.system.menu.MenuQueryCommand;
import com.project.demo.code.domain.DemoMenu;
import com.project.demo.code.service.DemoMenuService;
import com.project.demo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 菜单权限表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2024年12月24日 16:48:22
 */
@RestController
@RequestMapping("/system/menu")
@Tag(name = "菜单")
public class DemoMenuController {

    @Resource
    private DemoMenuService menuService;

    /**
     * 查询菜单列表
     */
    @PostMapping("/list")
    @Operation(description = "查询菜单列表(分页)", summary = "查询菜单列表(分页)")
    public Result<IPage<DemoMenu>> list(@RequestBody MenuQueryCommand command) {
        return menuService.selectList(command);
    }

    /**
     * 新增菜单
     */
    @PostMapping("/add")
    @Operation(description = "新增菜单", summary = "新增菜单")
    public Result<String> add(@RequestBody DemoMenu menu) {
        return menuService.add(menu);
    }

    /**
     * 更新菜单
     */
    @PostMapping("/edit")
    @Operation(description = "编辑菜单", summary = "编辑菜单")
    public Result<String> edit(@RequestBody DemoMenu menu) {
        return menuService.edit(menu);
    }

    /**
     * 删除菜单
     */
    @PostMapping("/deleted")
    @Operation(description = "删除菜单", summary = "删除菜单")
    public Result<String> deleted(@RequestBody MenuIdCommand command) {
        return menuService.deleted(command);
    }

    /**
     * 获取菜单详情
     */
    @PostMapping("/getMenuById")
    @Operation(description = "获取菜单详情", summary = "获取菜单详情")
    public Result<DemoMenu> getMenuById(@RequestBody MenuIdCommand command) {
        return menuService.getMenuById(command);
    }

    /**
     * 根据当前用户信息获取菜单信息
     */
    @PostMapping("/getMenuByLoinUser")
    @Operation(description = "根据当前用户信息获取菜单信息", summary = "根据当前用户信息获取菜单信息")
    public Result<List<DemoMenu>> getMenuByLoinUser () {
        return menuService.getMenuByLoinUser();
    }
}
