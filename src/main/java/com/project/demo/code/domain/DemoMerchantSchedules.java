package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * <p>
 * 商家营业时间表
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Getter
@Setter
@TableName("demo_merchant_schedules")
@Schema(name = "DemoMerchantSchedules", description = "商家营业时间表")
public class DemoMerchantSchedules {

    @Schema(description = "营业时间表ID")
    @TableId("schedule_id")
    private Long scheduleId;

    @Schema(description = "商家ID")
    @TableField("merchant_id")
    private Long merchantId;

    @Schema(description = "星期几（0：周日，1：周一，... 6：周六）")
    @TableField("day_of_week")
    private Integer dayOfWeek;

    @Schema(description = "当天的营业开始时间")
    @TableField("open_time")
    private Date openTime;

    @Schema(description = "当天的营业结束时间")
    @TableField("close_time")
    private Date closeTime;

    @Schema(description = "是否为节假日（0：否，1：是）")
    @TableField("is_holiday")
    private Integer isHoliday;

    @Schema(description = "删除标志 1 有效，其他无效")
    @TableField("deleted")
    private Integer deleted;

    @Schema(description = "版本")
    @TableField("version")
    @Version
    private Integer version;

    @Schema(description = "创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;
}
