package com.project.demo.code.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.demo.CartItemQueryCommand;
import com.project.demo.code.command.demo.CartItemsExecuteCommand;
import com.project.demo.code.domain.DemoCartItems;
import com.project.demo.code.dto.demo.CartDTO;
import com.project.demo.code.dto.demo.CartItemDTO;
import com.project.demo.common.Result;

/**
 * <p>
 * 购物车明细表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
public interface DemoCartItemsService extends IService<DemoCartItems> {
    /**
     * 添加购物车
     *
     * @param command 参数
     * @return 结果
     */
    Result<CartDTO> add(CartItemsExecuteCommand command);

    /**
     * 查询购物车
     * @param command 参数
     * @return 购物车信息
     */
    CartDTO queryCartItem(CartItemQueryCommand command);

    /**
     * 清空购物车
     * @param command 参数
     * @return 结果
     */
    Result<String> clearCart(CartItemQueryCommand command);
}
