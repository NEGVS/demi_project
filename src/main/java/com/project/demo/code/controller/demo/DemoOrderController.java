package com.project.demo.code.controller.demo;

import com.project.demo.code.command.common.PayCommand;
import com.project.demo.code.command.demo.OrderExecuteCommand;
import com.project.demo.code.command.demo.OrderQueryCommand;
import com.project.demo.code.domain.DemoOrder;
import com.project.demo.code.dto.demo.OrderDTO;
import com.project.demo.code.service.DemoOrderService;
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
 * 订单表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@RestController
@RequestMapping("/demo/order")
@Tag(name = "订单")
public class DemoOrderController {
    // 1、用户从购物车点击去结算
    // 2、获取到购物车的所有明细信息，展示给用户
    // 3、用户确认无误之后，选择支付方式
    // 4、用户点击支付支付，生成订单，获取第三方支付ID，存入到支付记录表中
    // 5、用户支付成功之后，更新支付记录表为已支付，订单同步改变状态，当前购物车清空
    // 6、用户支付失败，更新支付记录表为支付失败，订单同步改变状态，当前购物车不清空
    // 7、用户未支付超时，更新支付记录表为支付超时，订单同步改变状态，当前购物车不清空

    @Resource
    private DemoOrderService orderService;

    /**
     * 生成订单
     * 返回支付地址
     */
    @PostMapping("/createOrder")
    @Operation(summary = "生成订单", description = "生成订单")
    public Result<OrderDTO> createOrder(@RequestBody OrderExecuteCommand command) {
        return Result.success(orderService.createOrder(command));
    }

    @PostMapping("/queryOrder")
    @Operation(summary = "查询订单", description = "查询订单")
    public Result<DemoOrder> queryOrder(@RequestBody OrderQueryCommand command) {
        return Result.success(orderService.queryOrder(command));
    }

    @PostMapping("/payOrder")
    @Operation(summary = "支付订单（模拟）", description = "支付订单（模拟）")
    public Result<String> payOrder(@RequestBody PayCommand command) {
        return orderService.payOrder(command);
    }
}
