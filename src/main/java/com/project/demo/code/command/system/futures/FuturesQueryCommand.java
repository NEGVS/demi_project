package com.project.demo.code.command.system.futures;


import com.baomidou.mybatisplus.annotation.TableField;
import com.project.demo.code.domain.common.PageModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "期货数据查询命令")
public class FuturesQueryCommand extends PageModel {
    @Schema(description = "品种名，类型为字符串")
    private String commodity;

    @Schema(description = "日期，类型为日期格式（YYYY-MM-DD）")
    private String date;

    @Schema(description = "类型，类型为字符串")
    private String type;

    @Schema(description = "席位信息，类型为字符串")
    @TableField("seat")
    private String seat;
}
