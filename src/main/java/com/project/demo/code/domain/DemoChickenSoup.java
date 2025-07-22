package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.lang.String;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 鸡汤表
 * </p>
 *
 * @author hylogan
 * @since 2025年04月07日 17:33:29
 */
@Getter
@Setter
@Accessors(chain = true)
@TableName("demo_chicken_soup")
@Schema(name = "DemoChickenSoup", description = "鸡汤表")
public class DemoChickenSoup {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private String id;

    @Schema(description = "内容")
    @TableField("content")
    private String content;

    // 分页参数
    @TableField(exist = false)
    private Integer pageNum;
    @TableField(exist = false)
    private Integer pageSize;
}
