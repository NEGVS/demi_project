package com.project.demo.common.enums;

import lombok.Getter;

@Getter
public enum RedisKey {
    USER_TOKEN("user_token:", "用户token"),
    LOGIN_USER_KEY("login_user_key","登录-用户信息key前缀");

    final String prefix;
    final String desc;

    RedisKey(String prefix, String desc) {
        this.prefix = prefix;
        this.desc = desc;
    }
}
