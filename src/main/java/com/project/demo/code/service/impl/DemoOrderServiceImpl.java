package com.project.demo.code.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.common.PayCommand;
import com.project.demo.code.command.demo.CartItemQueryCommand;
import com.project.demo.code.command.demo.OrderExecuteCommand;
import com.project.demo.code.command.demo.OrderQueryCommand;
import com.project.demo.code.domain.DemoOrder;
import com.project.demo.code.domain.DemoOrderItem;
import com.project.demo.code.domain.DemoPaymentRecords;
import com.project.demo.code.domain.DemoProducts;
import com.project.demo.code.domain.common.PayVO;
import com.project.demo.code.dto.demo.*;
import com.project.demo.code.mapper.DemoOrderMapper;
import com.project.demo.code.mapper.DemoPaymentRecordsMapper;
import com.project.demo.code.service.*;
import com.project.demo.common.Result;
import com.project.demo.common.exception.DemoProjectException;
import com.project.demo.common.util.RedisUtil;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Service
@Slf4j
public class DemoOrderServiceImpl extends ServiceImpl<DemoOrderMapper, DemoOrder> implements DemoOrderService {

    @Resource
    private DemoCartItemsService cartItemsService;

    @Resource
    private DemoMerchantsService merchantsService;

    @Resource
    private DemoProductsService productsService;

    @Resource
    private DemoOrderItemService orderItemService;

    @Resource
    private DemoPaymentRecordsMapper paymentRecordsMapper;

    @Resource
    private DemoOrderMapper orderMapper;

    @Resource
    private RedisUtil redisUtil;

    RestTemplate restTemplate = new RestTemplate();


    /**
     * 生成订单信息
     *
     * @param command 生成订单参数
     * @return 订单信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderDTO createOrder(OrderExecuteCommand command) {
        // 1、参数校验
        validateParameters(command);
        // 2、检查商户是否存在、状态是否正常、商家是否处于营业时间
        checkMerchant(command.getMerchantId());
        // 3、获取当前用户ID + 商家ID 的购物车信息，对比入参的购物车信息
        checkCartItem(command.getCartInfo(), command.getMerchantId());
        // 4、创建订单
        OrderDTO orderDTO = loadOrder(command);
        // 5、获取支付ID
        String paymentId = getPaymentId(orderDTO);
        orderDTO.setPaymentId(paymentId);
        // 根据支付ID 扫码 H5 跳转app
        // 存入Redis 10分钟取消
        redisUtil.setWithExpire(paymentId, JSONUtil.toJsonStr(orderDTO), 10, TimeUnit.MINUTES);
        // 6、支付记录
        getPaymentRecords(command.getPaymentMethod(), orderDTO, paymentId);
        return orderDTO;
    }

    /**
     * 查询订单信息
     *
     * @param command 查询条件
     * @return 订单信息
     */
    @Override
    public DemoOrder queryOrder(OrderQueryCommand command) {
        if (command == null) {
            return null;
        }
        return orderMapper.selectById(command.getOrderId());
    }

    /**
     * 支付订单 （模拟）
     *
     * @param command 支付ID
     * @return 结果
     */
    @Override
    public Result<String> payOrder(PayCommand command) {
        Object object = redisUtil.get(command.getPaymentId());
        if (object == null) {
            log.info("订单不存在或者订单超时未支付");
            return Result.error("支付失败");
        }
        OrderDTO bean = JSONUtil.toBean(JSONUtil.toJsonStr(object), OrderDTO.class);
        if (bean.getStatus() == 0) {
            // 未支付，调用本方法需要去调用通知支付的接口（模拟）
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String jsonStr = JSONUtil.toJsonStr(command);
            HttpEntity<String> entity = new HttpEntity<>(jsonStr, headers);
            String msg = restTemplate.postForObject("http://localhost:7860/demo/notify/pay/wxNotify", entity, String.class);
            log.info("msg:{}", msg);
        } else if (bean.getStatus() == 1) {
            // 订单已经支付成功
            return Result.success(bean.getPaymentId());
        }
        return Result.success();
    }

