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
 * 结算单明细表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_settlement_detail")
@Schema(name = "DemoSettlementDetail", description = "结算单明细表")
public class DemoSettlementDetail {

    @Schema(description = "结算明细ID")
    @TableId("detail_id")
    private Long detailId;

    @Schema(description = "结算单ID")
    @TableField("settlement_id")
    private Long settlementId;

    @Schema(description = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "商家从该订单中获得的收入")
    @TableField("merchant_income")
    private BigDecimal merchantIncome;

    @Schema(description = "平台从该订单中收取的佣金")
    @TableField("platform_commission")
    private BigDecimal platformCommission;

    @Schema(description = "配送员收入")
    @TableField("delivery_fee")
    private BigDecimal deliveryFee;

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
