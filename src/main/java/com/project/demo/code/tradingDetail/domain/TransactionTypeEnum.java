package com.project.demo.code.tradingDetail.domain;

public enum TransactionTypeEnum {

    DEAL(1L, "成交"),
    HOLD_BUY(2L, "持买单"),
    HOLD_SALE(2L, "持卖单");

    private final Long code;
    private final String description;

    TransactionTypeEnum(Long code, String description) {
        this.code = code;
        this.description = description;
    }

    public Long getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static String getDescriptionByCode(Long code) {
        for (TransactionTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type.getDescription();
            }
        }
        return "未知"; // 或抛出异常，视业务需求而定
    }
}
