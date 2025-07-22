package com.project.demo.code.command.demo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "CartItemQueryCommand", description = "购物车明细查询Command")
public class CartItemQueryCommand implements Serializable {
    @Schema(description = "商家ID")
    private Long merchantId;
}
