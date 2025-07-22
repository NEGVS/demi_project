package com.project.demo.code.service;

import com.project.demo.code.command.common.PayCommand;
import com.project.demo.code.command.demo.OrderExecuteCommand;
import com.project.demo.code.command.demo.OrderQueryCommand;
import com.project.demo.code.domain.DemoOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.dto.demo.OrderDTO;
import com.project.demo.common.Result;

/**
 * <p>
 * 订单表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
public interface DemoOrderService extends IService<DemoOrder> {

    /**
     * 生成订单
     * @param command 生成订单参数
     * @return 订单信息
     */
    OrderDTO createOrder(OrderExecuteCommand command);

    /**
     * 查询订单
     * @param command 查询条件
     * @return 订单信息
     */
    DemoOrder queryOrder(OrderQueryCommand command);


    /**
     * 支付订单 （模拟）
     * @param command 支付ID
     * @return 结果
     */
    Result<String> payOrder(PayCommand command);
}
