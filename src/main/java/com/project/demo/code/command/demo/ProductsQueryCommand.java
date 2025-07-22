package com.project.demo.code.command.demo;

import com.project.demo.code.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "DemoProductsQueryCommand", description = "商品列表查询条件")
public class ProductsQueryCommand extends PageModel implements Serializable {

    @Schema(description = "商品分类ID")
    private Long categoryId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品ID")
    private Long productId;

    @Schema(description = "商家ID")
    private Long merchantId;
}
