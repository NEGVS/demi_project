package com.project.demo.code.command.demo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "商品ID")
public class ProductIdCommand implements Serializable {
    @Schema(description = "商品ID")
    private Long productId;
}
