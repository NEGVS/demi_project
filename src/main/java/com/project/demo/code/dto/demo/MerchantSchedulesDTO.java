package com.project.demo.code.dto.demo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(name = "MerchantSchedulesDTO", description = "商家营业时间返回DTO")
public class MerchantSchedulesDTO {

    @Schema(description = "营业时间表ID")
    private Long scheduleId;

    @Schema(description = "商家ID")
    private Long merchantId;

    @Schema(description = "星期几（0：周日，1：周一，... 6：周六）")
    private Integer dayOfWeek;

    @Schema(description = "当天的营业开始时间")
    private Date openTime;

    @Schema(description = "当天的营业结束时间")
    private Date closeTime;

    @Schema(description = "是否为节假日（0：否，1：是）")
    private Integer isHoliday;

}
