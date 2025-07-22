package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_order")
@Schema(name = "DemoOrder", description = "订单表")
public class DemoOrder {

    @Schema(description = "订单id")
    @TableId("order_id")
    private Long orderId;

    @Schema(description = "用户id")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "商家id")
    @TableField("merchant_id")
    private Long merchantId;

    @Schema(description = "订单总金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @Schema(description = "实际支付金额")
    @TableField("final_amount")
    private BigDecimal finalAmount;

    @Schema(description = "配送费")
    @TableField("delivery_fee")
    private BigDecimal deliveryFee;

    @Schema(description = "订单状态 0：待支付 1：已支付 2：订单超时取消 3：用户手动取消 4：订单已完成 5：订单退款中 6：订单退款完成")
    @TableField("status")
    private Integer status;

    @Schema(description = "是否已进入结算 （0：未结算 , 1：结算中 , 2：结算完成）")
    @TableField("is_in_settlement")
    private Integer isInSettlement;

    @Schema(description = "支付状态 0：待支付 1：已支付 2：已退款 3：订单结束")
    @TableField("payment_status")
    private Integer paymentStatus;

    @Schema(description = "删除标志 1 有效，其他无效")
    @TableField("deleted")
    private Integer deleted;

    @Schema(description = "版本")
    @TableField("version")
    @Version
    private Integer version;

    @Schema(description = "创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;
}
