package com.project.demo.code.command.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "MerchantsIdCommand", description = "商家ID command")
public class MerchantsIdCommand {
    @Schema(description = "商家ID")
    private Long merchantId;
}
