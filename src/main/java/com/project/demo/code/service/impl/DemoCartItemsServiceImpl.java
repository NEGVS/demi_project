package com.project.demo.code.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.demo.CartItemQueryCommand;
import com.project.demo.code.command.demo.CartItemsExecuteCommand;
import com.project.demo.code.domain.DemoCartItems;
import com.project.demo.code.domain.DemoCartMetadata;
import com.project.demo.code.domain.DemoMerchants;
import com.project.demo.code.domain.DemoProducts;
import com.project.demo.code.dto.demo.CartDTO;
import com.project.demo.code.dto.demo.CartItemDTO;
import com.project.demo.code.mapper.DemoCartItemsMapper;
import com.project.demo.code.mapper.DemoCartMetadataMapper;
import com.project.demo.code.mapper.DemoMerchantsMapper;
import com.project.demo.code.mapper.DemoProductsMapper;
import com.project.demo.code.service.DemoCartItemsService;
import com.project.demo.common.Result;
import com.project.demo.common.exception.DemoProjectException;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * 购物车明细表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Service
@Slf4j
public class DemoCartItemsServiceImpl extends ServiceImpl<DemoCartItemsMapper, DemoCartItems> implements DemoCartItemsService {


    @Resource
    private DemoCartMetadataMapper cartMetadataMapper;

    @Resource
    private DemoMerchantsMapper merchantsMapper;

    @Resource
    private DemoProductsMapper productsMapper;

    @Resource
    private DemoCartItemsMapper cartItemsMapper;

