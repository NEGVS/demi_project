package com.project.demo.code.domain.common;


import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(name = "PayVO", description = "(模拟)支付VO")
public class PayVO implements Serializable {

    // 订单号
    @Schema(description = "订单号")
    private Long orderId;

    // 商户号（微信收款方/支付宝收款方）
    @Schema(description = "商户号")
    private String merchantNo;

    // 支付标题
    @Schema(description = "支付标题")
    private String subject;

    // 商品相关信息
    @Schema(description = "商品相关信息")
    private String body;

    @Schema(description = "支付金额")
    private BigDecimal finalAmount;

    @Schema(description = "支付方式 0：微信 1：支付宝")
    private Integer paymentMethod;

    // 用户ID
    @Schema(description = "用户ID")
    private Long userId;

    // 回调地址
    @Schema(description = "回调地址")
    private String notifyUrl;

    // 交易类型 （扫码 H5 跳转app支付）
    @Schema(description = "交易类型")
    private String tradeType;
}
