package com.project.demo.code.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.demo.UserAddressesIdCommand;
import com.project.demo.code.command.demo.UserAddressesQueryCommand;
import com.project.demo.code.domain.DemoUserAddresses;
import com.project.demo.code.mapper.DemoUserAddressesMapper;
import com.project.demo.code.service.DemoUserAddressesService;
import com.project.demo.common.Result;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户收货地址表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:44:19
 */
@Service
@Slf4j
public class DemoUserAddressesServiceImpl extends ServiceImpl<DemoUserAddressesMapper, DemoUserAddresses> implements DemoUserAddressesService {

    @Resource
    private DemoUserAddressesMapper userAddressesMapper;

    /**
     * 查询用户收货地址列表
     *
     * @param command 查询参数
     * @return 结果
     */
    @Override
    public Result<IPage<DemoUserAddresses>> selectList(UserAddressesQueryCommand command) {
        // 创建分页对象，参数分别是：当前页数(page)、每页显示条数(size)
        Page<DemoUserAddresses> page = new Page<>(command.getPageNum(), command.getPageSize());
        // 执行分页查询
        return Result.success(userAddressesMapper.page(page, command));
    }

    /**
     * 新增用户收货地址
     *
     * @param command 收货地址
     * @return 结果
     */
    @Override
    public Result<String> add(DemoUserAddresses command) {
        String errorMsg = validateParameters(command, true);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        String loginName = SecurityUtils.getCurrentUsername();
        if (StrUtil.isBlank(loginName)) {
            return Result.error(401, "请先登录");
        }
        command.setUserId(SecurityUtils.getCurrentUserId());
        command.setCreateBy(loginName);
        command.setUpdateBy(loginName);
        if (userAddressesMapper.insert(command) > 0) {
            return Result.success();
        }
        return null;
    }

    /**
     * 编辑用户收货地址
     *
     * @param command 收货地址
     * @return 结果
     */
    @Override
    public Result<String> edit(DemoUserAddresses command) {
        String errorMsg = validateParameters(command, false);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        String loginName = SecurityUtils.getCurrentUsername();
        if (StrUtil.isBlank(loginName)) {
            return Result.error(401, "请先登录");
        }
        command.setUserId(SecurityUtils.getCurrentUserId());
        command.setUpdateBy(loginName);
        command.setUpdateDate(new Date());

        // 查询当前版本号
        DemoUserAddresses dbEntity = userAddressesMapper.selectById(command.getAddressId());
        if (dbEntity == null) {
            return Result.error("数据不存在");
        }
        command.setVersion(dbEntity.getVersion()); // 设置当前版本号

        // 更新
        int rows = userAddressesMapper.updateById(command);
        if (rows > 0) {
            return Result.success();
        }
        return Result.error();
    }

    @Override
    public Result<String> deleted(UserAddressesIdCommand command) {
        String errorMsg = validateParamId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        if (userAddressesMapper.deleteUserAddressById(command) > 0) {
            return Result.success();
        }
        return null;
    }

    /**
     * 设置默认地址
     *
     * @param command id
     * @return 结果
     */
    @Override
    public Result<String> setIsDefault(UserAddressesIdCommand command) {
        String errorMsg = validateParamId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        userAddressesMapper.updateNoDefaultById(command, userId);
        if (userAddressesMapper.updateYesDefaultById(command, userId) > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 查询用户收货地址列表
     */
    @Override
    public Result<List<DemoUserAddresses>> getUserAddressesList() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        List<DemoUserAddresses> list = userAddressesMapper.getUserAddressByUserId(userId);
        return Result.success(list);
    }

    /**
     * 参数校验
     *
     * @param command 入参
     * @param flag    新增 or 编辑   true 新增 false 编辑
     * @return 结果
     */
    private String validateParameters(DemoUserAddresses command, Boolean flag) {
        if (command == null) {
            return "参数为空";
        }
        if (!flag && command.getAddressId() == null) {
            return "主键不能为空";
        }
        if (StrUtil.isBlank(command.getReceiverName())) {
            return "收货人姓名不能为空";
        }
        if (StrUtil.isBlank(command.getPhoneNumber())) {
            return "收货人手机号不能为空";
        }
        if (StrUtil.isBlank(command.getProvince())) {
            return "省份不能为空";
        }
        if (StrUtil.isBlank(command.getCity())) {
            return "城市不能为空";
        }
        if (StrUtil.isBlank(command.getDistrict())) {
            return "区/县不能为空";
        }
        if (StrUtil.isBlank(command.getDetailedAddress())) {
            return "详细地址不能为空";
        }
        return null;
    }


    private String validateParamId(UserAddressesIdCommand command) {
        if (command == null || command.getAddressId() == null) {
            return "ID为空";
        }
        return null;
    }
}