    /**
     * 添加商品到购物车
     *
     * @param command 参数
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<CartDTO> add(CartItemsExecuteCommand command) {
        log.info("添加商品到购物车:{}", JSONUtil.toJsonStr(command));
        // 1、参数校验
        String errorMsg = validateParameters(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 1.2、校验用户是否登录
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "用户未登录");
        }
        // 2、业务校验
        // 2.1、商家是否存在
        checkMerchants(command.getMerchantId());
        // 2.2、商品是否存在 （单价是否有变化）
        checkProduct(command);
        // 2.3、检查购物车元信息是否存在
        DemoCartMetadata cartMeta = getCartMeta(command, userId);
        // 3、执行业务
        addOrUpdate(command, cartMeta, userId);
        CartItemQueryCommand queryCommand = new CartItemQueryCommand();
        queryCommand.setMerchantId(command.getMerchantId());
        return Result.success(queryCartItem(queryCommand));
    }

    /**
     * 查询购物车明细
     *
     * @param command 参数
     * @return 购物车信息
     */
    @Override
    public CartDTO queryCartItem(CartItemQueryCommand command) {
        CartDTO result = new CartDTO();
        // 校验用户是否登录
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new DemoProjectException("用户未登录");
        }
        if (command == null || command.getMerchantId() == null) {
            throw new DemoProjectException("参数错误");
        }
        // 商品总价
        BigDecimal totalPrice = BigDecimal.ZERO;
        // 查询购物车明细
        List<CartItemDTO> cartItemList = cartItemsMapper.queryCartItem(command.getMerchantId(), userId);
        if (CollUtil.isNotEmpty(cartItemList)) {
            for (CartItemDTO cartItemDTO : cartItemList) {
                totalPrice = totalPrice.add(cartItemDTO.getTotalPrice());
            }
        }
        result.setTotalPrice(totalPrice);
        result.setCartItemList(cartItemList);
        return result;
    }

    @Override
    public Result<String> clearCart(CartItemQueryCommand command) {
        // 校验用户是否登录
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            throw new DemoProjectException("用户未登录");
        }
        if (command == null || command.getMerchantId() == null) {
            throw new DemoProjectException("参数错误");
        }
        int row = cartItemsMapper.clearCartByMerchantId(command.getMerchantId(), userId);
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 新增或者更新购物车明细
     *
     * @param command  参数
     * @param cartMeta 购物车元信息
     * @param userId   用户ID
     */
    private void addOrUpdate(CartItemsExecuteCommand command, DemoCartMetadata cartMeta, Long userId) {
        // 查询购物车数据是否已有当前商品
        DemoCartItems cartItem = cartItemsMapper.selectByCartMetaIdAndProductId(cartMeta.getCartId(), command.getProductId());
        if (cartItem == null) {
            // 3.1、如果购物车中没有当前商品，则新增一条数据
            cartItem = new DemoCartItems();
            // 商家ID
            cartItem.setMerchantId(command.getMerchantId());
            // 购物车元信息ID
            cartItem.setCartId(cartMeta.getCartId());
            // 商品ID
            cartItem.setProductId(command.getProductId());
            if (command.getQuantity() <= 0) {
                throw new DemoProjectException("商品数量不能小于0");
            }
            // 商品数量
            cartItem.setQuantity(command.getQuantity());
            // 商品单价
            cartItem.setUnitPrice(command.getUnitPrice());
            // 商品总价
            cartItem.setTotalPrice(command.getUnitPrice().multiply(BigDecimal.valueOf(command.getQuantity())));
            cartItem.setUserId(userId);
            cartItem.setCreateBy(userId.toString());
            cartItem.setUpdateBy(userId.toString());
            int row = cartItemsMapper.insert(cartItem);
            if (row == 0) {
                log.info("新增购物车明细失败");
                throw new DemoProjectException("新增购物车明细失败");
            }
        } else {
            // 商家ID是否不同
            if (!cartItem.getMerchantId().equals(command.getMerchantId())) {
                throw new DemoProjectException("商家ID不一致");
            }
            // 数量相加
            cartItem.setQuantity(command.getQuantity() + cartItem.getQuantity());
            // 商品总价
            cartItem.setTotalPrice(command.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            // 判断数量是否小于0
            if (cartItem.getQuantity() <= 0) {
                // 清空购物车(当前商品清空)
                cartItemsMapper.clearCartByProductId(command.getProductId(), userId);
                return;
            }
            int row = cartItemsMapper.updateByCartItemId(cartItem);
            if (row == 0) {
                log.info("更新购物车明细失败");
                throw new DemoProjectException("新增购物车明细失败");
            }
        }
    }

    /**
     * 校验商品是否存在、单价是否发生变化
     *
     * @param command 参数
     */
    private void checkProduct(CartItemsExecuteCommand command) {
        DemoProducts product = productsMapper.selectProductById(command.getProductId());
        if (product == null) {
            throw new DemoProjectException("商品不存在或已下架");
        }
        if (product.getPrice().compareTo(command.getUnitPrice()) != 0) {
            throw new DemoProjectException("商品单价发生变化,请重新添加");
        }
    }

    /**
     * 校验商家是否存在、状态是否在营业
     *
     * @param merchantId 参数
     */
    private void checkMerchants(Long merchantId) {
        DemoMerchants merchants = merchantsMapper.getMerchantsById(merchantId);
        if (merchants == null) {
            throw new DemoProjectException("商家信息不存在");
        }
        if (merchants.getStatus() != 1) {
            throw new DemoProjectException("当前商家未营业");
        }
    }

    /**
     * 获取购物车元信息（返回的一定有信息）
     *
     * @param command 参数
     * @param userId  用户ID
     */
    private DemoCartMetadata getCartMeta(CartItemsExecuteCommand command, Long userId) {
        DemoCartMetadata cartMetadata = null;
        if (command.getCartId() == null || command.getCartId() == 0) {
            cartMetadata = cartMetadataMapper.selectByUserIdAndMerchantId(userId, command.getMerchantId());
        } else {
            cartMetadata = cartMetadataMapper.selectById(command.getCartId());
        }

        if (cartMetadata == null) {
            // 不存在需要新增一条数据
            cartMetadata = new DemoCartMetadata();
            // 用户ID
            cartMetadata.setUserId(userId);
            // 商家ID
            cartMetadata.setMerchantId(command.getMerchantId());
            int insertRow = cartMetadataMapper.insert(cartMetadata);
            if (insertRow == 0) {
                log.info("新增购物车元信息失败：{}", insertRow);
                throw new DemoProjectException("新增购物车元信息失败");
            }
        }
        // 商家ID是否一致
        if (!cartMetadata.getMerchantId().equals(command.getMerchantId())) {
            throw new DemoProjectException("商家ID不一致");
        }
        // 双重保险，保证返回的一定有值
        if (cartMetadata.getCartId() == null) {
            throw new DemoProjectException("购物车元信息不存在");
        }
        return cartMetadata;
    }

    /**
     * 参数必填校验
     *
     * @param command 参数
     * @return 错误信息
     */
    private String validateParameters(CartItemsExecuteCommand command) {
        if (command == null) {
            return "参数为空";
        }
        if (command.getProductId() == null || command.getProductId() == 0) {
            return "商品ID不能为空";
        }
        if (command.getMerchantId() == null || command.getMerchantId() == 0) {
            return "商家ID不能为空";
        }
        if (command.getQuantity() == null || command.getQuantity() == 0) {
            return "商品数量不能为空";
        }
        if (command.getUnitPrice() == null || command.getUnitPrice().compareTo(BigDecimal.ZERO) == 0) {
            return "商品单价不能为空";
        }
        return null;
    }
}
