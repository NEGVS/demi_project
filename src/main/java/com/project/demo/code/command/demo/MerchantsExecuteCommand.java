package com.project.demo.code.command.demo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.project.demo.code.dto.demo.MerchantSchedulesDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(name = "MerchantsExecuteCommand", description = "商家添加、编辑Command")
public class MerchantsExecuteCommand implements Serializable {
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

    @Schema(description = "是否为自己添加（1：是（默认）；2：否）")
    private Integer isSelf = 1;

    @Schema(description = "商家营业时间配置")
    List<MerchantSchedulesDTO> merchantSchedules;
}
