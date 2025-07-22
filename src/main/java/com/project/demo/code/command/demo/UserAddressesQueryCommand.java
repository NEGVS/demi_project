package com.project.demo.code.command.demo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.project.demo.code.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "UserAddressesQueryCommand", description = "用户收货地址查询条件")
public class UserAddressesQueryCommand extends PageModel implements Serializable {

    @Schema(description = "地址ID")
    private Long addressId;

    @Schema(description = "收货人姓名")
    private String receiverName;

    @Schema(description = "收货人手机号")
    private String phoneNumber;

    @Schema(description = "省份")
    private String province;

    @Schema(description = "城市")
    private String city;

    @Schema(description = "区/县")
    private String district;

    @Schema(description = "邮政编码")
    private String postalCode;

}
