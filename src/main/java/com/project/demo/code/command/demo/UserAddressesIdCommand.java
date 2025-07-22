package com.project.demo.code.command.demo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "UserAddressesIdCommand", description = "用户收货地址主键IDCommand")
public class UserAddressesIdCommand implements Serializable {
    @Schema(description = "主键ID")
    private Long addressId;
}
