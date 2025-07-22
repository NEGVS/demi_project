package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author hylogan
 * @since 2024年06月12日 13:34:47
 */
@Getter
@Setter
@TableName("trading_data")
@Schema(name = "TradingData", description = "")
public class TradingData {

    @Schema(description = "自增主键，用于唯一标识每一行数据")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "席位信息，类型为字符串")
    @TableField("seat")
    private String seat;

    @Schema(description = "多单数量，允许负数")
    @TableField("long_position")
    private Integer longPosition;

    @Schema(description = "增减量，允许负数")
    @TableField("change_num")
    private Integer changeNum;

    @Schema(description = "净多空数据，允许负数")
    @TableField("net_long_short")
    private Integer netLongShort;

    @Schema(description = "品种名，类型为字符串")
    @TableField("commodity")
    private String commodity;

    @Schema(description = "日期，类型为日期格式（YYYY-MM-DD）")
    @TableField("date")
    private Date date;

    @Schema(description = "类型信息，类型为字符串")
    @TableField("type")
    private String type;
}
