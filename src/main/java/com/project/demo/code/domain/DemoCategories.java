package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品分类表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_categories")
@Schema(name = "DemoCategories", description = "商品分类表")
public class DemoCategories {

    @Schema(description = "分类ID")
    @TableId("category_id")
    private Long categoryId;

    @Schema(description = "父级分类ID , 顶级分类为 0")
    @TableField("parent_category_id")
    private Long parentCategoryId;

    @Schema(description = "分类名称")
    @TableField("category_name")
    private String categoryName;

    @Schema(description = "分类描述")
    @TableField("description")
    private String description;

    @Schema(description = "删除标志 1 有效，其他无效")
    @TableField("deleted")
    private Byte deleted;

    @Schema(description = "版本")
    @TableField("version")
    @Version
    private Integer version;

    @Schema(description = "创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;
}
