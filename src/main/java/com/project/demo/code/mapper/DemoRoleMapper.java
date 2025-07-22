package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.demo.code.command.system.role.RoleQueryCommand;
import com.project.demo.code.domain.DemoRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 角色信息表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2024年12月25日 16:44:06
 */
@Mapper
public interface DemoRoleMapper extends BaseMapper<DemoRole> {

    /**
     * 分页查询角色表
     *
     * @param page    分页对象
     * @param command 参数对象
     * @return 结果
     */
    IPage<DemoRole> page(@Param("page") Page<DemoRole> page, @Param("command") RoleQueryCommand command);

    /**
     * 根据角色ID查询角色
     *
     * @param roleId 角色ID
     * @return 结果
     */
    DemoRole findRoleByRoleId(@Param("roleId") Long roleId);

    /**
     * 批量插入角色菜单权限
     * @param roleId 角色ID
     * @param menuIdList 角色菜单列表
     * @return 结果
     */
    int insertRoleMenu(@Param("roleId") Long roleId,@Param("menuIdList") List<Long> menuIdList);
    /**
     * 批量移除角色菜单权限
     * @param roleId 角色ID
     * @param menuIdList 角色菜单列表
     * @return 结果
     */
    int removeMenuByRoleId(@Param("roleId") Long roleId,@Param("menuIdList") List<Long> menuIdList);
}
