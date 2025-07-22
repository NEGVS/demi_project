package com.project.demo.code.controller.demo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.project.demo.code.command.common.PayCommand;
import com.project.demo.code.domain.DemoOrder;
import com.project.demo.code.domain.DemoPaymentRecords;
import com.project.demo.code.dto.demo.OrderDTO;
import com.project.demo.code.mapper.DemoCartItemsMapper;
import com.project.demo.code.mapper.DemoOrderMapper;
import com.project.demo.code.mapper.DemoPaymentRecordsMapper;
import com.project.demo.common.Result;
import com.project.demo.common.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/demo/notify/pay")
@Tag(name = "支付回调")
public class PayController {

    @Resource
    RedisUtil redisUtil;

    @Resource
    DemoOrderMapper orderMapper;
    @Resource
    DemoPaymentRecordsMapper paymentRecordsMapper;

    @Resource
    DemoCartItemsMapper cartItemsMapper;

    // 支付通知接收
    @PostMapping("/wxNotify")
    @Operation(description = "微信支付回调", summary = "微信支付回调")
    public Result<String> payNotify(@RequestBody PayCommand command) {
        log.info("支付回调：{}", JSONUtil.toJsonStr(command));
        Object object = redisUtil.get(command.getPaymentId());
        if (object == null) {
            log.info("支付回调失败，订单不存在");
            return Result.error("订单不存在");
        }
        OrderDTO bean = JSONUtil.toBean(JSONUtil.toJsonStr(object), OrderDTO.class);
        if (bean.getStatus() == 0) {
            // 更新订单状态
            LambdaUpdateWrapper<DemoOrder> orderUpdateWrapper = new LambdaUpdateWrapper<>();
            orderUpdateWrapper.eq(DemoOrder::getOrderId, bean.getOrderId());
            orderUpdateWrapper.eq(DemoOrder::getStatus, 0);
            orderUpdateWrapper.eq(DemoOrder::getDeleted, 1);
            orderUpdateWrapper.eq(DemoOrder::getPaymentStatus, 0);
            orderUpdateWrapper.set(DemoOrder::getUpdateBy, "System");
            orderUpdateWrapper.set(DemoOrder::getStatus, 1);
            orderUpdateWrapper.set(DemoOrder::getPaymentStatus, 1);
            int row = orderMapper.update(orderUpdateWrapper);
            if (row <= 0) {
                log.info("订单状态修改失败,订单ID:{}", bean.getOrderId());
                return Result.error("订单状态修改失败");
            }
            // 修改支付记录状态
            LambdaUpdateWrapper<DemoPaymentRecords> updateWrapper = new LambdaUpdateWrapper<>();
            // 修改支付状态
            updateWrapper.set(DemoPaymentRecords::getStatus, 1);
            updateWrapper.set(DemoPaymentRecords::getFinalDate, new Date());
            updateWrapper.set(DemoPaymentRecords::getUpdateBy, "System");
            updateWrapper.eq(DemoPaymentRecords::getOrderId, bean.getOrderId());
            updateWrapper.eq(DemoPaymentRecords::getTransactionId, command.getPaymentId());
            updateWrapper.eq(DemoPaymentRecords::getStatus, 0);
            updateWrapper.eq(DemoPaymentRecords::getDeleted, 1);
            updateWrapper.eq(DemoPaymentRecords::getPaymentMethod, 0);
            int updateRow = paymentRecordsMapper.update(updateWrapper);
            if (updateRow <= 0) {
                log.info("支付记录状态修改失败,订单ID:{}", bean.getOrderId());
                return Result.error("支付记录状态修改失败");
            }
            bean.setStatus(1);
            // 更新redis 中存储的数据
            redisUtil.setWithExpire(command.getPaymentId(), JSONUtil.toJsonStr(bean), 10, TimeUnit.MINUTES);
            // 清空购物车
            int i = cartItemsMapper.clearCartByMerchantId(bean.getMerchantId(), bean.getUserId());
            if (i <= 0) {
                log.info("购物车清空失败，当前用户id：{}，当前商户ID：{}", bean.getUserId(), bean.getMerchantId());
            }
        } else {
            // 状态已被修改过，不做处理
            log.info("订单状态已修改，订单ID:{}", bean.getOrderId());
        }
        return Result.success("支付成功");
    }


    public static void main(String[] args) {
        // 两个date日期，比较时间
        DateTime date1 = DateUtil.parse("2025-01-13");
        DateTime date2 = DateUtil.parse("2025-01-12");
        System.out.println(DateUtil.compare(date1, date2, "yyyy-MM-dd"));
    }
}
