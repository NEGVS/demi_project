package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 订单明细表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月07日 16:27:41
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("demo_order_item")
@Schema(name = "DemoOrderItem", description = "订单明细表")
public class DemoOrderItem {

    @Schema(description = "订单明细id")
    @TableId(value = "order_item_id", type = IdType.AUTO)
    private Long orderItemId;

    @Schema(description = "订单ID")
    @TableField("order_id")
    private Long orderId;

    @Schema(description = "商品ID")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "商品数量")
    @TableField("quantity")
    private Integer quantity;

    @Schema(description = "商品单价")
    @TableField("unitPrice")
    private BigDecimal unitPrice;

    @Schema(description = "当前商品的小计金额")
    @TableField("total_amount")
    private BigDecimal totalAmount;

    @Schema(description = "优惠金额")
    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @Schema(description = "删除标志 1 有效，其他无效")
    @TableField("deleted")
    private Byte deleted;

    @Schema(description = "版本")
    @TableField("version")
    @Version
    private Integer version;

    @Schema(description = "创建时间")
    @TableField("create_date")
    private Date createDate;

    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "更新时间")
    @TableField("update_date")
    private Date updateDate;

    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;
}
