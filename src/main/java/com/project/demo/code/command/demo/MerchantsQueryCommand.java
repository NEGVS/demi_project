package com.project.demo.code.command.demo;

import com.project.demo.code.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "DemoMerchantsQueryCommand", description = "商家列表查询条件")
public class MerchantsQueryCommand extends PageModel implements Serializable {

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

    @Schema(description = "用户ID(当前店铺所有者)")
    private Long userId;

    @Schema(description = "店铺状态（0：未激活；1：正常；2：暂停；3：关闭）")
    private Byte status;
}
