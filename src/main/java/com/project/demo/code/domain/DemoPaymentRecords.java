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
 * 支付记录表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_payment_records")
@Schema(name = "DemoPaymentRecords", description = "支付记录表")
public class DemoPaymentRecords {

    @Schema(description = "支付记录ID")
    @TableId("payment_id")
    private Long paymentId;

    @Schema(description = "对应的订单ID")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "支付方式 0：微信 1：支付宝")
    @TableField("payment_method")
    private Integer paymentMethod;

    @Schema(description = "支付金额")
    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    @Schema(description = "支付状态 支付状态 0：待支付 1：已支付 2：已退款 3：订单结束")
    @TableField("status")
    private Integer status;

    @Schema(description = "第三方支付平台的交易流水号")
    @TableField("transaction_id")
    private String transactionId;

    @Schema(description = "支付完成时间")
    @TableField("final_date")
    private Date finalDate;

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
