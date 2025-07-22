package com.project.demo.code.command.demo;

import com.project.demo.code.dto.demo.CartDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "OrderExecuteCommand", description = "订单执行command")
public class OrderExecuteCommand implements Serializable {

    // 用户ID
    @Schema(description = "用户ID")
    private Long userId;

    // 商家ID
    @Schema(description = "商家ID")
    private Long merchantId;

    // 支付方式
    @Schema(description = "支付方式 0：微信 1：支付宝")
    private Integer paymentMethod;

    /**
     * 购物车信息
     */
    @Schema(description = "购物车信息")
    private CartDTO cartInfo;
}
