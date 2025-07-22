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
 * 结算单表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_settlements")
@Schema(name = "DemoSettlements", description = "结算单表")
public class DemoSettlements {

    @Schema(description = "结算单ID")
    @TableId("settlement_id")
    private Long settlementId;

    @Schema(description = "商家ID")
    @TableField("merchant_id")
    private Long merchantId;

    @Schema(description = "总订单数")
    @TableField("total_order_count")
    private Integer totalOrderCount;

    @Schema(description = "总收入金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @Schema(description = "商家实际收入")
    @TableField("merchant_income")
    private BigDecimal merchantIncome;

    @Schema(description = "平台佣金")
    @TableField("platform_commission")
    private BigDecimal platformCommission;

    @Schema(description = "结算状态 0：待结算 1：已结算")
    @TableField("status")
    private Byte status;

    @Schema(description = "结算完成时间")
    @TableField("final_date")
    private Date finalDate;

    @Schema(description = "删除标志 1 有效，其他无效")
    @TableField("deleted")
    private Byte deleted;

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
