package com.project.demo.common.util;

import lombok.Data;

@Data
public class ActiveVO {

    // ID
    private Integer id;

    // 日活跃
    private Integer msgCount;

    // 月活跃
    private Integer monthMsgCount;

    // 名称
    private String nickName;

    // 金币
    private Integer corn;

    // 魅力
    private Integer charm;
}
