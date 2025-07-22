package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 土味情话表
 * </p>
 *
 * @author hylogan
 * @since 2024年12月17日 11:57:31
 */
@Getter
@Setter
@TableName("demo_love_talk")
@Schema(name = "DemoLoveTalk", description = "土味情话表")
public class DemoLoveTalk {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "内容")
    @TableField("content")
    private String content;
}
