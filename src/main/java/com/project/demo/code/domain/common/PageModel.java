package com.project.demo.code.domain.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 分页
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PageModel implements Serializable {
    /**
     * 获取当前页
     */
    @Schema( description = "获取当前页")
    private long pageNum = 1;
    /**
     * 当前页显示几条数据
     */
    @Schema( description = "当前页显示几条数据")
    private long pageSize = 10;
    /**
     * 当前页是否有下一页
     */
    @Schema( description = "当前页是否有下一页")
    private boolean hasNext;
    /**
     * 当前页是否有上一页
     */
    @Schema( description = "当前页是否有上一页")
    private boolean hasPrevious;

}
