package com.project.demo.code.command.demo;


import com.project.demo.code.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(name = "CategoriesQueryCommand", description = "商品分类查询条件")
public class CategoriesQueryCommand extends PageModel implements Serializable {

    @Schema(description = "分类ID")
    private Long categoryId;

    @Schema(description = "父级分类ID , 顶级分类为 0")
    private Long parentCategoryId;

    @Schema(description = "分类名称")
    private String categoryName;

}
