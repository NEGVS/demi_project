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
 * 购物车明细表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_cart_items")
@Schema(name = "DemoCartItems", description = "购物车明细表")
public class DemoCartItems {

    @Schema(description = "购物车明细ID")
    @TableId("cart_item_id")
    private Long cartItemId;

    @Schema(description = "购物车元数据信息ID")
    @TableField("cart_id")
    private Long cartId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "商品ID")
    @TableField("product_id")
    private Long productId;

    @Schema(description = "商家ID")
    @TableField("merchant_id")
    private Long merchantId;

    @Schema(description = "商品数量")
    @TableField("quantity")
    private Integer quantity;

    @Schema(description = "商品单价")
    @TableField("unit_price")
    private BigDecimal unitPrice;

    @Schema(description = "商品总价 = 单价 x 数量")
    @TableField("total_price")
    private BigDecimal totalPrice;

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
