package com.project.demo.code.controller.demo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.command.demo.UserAddressesIdCommand;
import com.project.demo.code.command.demo.UserAddressesQueryCommand;
import com.project.demo.code.domain.DemoUserAddresses;
import com.project.demo.code.service.DemoUserAddressesService;
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
 * 用户收货地址表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:44:19
 */
@RestController
@RequestMapping("/demo/addresses")
@Tag(name = "用户收货地址")
public class DemoUserAddressesController {

    @Resource
    private DemoUserAddressesService userAddressesService;

    /**
     * 查询用户收货地址
     */
    @PostMapping("list")
    @Operation(description = "查询用户收货地址(分页)", summary = "查询用户收货地址(分页)")
    public Result<IPage<DemoUserAddresses>> list(@RequestBody UserAddressesQueryCommand command) {
        return userAddressesService.selectList(command);
    }

    /**
     * 根据当前登录用户查询收货地址
     */
    @PostMapping("getUserAddressesList")
    @Operation(description = "根据当前登录用户查询收货地址", summary = "根据当前登录用户查询收货地址")
    public Result<List<DemoUserAddresses>> getUserAddressesList() {
        return userAddressesService.getUserAddressesList();
    }

    /**
     * 新增用户收货地址
     */
    @PostMapping("add")
    @Operation(description = "新增用户收货地址", summary = "新增用户收货地址")
    public Result<String> add(@RequestBody DemoUserAddresses command) {
        return userAddressesService.add(command);
    }

    /**
     * 修改用户收货地址
     */
    @PostMapping("edit")
    @Operation(description = "修改用户收货地址", summary = "修改用户收货地址")
    public Result<String> edit(@RequestBody DemoUserAddresses command) {
        return userAddressesService.edit(command);
    }

    /**
     * 删除用户收货地址
     */
    @PostMapping("/deleted")
    @Operation(description = "删除用户收货地址", summary = "删除用户收货地址")
    public Result<String> deleted(@RequestBody UserAddressesIdCommand command) {
        return userAddressesService.deleted(command);
    }

    /**
     * 设置默认地址
     */
    @PostMapping("/setIsDefault")
    @Operation(description = "设置默认地址", summary = "设置默认地址")
    public Result<String> setIsDefault(@RequestBody UserAddressesIdCommand command) {
        return userAddressesService.setIsDefault(command);
    }
}