    /**
     * 获取支付ID TODO 测试
     *
     * @param orderDTO 订单信息
     * @return 支付ID
     */
    private String getPaymentId(OrderDTO orderDTO) {
        PayVO payVO = new PayVO();
        // 订单ID
        payVO.setOrderId(orderDTO.getOrderId());
        // 商户号
        payVO.setMerchantNo("hy_demo_project");
        // 支付标题
        payVO.setSubject("商品支付");
        // 商品信息
        payVO.setBody(JSONUtil.toJsonStr(orderDTO));
        // 支付金额
        payVO.setFinalAmount(orderDTO.getFinalAmount());
        // 支付方式 目前默认微信0
        payVO.setPaymentMethod(0);
        // 用户ID
        payVO.setUserId(SecurityUtils.getCurrentUserId());
        // 支付成功通知地址
        payVO.setNotifyUrl("http://localhost:7860/notify/pay/wxNotify");
        // 交易类型 扫码 H5 app
        payVO.setTradeType("H5");
        // 模拟请求
        log.info("支付请求参数：{}", JSONUtil.toJsonStr(payVO));
        // 生成随机的支付ID
        String paymentId = UUID.randomUUID().toString();
        log.info("支付ID：{}", paymentId);
        return paymentId;
    }

    /**
     * 生成支付记录信息
     *
     * @param paymentMethod 支付方式
     * @param orderDTO      订单信息
     * @param paymentId     支付ID
     */
    private void getPaymentRecords(Integer paymentMethod, OrderDTO orderDTO, String paymentId) {
        // 1、生成支付记录信息
        DemoPaymentRecords paymentRecord = new DemoPaymentRecords();
        // 支付方式
        paymentRecord.setPaymentMethod(paymentMethod);
        // 订单ID
        paymentRecord.setOrderId(orderDTO.getOrderId());
        // 支付金额
        paymentRecord.setPaymentAmount(orderDTO.getFinalAmount());
        // 支付状态
        paymentRecord.setStatus(0);
        // 创建人
        paymentRecord.setCreateBy(SecurityUtils.getLoginUserId());
        // 更新人
        paymentRecord.setUpdateBy(SecurityUtils.getLoginUserId());
        // 第三方支付ID
        paymentRecord.setTransactionId(paymentId);
        // 插入支付记录
        int row = paymentRecordsMapper.insert(paymentRecord);
        if (row <= 0) {
            log.info("支付记录创建失败");
            throw new DemoProjectException("支付记录创建失败");
        }
    }

    /**
     * 装载订单信息
     *
     * @param command 参数
     */
    private OrderDTO loadOrder(OrderExecuteCommand command) {
        OrderDTO orderDTO = new OrderDTO();
        // 订单总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        // 订单明细
        List<DemoOrderItem> orderItemList = new ArrayList<>();
        // 1、根据购物车数据、组装订单明细信息
        for (CartItemDTO cartItem : command.getCartInfo().getCartItemList()) {
            DemoOrderItem orderItem = getOrderItem(cartItem);
            orderItemList.add(orderItem);
            // 计算订单总价
            totalAmount = totalAmount.add(cartItem.getTotalPrice());
        }
        DemoOrder order = getOrder(command, totalAmount);
        // 插入订单数据
        int row = baseMapper.insert(order);
        if (row > 0) {
            orderItemList.forEach(item -> {
                item.setOrderId(order.getOrderId());
                item.setCreateBy(SecurityUtils.getLoginUserId());
                item.setUpdateBy(SecurityUtils.getLoginUserId());
            });
            // 插入明细数据
            boolean saveBatch = orderItemService.saveBatch(orderItemList);
            if (!saveBatch) {
                log.info("新增订单明细数据失败");
                throw new DemoProjectException("订单创建失败");
            }
        } else {
            log.info("订单创建失败");
            throw new DemoProjectException("订单创建失败");
        }
        // 订单ID
        orderDTO.setOrderId(order.getOrderId());
        // 订单总价
        orderDTO.setFinalAmount(totalAmount);
        // 订单状态
        orderDTO.setStatus(0);
        // 用户ID
        orderDTO.setUserId(order.getUserId());
        // 商家ID
        orderDTO.setMerchantId(order.getMerchantId());

        List<OrderItemDTO> orderItemDTOS = new ArrayList<>();
        // 订单详情
        for (DemoOrderItem orderItem : orderItemList) {
            OrderItemDTO orderItemDTO = new OrderItemDTO();
            BeanUtil.copyProperties(orderItem, orderItemDTO);
            orderItemDTOS.add(orderItemDTO);
        }
        // 订单明细
        orderDTO.setOrderItemList(orderItemDTOS);
        return orderDTO;
    }

