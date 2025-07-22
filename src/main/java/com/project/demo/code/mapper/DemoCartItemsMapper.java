package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.demo.code.domain.DemoCartItems;
import com.project.demo.code.dto.demo.CartItemDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 购物车明细表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Mapper
public interface DemoCartItemsMapper extends BaseMapper<DemoCartItems> {

    /**
     * 根据购物车元数据ID和商品ID查询当前商品是否已存在购物车中
     *
     * @param cartId    购物车元数据ID
     * @param productId 商品ID
     * @return 购物车信息
     */
    DemoCartItems selectByCartMetaIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);

    /**
     * 根据购物车明细ID更新购物车明细信息
     * @param cartItem 购物车明细信息
     * @return 影响条数
     */
    int updateByCartItemId(@Param("cartItem")DemoCartItems cartItem);

    /**
     * 查询购物车明细
     * @param merchantId 商家ID
     * @param userId 用户ID
     * @return 购物车明细列表
     */
    List<CartItemDTO> queryCartItem(@Param("merchantId") Long merchantId, @Param("userId") Long userId);

    /**
     * 清空购物车 用户ID + 商家ID
     * @param merchantId 商家ID
     * @param userId 用户ID
     * @return 结果
     */
    int clearCartByMerchantId(@Param("merchantId") Long merchantId, @Param("userId") Long userId);

    /**
     * 清空购物车 用户ID + 商品ID
     * @param productId 商品ID
     * @param userId 用户ID
     * @return 结果
     */
    int clearCartByProductId(@Param("productId") Long productId, @Param("userId") Long userId);
}
