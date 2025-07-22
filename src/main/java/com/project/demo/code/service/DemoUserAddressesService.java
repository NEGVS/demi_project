package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.demo.UserAddressesIdCommand;
import com.project.demo.code.command.demo.UserAddressesQueryCommand;
import com.project.demo.code.domain.DemoUserAddresses;
import com.project.demo.common.Result;

import java.util.List;

/**
 * <p>
 * 用户收货地址表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:44:19
 */
public interface DemoUserAddressesService extends IService<DemoUserAddresses> {

    /**
     * 分页查询用户收货地址表
     * @param command 查询参数
     * @return 结果
     */
    Result<IPage<DemoUserAddresses>> selectList(UserAddressesQueryCommand command);

    /**
     * 新增收货地址
     * @param command 收货地址
     * @return 结果
     */
    Result<String> add(DemoUserAddresses command);

    /**
     * 编辑收货地址
     * @param command 收货地址
     * @return 结果
     */
    Result<String> edit(DemoUserAddresses command);

    /**
     * 删除
     * @param command id
     * @return 结果
     */
    Result<String> deleted(UserAddressesIdCommand command);

    /**
     * 设置默认地址
     * @param command id
     * @return 结果
     */
    Result<String> setIsDefault(UserAddressesIdCommand command);

    /**
     * 根据当前登录用户查询收货地址
     * @return 结果
     */
    Result<List<DemoUserAddresses>> getUserAddressesList();
}
