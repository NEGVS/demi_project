package com.project.demo.code.dto.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Schema(name = "CartItemDTO", description = "购物车返回(主)DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartDTO {

    @Schema(description = "购物车明细")
    List<CartItemDTO> cartItemList;

    @Schema(description = "商品总价（当前购物车总价）")
    private BigDecimal totalPrice;
}
