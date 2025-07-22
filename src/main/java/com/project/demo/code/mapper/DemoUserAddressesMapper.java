package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.demo.code.command.demo.UserAddressesIdCommand;
import com.project.demo.code.command.demo.UserAddressesQueryCommand;
import com.project.demo.code.domain.DemoUserAddresses;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户收货地址表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:44:19
 */
@Mapper
public interface DemoUserAddressesMapper extends BaseMapper<DemoUserAddresses> {

    /**
     * 分页查询用户收货地址表
     * @param page 分页对象
     * @param command 参数对象
     * @return 结果
     */
    IPage<DemoUserAddresses> page(@Param("page") Page<DemoUserAddresses> page, @Param("command") UserAddressesQueryCommand command);

    /**
     * 删除用户地址
     * @param command 地址ID
     * @return 结果
     */
    int deleteUserAddressById(@Param("command") UserAddressesIdCommand command);

    /**
     * 将其他的地址设置为非默认地址
     * @param command id
     * @param userId 用户id
     * @return 结果
     */
    int updateNoDefaultById(@Param("command") UserAddressesIdCommand command ,@Param("userId") Long userId);

    /**
     * updateYesDefaultById
     * @param command id
     * @param userId 用户id
     * @return 结果
     */
    int updateYesDefaultById(@Param("command") UserAddressesIdCommand command ,@Param("userId") Long userId);

    /**
     * 根据用户id查询地址
     * @param userId 用户id
     */
    List<DemoUserAddresses> getUserAddressByUserId(@Param("userId") Long userId);
}
