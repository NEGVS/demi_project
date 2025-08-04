package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_products")
@Schema(name = "DemoProducts", description = "商品表")
public class DemoProducts {

    @Schema(description = "商品ID")
    @TableId("product_id")
    private Long productId;

    @Schema(description = "商家ID")
    @TableField("merchant_id")
    private Long merchantId;

    @Schema(description = "商品分类ID")
    @TableField("category_id")
    private Long categoryId;

    @Schema(description = "商品名称")
    @TableField("product_name")
    private String productName;

    @Schema(description = "商品单价")
    @TableField("price")
    private BigDecimal price;

    @Schema(description = "折扣价（如果没有为0）")
    @TableField("discount_price")
    private BigDecimal discountPrice;

    @Schema(description = "商品库存")
    @TableField("mapper/stock")
    private Integer stock;

    @Schema(description = "商品主图（存储文件路径或URL）")
    @TableField("product_image")
    private String productImage;

    @Schema(description = "商品详细描述")
    @TableField("description")
    private String description;

    @Schema(description = "商品状态（0：下架；1：上架；2：售罄）")
    @TableField("status")
    private Integer status;

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
