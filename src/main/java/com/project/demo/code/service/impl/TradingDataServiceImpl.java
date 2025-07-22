package com.project.demo.code.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.system.futures.FuturesQueryCommand;
import com.project.demo.code.domain.TradingData;
import com.project.demo.code.mapper.TradingDataMapper;
import com.project.demo.code.service.TradingDataService;
import com.project.demo.common.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2024年06月12日 13:34:47
 */
@Service
@Slf4j
public class TradingDataServiceImpl extends ServiceImpl<TradingDataMapper, TradingData> implements TradingDataService {

    @Resource
    private TradingDataMapper tradingDataMapper;


    /**
     * 查询列表
     * @param command 查询参数
     * @return 结果
     */
    @Override
    public Result<IPage<TradingData>> selectList(FuturesQueryCommand command) {
        // 1、构建分页参数
        Page<TradingData> page = new Page<>(command.getPageNum(), command.getPageSize());
        // 执行分页查询
        IPage<TradingData> resultPage = tradingDataMapper.page(page, command);
        return Result.success(resultPage);
    }
}
