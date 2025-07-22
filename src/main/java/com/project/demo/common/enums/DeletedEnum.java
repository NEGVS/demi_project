package com.project.demo.common.enums;

import lombok.Getter;

/**
 * 是否被删除
 */
public enum DeletedEnum {
    NO(2, "是"),

    Yes(1, "否"),
    ;

    @Getter
    private final Integer code;

    private final String name;

    DeletedEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
