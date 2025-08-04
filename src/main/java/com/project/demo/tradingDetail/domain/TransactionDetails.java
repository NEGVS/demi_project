package com.project.demo.tradingDetail.domain;

import lombok.Data;

import java.util.Date;

/**
 * Table for storing futures transaction details对象 transaction_details
 */
@Data
public class TransactionDetails {

    /**
     * Unique transaction ID
     */
    private Long id;

    /**
     * 名次
     */
    private String ranking;

    /**
     * 会员简称
     */
    private String memberName;


    //@Excel(name = "期货公司会员简称")
    //@ApiModelProperty(name = "期货公司会员简称")
    private String memberName2;


    //@Excel(name = "期货公司会员简称")
    //@ApiModelProperty(name = "期货公司会员简称")
    private String memberName3;

    /**
     * 合约代码
     */
    //@Excel(name = "合约代码")
    //@ApiModelProperty(name = "合约代码")
    private String contractCode;

    /**
     * 交易类型：成交、持买单、持卖单
     */
    //@Excel(name = "交易类型", readConverterExp = "1=成交,2=持买单,3=持卖单")
    //@ApiModelProperty(name = "交易类型")
    private Long transactionType;

    /**
     * 交易总量（成交量/持买单量/持卖单量）
     */
    //@Excel(name = "交易总量")
    //@ApiModelProperty(name = "交易总量")
    private Long totalVolume;

    /**
     * 增减量
     */
    //@Excel(name = "增减量")
    //@ApiModelProperty(name = "增减量")
    private Long volumeChange;

    /**
     * 日期
     */
    //@JsonFormat(pattern = "yyyy-MM-dd")
    //@Excel(name = "日期", width = 30, dateFormat = "yyyy-MM-dd")
    //@ApiModelProperty(name = "日期")
    private String transactionDate;

    /**
     * 期货品种（期货品种中文名）Futures variety
     */
    //@Excel(name = "期货品种")
    //@ApiModelProperty(name = "期货品种")
    private String futuresVariety;

    /**
     * 交易所名称
     */
    //@Excel(name = "交易所名称")
    //@ApiModelProperty(name = "交易所名称")
    private String exchangeName;

    /**
     * 创建时间
     */
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "Asia/Shanghai")
    //@Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    //@ApiModelProperty(name = "创建时间")
    private Date createdTime;


}
