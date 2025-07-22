package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.demo.code.command.system.futures.FuturesQueryCommand;
import com.project.demo.code.domain.TradingData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2024年06月12日 13:34:47
 */
@Mapper
public interface TradingDataMapper extends BaseMapper<TradingData> {

    void insertList(List<TradingData> dataList);


    /**
     * 分页查询
     *
     * @param page    分页参数
     * @param command 查询参数
     * @return 结果
     */
    IPage<TradingData> page(@Param("page") Page<TradingData> page, @Param("command") FuturesQueryCommand command);
}
