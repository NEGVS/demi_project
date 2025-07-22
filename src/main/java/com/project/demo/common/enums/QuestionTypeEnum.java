package com.project.demo.common.enums;

import lombok.Getter;

/**
 * 题目类型
 */
@Getter
public enum QuestionTypeEnum {

    SINGLE("单选题", "single"),
    MULTIPLE("多选题", "multiple"),
    JUDGMENT("判断题", "judgment"),
    SUBJECTIVE("主观题", "subjective"),
    ;

    private final String name;
    private final String value;

    QuestionTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
