package com.project.demo.code.command.common;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "PayCommand", description = "支付command")
public class PayCommand implements Serializable {
    // 支付id
    @Schema(description = "支付id")
    private String paymentId;
}
