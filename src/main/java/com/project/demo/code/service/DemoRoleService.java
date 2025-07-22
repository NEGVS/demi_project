package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.system.role.RoleIdCommand;
import com.project.demo.code.command.system.role.RoleQueryCommand;
import com.project.demo.code.command.system.rolemenu.PermissionExecuteCommand;
import com.project.demo.code.domain.DemoRole;
import com.project.demo.common.Result;

/**
 * <p>
 * 角色信息表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2024年12月25日 16:44:06
 */
public interface DemoRoleService extends IService<DemoRole> {

    /**
     * 查询角色列表
     *
     * @param command 查询条件
     * @return 结果
     */
    Result<IPage<DemoRole>> selectList(RoleQueryCommand command);

    /**
     * 新增角色
     *
     * @param role 新增角色参数
     * @return 结果
     */
    Result<String> add(DemoRole role);

    /**
     * 编辑角色
     *
     * @param role 编辑角色参数
     * @return 结果
     */
    Result<String> edit(DemoRole role);

    /**
     * 删除角色
     *
     * @param command 删除角色参数
     * @return 结果
     */
    Result<String> deleted(RoleIdCommand command);

    /**
     * 获取角色详情
     *
     * @param command 获取角色详情参数
     * @return 结果
     */
    Result<DemoRole> getMenuById(RoleIdCommand command);

    /**
     * 对某个角色添加权限
     * @param command 设置角色权限参数
     * @return 结果
     */
    Result<String> addMenuByRoleId(PermissionExecuteCommand command);

    /**
     * 对某个角色移除权限
     * @param command 设置角色权限参数
     * @return 结果
     */
    Result<String> removeMenuByRoleId(PermissionExecuteCommand command);
}
