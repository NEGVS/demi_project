package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商家表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_merchants")
@Schema(name = "DemoMerchants", description = "商家表")
public class DemoMerchants {

    @Schema(description = "商家ID")
    @TableId("merchant_id")
    private Long merchantId;

    @Schema(description = "商家名称")
    @TableField("merchant_name")
    private String merchantName;

    @Schema(description = "商家联系人")
    @TableField("contact_name")
    private String contactName;

    @Schema(description = "商家联系电话")
    @TableField("contact_phone")
    private String contactPhone;

    @Schema(description = "商家邮箱")
    @TableField("email")
    private String email;

    @Schema(description = "商家地址")
    @TableField("address")
    private String address;

    @Schema(description = "营业执照基本信息（存储文件路径或编号）")
    @TableField("business_license")
    private String businessLicense;

    @Schema(description = "用户ID(当前店铺所有者)")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "店铺状态（0：未激活；1：正常；2：暂停；3：关闭）")
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
