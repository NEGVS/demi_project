package com.project.demo.code.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.demo.MerchantsExecuteCommand;
import com.project.demo.code.command.demo.MerchantsIdCommand;
import com.project.demo.code.command.demo.MerchantsQueryCommand;
import com.project.demo.code.domain.DemoMerchantSchedules;
import com.project.demo.code.domain.DemoMerchants;
import com.project.demo.code.domain.DemoUser;
import com.project.demo.code.dto.demo.MerchantSchedulesDTO;
import com.project.demo.code.dto.demo.MerchantsDTO;
import com.project.demo.code.mapper.DemoMerchantSchedulesMapper;
import com.project.demo.code.mapper.DemoMerchantsMapper;
import com.project.demo.code.mapper.UserMapper;
import com.project.demo.code.service.DemoMerchantsService;
import com.project.demo.common.Result;
import com.project.demo.common.exception.DemoProjectException;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 商家表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Service
@Slf4j
public class DemoMerchantsServiceImpl extends ServiceImpl<DemoMerchantsMapper, DemoMerchants> implements DemoMerchantsService {

    @Resource
    private DemoMerchantsMapper merchantsMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DemoMerchantSchedulesMapper merchantSchedulesMapper;

    /**
     * 商家表分页查询
     * @param command 查询条件
     * @return 结果
     */
    @Override
    public Result<IPage<DemoMerchants>> selectList(MerchantsQueryCommand command) {
        log.info("分页查询商家信息，参数：{}", JSONUtil.toJsonStr(command));
        // 构建分页参数
        Page<DemoMerchants> page = new Page<>(command.getPageNum(), command.getPageSize());
        // 执行查询，并且返回结果
        return Result.success(merchantsMapper.page(page, command));
    }

    /**
     * 新增商家
     *
     * @param command 商家
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> add(MerchantsExecuteCommand command) {
        log.info("新增商家信息: {}", JSONUtil.toJsonStr(command));

        // 1. 校验参数
        String errorMsg = validateParameters(command, true);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }

        // 2. 组装商家数据
        DemoMerchants merchants = buildMerchants(command);

        // 3. 获取当前用户
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户信息异常");
        }
        merchants.setCreateBy(userId.toString());
        merchants.setUpdateBy(userId.toString());

        // 4. 插入商家数据
        if (merchantsMapper.insert(merchants) <= 0) {
            return Result.error();
        }

        // 5. 插入商家营业时间配置
        List<MerchantSchedulesDTO> merchantSchedules = command.getMerchantSchedules();
        if (CollUtil.isNotEmpty(merchantSchedules)) {
            List<DemoMerchantSchedules> schedulesList = merchantSchedules.stream()
                    .filter(schedule -> schedule.getDayOfWeek() != null && schedule.getIsHoliday() != null)
                    .map(schedule -> getMerchantSchedules(schedule, merchants, userId))
                    .toList();

            if (CollUtil.isEmpty(schedulesList)) {
                log.warn("商家营业时间配置中部分数据不完整，未插入");
            } else {
                merchantSchedulesMapper.insertBatch(schedulesList);
                log.info("新增商家营业时间配置，成功条数：{}", schedulesList.size());
            }
        }

        return Result.success("商家新增成功");
    }

    /**
     * 编辑商家
     *
     * @param command 商家信息
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> edit(MerchantsExecuteCommand command) {
        log.info("编辑商家信息: {}", JSONUtil.toJsonStr(command));

        // 1. 校验参数
        String errorMsg = validateParameters(command, false);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }

        // 2. 查询商家信息
        DemoMerchants merchants = merchantsMapper.getMerchantsById(command.getMerchantId());
        if (merchants == null) {
            return Result.error("商家不存在");
        }

        // 3. 校验商家所有者与状态
        if (!Objects.equals(merchants.getUserId(), command.getUserId())) {
            return Result.error("商家所有者不能修改");
        }
        if (merchants.getStatus() != 0) {
            return Result.error("商家状态暂不支持编辑");
        }

        // 4. 获取当前用户
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error("用户信息异常");
        }

        // 5. 更新商家信息
        updateMerchants(merchants, command);
        merchants.setUpdateBy(userId.toString());
        // 6. 批量更新营业时间配置
        List<MerchantSchedulesDTO> schedules = command.getMerchantSchedules();
        if (CollUtil.isNotEmpty(schedules)) {
            List<DemoMerchantSchedules> schedulesList = schedules.stream()
                    .filter(schedule -> schedule.getScheduleId() != null && schedule.getDayOfWeek() != null && schedule.getIsHoliday() != null)
                    .map(schedule -> getMerchantSchedules(schedule, merchants, userId))
                    .toList();

            merchantSchedulesMapper.updateBatch(schedulesList);
            log.info("批量更新商家营业时间配置，成功条数：{}", schedulesList.size());
        }

        return Result.success("商家编辑成功");
    }

    /**
     * 删除商家
     *
     * @param command 商家信息
     * @return 结果
     */
    @Override
    public Result<String> delete(MerchantsIdCommand command) {
        log.info("删除商家信息:{}", JSONUtil.toJsonStr(command));
        if (command == null || command.getMerchantId() == null) {
            return Result.error("商家ID不能为空");
        }
        if (merchantsMapper.deleteById(command.getMerchantId()) > 0) {
            return Result.success();
        }
        return null;
    }

