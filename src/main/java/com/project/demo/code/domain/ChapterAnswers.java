package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 章节答案表
 * </p>
 *
 * @author hylogan
 * @since 2024年09月26日 18:33:09
 */
@Getter
@Setter
@TableName("chapter_answers")
@Schema(name = "ChapterAnswers", description = "章节答案表")
public class ChapterAnswers {

    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "章节ID")
    @TableField("topicId")
    private String topicId;

    @Schema(description = "第几题的答案")
    @TableField("indexChapter")
    private Integer indexChapter;

    @Schema(description = "课程ID")
    @TableField("courseId")
    private String courseId;

    @Schema(description = "章节名称")
    @TableField("topicTitle")
    private String topicTitle;

    @Schema(description = "答案")
    @TableField("answer")
    private String answer;

    @Schema(description = "创建时间")
    private Date createdAt;

    @Schema(description = "更新时间")
    private Date updatedAt;
}
