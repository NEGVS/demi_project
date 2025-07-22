package com.project.demo.code.dto.demo;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 购物车明细表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@Schema(name = "CartItemDTO", description = "购物车返回（明细）DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CartItemDTO {

    @Schema(description = "购物车明细ID")
    private Long cartItemId;

    @Schema(description = "购物车元数据信息ID")
    private Long cartId;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品图片")
    private String productImage;

    @Schema(description = "商品数量")
    private Integer quantity;

    @Schema(description = "商品单价")
    private BigDecimal unitPrice;

    @Schema(description = "商品总价(商品小计) = 单价 x 数量")
    private BigDecimal totalPrice;

}
