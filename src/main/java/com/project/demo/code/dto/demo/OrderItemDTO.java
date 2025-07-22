package com.project.demo.code.dto.demo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(name = "OrderItemDTO", description = "订单详情返回DTO")
public class OrderItemDTO implements Serializable {

    @Schema(description = "订单明细id")
    private Long orderItemId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "商品单价")
    private BigDecimal unitPrice;

    @Schema(description = "当前商品的小计金额")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    private BigDecimal discountAmount;
}