    /**
     * 获取订单主项信息
     *
     * @param command     参数
     * @param totalAmount 订单总金额
     * @return 订单信息
     */
    private DemoOrder getOrder(OrderExecuteCommand command, BigDecimal totalAmount) {
        DemoOrder order = new DemoOrder();
        // 用户ID
        order.setUserId(SecurityUtils.getCurrentUserId());
        // 商家ID
        order.setMerchantId(command.getMerchantId());
        // 订单总金额
        order.setTotalAmount(totalAmount);
        // 优惠金额
        order.setDiscountAmount(BigDecimal.ZERO);
        // 实际支付金额
        order.setFinalAmount(totalAmount);
        // 配送费
        order.setDeliveryFee(BigDecimal.ZERO);
        // 订单状态
        order.setStatus(0);
        // 支付状态
        order.setPaymentStatus(0);
        // 创建人
        order.setCreateBy(SecurityUtils.getLoginUserId());
        order.setUpdateBy(SecurityUtils.getLoginUserId());
        return order;
    }

    /**
     * 获取订单明细数据
     *
     * @param cartItem 购物车参数
     * @return 订单明细
     */
    private DemoOrderItem getOrderItem(CartItemDTO cartItem) {
        DemoOrderItem orderItem = new DemoOrderItem();
        // 商品ID
        orderItem.setProductId(cartItem.getProductId());
        // 商品单价
        orderItem.setUnitPrice(cartItem.getUnitPrice());
        // 商品数量
        orderItem.setQuantity(cartItem.getQuantity());
        // 商品小计
        orderItem.setTotalAmount(cartItem.getTotalPrice());
        return orderItem;
    }

    /**
     * 校验购物车信息是否和数据库信息一致
     *
     * @param cartDTO    入参购物车信息
     * @param merchantId 商家ID
     */
    private void checkCartItem(CartDTO cartDTO, Long merchantId) {
        // 1、获取数据库存储的购物车信息
        CartItemQueryCommand command = new CartItemQueryCommand();
        command.setMerchantId(merchantId);
        // 数据库的购物车信息
        CartDTO dataBaseCartInfo = cartItemsService.queryCartItem(command);
        if (dataBaseCartInfo == null) {
            log.info("数据库购物车信息为空");
            throw new DemoProjectException("购物车信息发生变化，请重新添加");
        }
        if (dataBaseCartInfo.getTotalPrice().compareTo(cartDTO.getTotalPrice()) != 0) {
            log.info("数据库购物车信息总价发生变化");
            throw new DemoProjectException("购物车信息发生变化，请重新添加");
        }
        if (CollUtil.isEmpty(dataBaseCartInfo.getCartItemList())) {
            log.info("数据库购物车信息无明细信息");
            throw new DemoProjectException("购物车信息发生变化，请重新添加");
        }
        List<CartItemDTO> cartItemList = cartDTO.getCartItemList();
        List<CartItemDTO> dbCartItemList = dataBaseCartInfo.getCartItemList();
        // 校验购物车商品列表的大小是否一致
        if (cartItemList.size() != dbCartItemList.size()) {
            log.info("购物车商品数量不一致");
            throw new DemoProjectException("购物车商品数量发生变化，请重新添加");
        }
        // 获取所有的商品ID
        List<Long> productIdList = cartItemList.stream().map(CartItemDTO::getProductId).toList();
        List<Long> dbProductIdList = dbCartItemList.stream().map(CartItemDTO::getProductId).toList();
        // 校验商品ID列表大小是否一致
        if (productIdList.size() != dbProductIdList.size()) {
            log.info("购物车商品数量不一致");
            throw new DemoProjectException("购物车商品数量发生变化，请重新添加");
        }
        // 校验是否有重复的元素 ， 如果有表示数据异常
        boolean hasDuplicates = productIdList.stream()
                .anyMatch(e -> Collections.frequency(productIdList, e) > 1);
        if (hasDuplicates) {
            log.info("入参中购物车商品ID重复");
            throw new DemoProjectException("购物车商品发生变化，请重新添加");
        }
        // 校验数据库是否重复元素
        boolean dbHasDuplicates = dbProductIdList.stream()
                .anyMatch(e -> Collections.frequency(dbProductIdList, e) > 1);
        if (dbHasDuplicates) {
            log.info("数据库中购物车商品ID重复");
            throw new DemoProjectException("购物车商品发生变化，请重新添加");
        }
        // 使用set对比两个商品id列表是否相同
        if (!new HashSet<>(productIdList).containsAll(dbProductIdList)) {
            log.info("购物车商品ID不一致");
            throw new DemoProjectException("购物车商品发生变化，请重新添加");
        }
        // 转为map
        Map<Long, CartItemDTO> cartItemMap = cartItemList.stream().collect(Collectors.toMap(CartItemDTO::getCartItemId, cartItem -> cartItem));
        Map<Long, CartItemDTO> dbCartItemMap = dbCartItemList.stream().collect(Collectors.toMap(CartItemDTO::getCartItemId, cartItem -> cartItem));
        // 查询所有的商品信息
        Map<Long, DemoProducts> productMap = productsService.selectMapByIds(productIdList);
        if (productMap == null){
            log.info("商品信息不存在");
            throw new DemoProjectException("商品信息不存在");
        }
        // 开始对比购物车明细
        for (Long cartItemId : productIdList) {
            // 入参购物车明细
            CartItemDTO cartItem = cartItemMap.get(cartItemId);
            // 数据库购物车明细
            CartItemDTO dbCartItem = dbCartItemMap.get(cartItemId);
            Long productId = cartItem.getProductId();
            if (!Objects.equals(productId, dbCartItem.getProductId())) {
                log.info("购物车明细商品ID不一致");
                throw new DemoProjectException("购物车明细商品发生变化，请重新添加");
            }
            DemoProducts product = productMap.get(productId);

            // 1、校验商品数量是否一致
            if (cartItem.getQuantity().compareTo(dbCartItem.getQuantity()) != 0) {
                log.info("购物车明细商品数量发生变化");
                throw new DemoProjectException("购物车明细商品发生变化，请重新添加");
            }
            // 2、单价 (入参购物车 - 数据库购物车 - 商品信息)
            if (cartItem.getUnitPrice().compareTo(product.getPrice()) != 0 || dbCartItem.getUnitPrice().compareTo(product.getPrice()) != 0) {
                log.info("购物车明细商品单价发生变化");
                throw new DemoProjectException("购物车明细商品发生变化，请重新添加");
            }
            // 3、商品总价
            if (cartItem.getTotalPrice().compareTo(dbCartItem.getTotalPrice()) != 0) {
                log.info("购物车明细商品总价发生变化");
                throw new DemoProjectException("购物车明细商品发生变化，请重新添加");
            }
        }
    }


