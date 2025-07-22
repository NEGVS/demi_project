package com.project.demo.common.enums;

import lombok.Getter;

/**
 * 状态码
 */
@Getter
public enum CommonCode {
    ACCOUNT_EXPIRED(100, "账号过期"),
    USER_NOT_FOUND(101, "用户不存在"),
    INVALID_CREDENTIALS(102, "用户名或密码错误"),
    PASSWORD_EXPIRED(103, "密码过期"),
    ACCOUNT_DISABLED(104, "账号不可用"),
    ACCOUNT_LOCKED(105, "账号锁定"),
    INSUFFICIENT_PERMISSIONS(106, "权限不足"),
    OTHER_ERROR(109, "其他错误");

    private final int code;
    private final String desc;

    CommonCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