    /**
     * 根据商家ID获取商家信息
     *
     * @param merchantId 商家ID
     * @return 结果
     */
    @Override
    public MerchantsDTO getMerchantsId(Long merchantId) {
        // 获取商家信息
        MerchantsDTO merchants = merchantsMapper.selectMerchantsById(merchantId);
        if (merchants == null) {
            throw new DemoProjectException("商家不存在");
        }
        // 获取商家营业时间配置
        List<MerchantSchedulesDTO> schedules = merchantSchedulesMapper.selectListByMerchantId(merchantId);
        if (CollUtil.isNotEmpty(schedules)) {
            merchants.setMerchantSchedules(schedules);
        }
        return merchants;
    }

    /**
     * 验证参数
     *
     * @param command 参数
     * @param flag    是否新增 新增 or 编辑   true 新增 false 编辑
     * @return 结果
     */
    private String validateParameters(MerchantsExecuteCommand command, Boolean flag) {
        if (command == null) {
            return "参数为空";
        }
        if (StrUtil.isBlank(command.getMerchantName())) {
            return "商家名称不能为空";
        }
        if (!flag && command.getMerchantId() == null) {
            return "商家ID不能为空";
        }
        // 具体业务验证
        return verificationMerchantsBusiness(command, flag);
    }

    /**
     * 商家业务验证
     *
     * @param command 参数
     * @return 结果
     */
    private String verificationMerchantsBusiness(MerchantsExecuteCommand command, Boolean flag) {
        // 2、是否为自己添加
        if (command.getIsSelf() == 1 && flag) {
            // 为自己添加
            // 获取当前登录用户ID
            Long userId = SecurityUtils.getCurrentUserId();
            // TODO 后期可能需要验证用户资质
            // 设置当前登录用户ID
            command.setUserId(userId);
        } else {
            // 为别人添加
            if (command.getUserId() == null) {
                return "用户ID不能为空";
            }
            // 检查用户是否存在
            DemoUser user = userMapper.selectById(command.getUserId());
            if (user == null) {
                return "用户不存在";
            }
            // TODO 后期可能需要验证用户资质
        }
        return null;
    }


    /**
     * 获取需要插入的商家营业时间配置数据
     *
     * @param merchantSchedule 商家营业时间配置
     * @param merchants        商家信息
     * @param userId           用户ID
     * @return 结果
     */
    private DemoMerchantSchedules getMerchantSchedules(MerchantSchedulesDTO merchantSchedule, DemoMerchants merchants, Long userId) {
        DemoMerchantSchedules merchantSchedulesEntity = new DemoMerchantSchedules();
        merchantSchedulesEntity.setMerchantId(merchants.getMerchantId());
        merchantSchedulesEntity.setDayOfWeek(merchantSchedule.getDayOfWeek());
        merchantSchedulesEntity.setIsHoliday(merchantSchedule.getIsHoliday());
        merchantSchedulesEntity.setOpenTime(merchantSchedule.getOpenTime());
        merchantSchedulesEntity.setCloseTime(merchantSchedule.getCloseTime());
        merchantSchedulesEntity.setCreateBy(userId.toString());
        merchantSchedulesEntity.setUpdateBy(userId.toString());
        return merchantSchedulesEntity;
    }

    /**
     * 构建商家信息
     *
     * @param command 参数
     * @return 结果
     */
    private DemoMerchants buildMerchants(MerchantsExecuteCommand command) {
        DemoMerchants merchants = new DemoMerchants();
        merchants.setMerchantName(command.getMerchantName());
        merchants.setAddress(command.getAddress());
        merchants.setContactName(command.getContactName());
        merchants.setContactPhone(command.getContactPhone());
        merchants.setEmail(command.getEmail());
        merchants.setBusinessLicense(command.getBusinessLicense());
        merchants.setUserId(command.getUserId());
        merchants.setStatus(command.getStatus());
        return merchants;
    }

    /**
     * 更新商家信息
     *
     * @param merchants 商家信息
     * @param command   参数
     */
    private void updateMerchants(DemoMerchants merchants, MerchantsExecuteCommand command) {
        if (StrUtil.isNotBlank(command.getMerchantName())) {
            merchants.setMerchantName(command.getMerchantName());
        }
        if (StrUtil.isNotBlank(command.getAddress())) {
            merchants.setAddress(command.getAddress());
        }
        if (StrUtil.isNotBlank(command.getContactName())) {
            merchants.setContactName(command.getContactName());
        }
        if (StrUtil.isNotBlank(command.getContactPhone())) {
            merchants.setContactPhone(command.getContactPhone());
        }
        if (StrUtil.isNotBlank(command.getEmail())) {
            merchants.setEmail(command.getEmail());
        }
        if (StrUtil.isNotBlank(command.getBusinessLicense())) {
            merchants.setBusinessLicense(command.getBusinessLicense());
        }
        if (command.getStatus() != null) {
            merchants.setStatus(command.getStatus());
        }
    }

}
