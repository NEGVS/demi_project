package com.project.demo.code.dto.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

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
@Schema(name = "DemoMerchantsDTO", description = "商家详情返回DTO")
public class MerchantsDTO {

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "商家名称")
    private String merchantName;

    @Schema(description = "商家联系人")
    private String contactName;

    @Schema(description = "商家联系电话")
    private String contactPhone;

    @Schema(description = "商家邮箱")
    private String email;

    @Schema(description = "商家地址")
    private String address;

    @Schema(description = "营业执照基本信息（存储文件路径或编号）")
    private String businessLicense;

    @Schema(description = "用户ID(当前店铺所有者)")
    private Long userId;

    @Schema(description = "店铺状态（0：未激活；1：正常；2：暂停；3：关闭）")
    private Integer status;

    @Schema(description = "创建时间")
    private Date createDate;

    @Schema(description = "创建人")
    private String createBy;

    @Schema(description = "更新时间")
    private Date updateDate;

    @Schema(description = "更新人")
    private String updateBy;

    @Schema(description = "商家营业时间配置")
    List<MerchantSchedulesDTO> merchantSchedules;
}
