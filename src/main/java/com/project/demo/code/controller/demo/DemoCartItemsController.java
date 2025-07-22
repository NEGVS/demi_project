package com.project.demo.code.controller.demo;

import com.project.demo.code.command.demo.CartItemQueryCommand;
import com.project.demo.code.command.demo.CartItemsExecuteCommand;
import com.project.demo.code.dto.demo.CartDTO;
import com.project.demo.code.dto.demo.CartItemDTO;
import com.project.demo.code.service.DemoCartItemsService;
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
 * 购物车明细表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@RestController
@RequestMapping("/demo/cart")
@Tag(name = "购物车")
public class DemoCartItemsController {

    @Resource
    private DemoCartItemsService cartItemsService;

    /**
     * 添加商品到购物车
     *
     * @param command 参数
     * @return 结果
     */
    @PostMapping("add")
    @Operation(description = "添加商品到购物车", summary = "添加商品到购物车")
    public Result<CartDTO> add(@RequestBody CartItemsExecuteCommand command) {
        return cartItemsService.add(command);
    }

    /**
     * 查询购物车
     */
    @PostMapping("queryCartItem")
    @Operation(description = "查询购物车", summary = "查询购物车")
    public Result<CartDTO> queryCartItem(@RequestBody CartItemQueryCommand command) {
        return Result.success(cartItemsService.queryCartItem(command));
    }

    /**
     * 清空购物车
     */
    @PostMapping("clearCart")
    @Operation(description = "清空购物车", summary = "清空购物车")
    public Result<String> clearCart(@RequestBody CartItemQueryCommand command) {
        return cartItemsService.clearCart(command);
    }
}
