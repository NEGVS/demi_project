package com.project.demo.code.command.demo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "OrderQueryCommand", description = "订单列表查询条件")
public class OrderQueryCommand implements Serializable {

    // 订单ID
    @Schema(description = "订单ID")
    private Long orderId;
}
