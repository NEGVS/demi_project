package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户收货地址表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:44:19
 */
@Getter
@Setter
@TableName("demo_user_addresses")
@Schema(name = "DemoUserAddresses", description = "用户收货地址表")
public class DemoUserAddresses {

    @Schema(description = "地址ID")
    @TableId( value ="address_id", type = IdType.AUTO)
    private Long addressId;

    @Schema(description = "用户ID")
    @TableField("user_id")
    private Long userId;

    @Schema(description = "收货人姓名")
    @TableField("receiver_name")
    private String receiverName;

    @Schema(description = "收货人手机号")
    @TableField("phone_number")
    private String phoneNumber;

    @Schema(description = "省份")
    @TableField("province")
    private String province;

    @Schema(description = "城市")
    @TableField("city")
    private String city;

    @Schema(description = "区/县")
    @TableField("district")
    private String district;

    @Schema(description = "详细地址")
    @TableField("detailed_address")
    private String detailedAddress;

    @Schema(description = "邮政编码")
    @TableField("postal_code")
    private String postalCode;

    @Schema(description = "是否为默认地址（1：是；0：否）")
    @TableField("is_default")
    private Byte isDefault;

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
