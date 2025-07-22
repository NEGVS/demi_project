package com.project.demo.code.dto.demo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(name = "OrderDTO", description = "订单返回DTO")
public class OrderDTO implements Serializable {

    // 支付ID
    @Schema(description = "支付ID")
    private String paymentId;

    // 订单ID
    @Schema(description = "订单ID")
    private Long orderId;

    // 商家ID
    @Schema(description = "商家ID")
    private Long merchantId;

    // 用户ID
    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "实际支付金额")
    private BigDecimal finalAmount;

    @Schema(description = "订单状态 0：待支付 1：已支付")
    private Integer status;

    @Schema(description = "订单明细")
    List<OrderItemDTO> orderItemList;
}
