package com.project.demo.code.command.demo;



import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(name = "CategoriesIdCommand", description = "商品分类ID")
public class CategoriesIdCommand implements Serializable {
    @Schema(description = "分类ID")
    private Long categoryId;
}
