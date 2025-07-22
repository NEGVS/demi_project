package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 购物车元信息表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_cart_metadata")
@Schema(name = "DemoCartMetadata", description = "购物车元信息表")
public class DemoCartMetadata {

    @Schema(description = "购物车元数据信息ID")
    @TableId("cart_id")
    private Long cartId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "优惠券ID")
    @TableField("coupon_id")
    private Long couponId;

    @Schema(description = "商家ID")
    @TableField("merchant_id")
    private Long merchantId;

    @Schema(description = "用户备注")
    @TableField("notes")
    private String notes;

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