    /**
     * 检查商户是否存在、状态是否允许下单、是否处于营业时间
     *
     * @param merchantId 商家ID
     */
    private void checkMerchant(Long merchantId) {
        // 1、查询商家信息
        MerchantsDTO merchantsDTO = merchantsService.getMerchantsId(merchantId);
        if (merchantsDTO == null) {
            throw new DemoProjectException("商家不存在");
        }
        // 2、校验商家状态
        if (merchantsDTO.getStatus() != 1) {
            throw new DemoProjectException("当前商家状态不允许下单");
        }
        // 3、校验商家营业时间（没有营业时间就表示一直在营业）
        if (CollUtil.isNotEmpty(merchantsDTO.getMerchantSchedules())) {
            // TODO 营业时间校验后续完善
            log.info("校验商家营业时间");
        }
    }

    /**
     * 生成订单参数校验
     *
     * @param command 参数
     */
    private void validateParameters(OrderExecuteCommand command) {
        log.info("校验参数入参：{},当前用户ID：{}，当前用户名：{}", JSONUtil.toJsonStr(command), SecurityUtils.getCurrentUserId(), SecurityUtils.getCurrentUsername());
        if (command == null) {
            log.info("非法参数");
            throw new DemoProjectException("非法参数");
        }
        if (command.getUserId() == null || command.getMerchantId() == null) {
            log.info("非法参数");
            throw new DemoProjectException("非法参数");
        }
        if (command.getCartInfo() == null) {
            log.info("购物车无信息");
            throw new DemoProjectException("购物车为空");
        }
        if (CollUtil.isEmpty(command.getCartInfo().getCartItemList())) {
            log.info("购物车无信息(明细为空)");
            throw new DemoProjectException("购物车为空");
        }
        if (command.getCartInfo().getTotalPrice() == null) {
            log.info("购物车无信息(总价为空)");
            throw new DemoProjectException("购物车为空");
        }
        // 校验登录人和当前下单是否是同一个人
        if (!Objects.equals(SecurityUtils.getCurrentUserId(), command.getUserId())) {
            log.info("登录人和当前下单的不是同一个用户");
            throw new DemoProjectException("非法参数");
        }
        // 支付方式
        if (command.getPaymentMethod() == null) {
            log.info("支付参数为空");
            throw new DemoProjectException("请选择支付方式");
        }
    }
}
