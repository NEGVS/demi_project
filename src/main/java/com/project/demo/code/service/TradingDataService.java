package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.system.futures.FuturesQueryCommand;
import com.project.demo.code.domain.TradingData;
import com.project.demo.common.Result;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author hylogan
 * @since 2024年06月12日 13:34:47
 */
@Service
public interface TradingDataService extends IService<TradingData> {

    /**
     * 查询列表
     * @param command 查询参数
     * @return 结果
     */
    Result<IPage<TradingData>> selectList(FuturesQueryCommand command);
}
