package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.demo.code.command.system.menu.MenuQueryCommand;
import com.project.demo.code.domain.DemoMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.demo.code.dto.rolemenu.RoleMenuDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2024年12月24日 16:48:22
 */
@Mapper
public interface DemoMenuMapper extends BaseMapper<DemoMenu> {

    /**
     * 分页查询菜单表
     * @param page 分页对象
     * @param command 参数对象
     * @return 结果
     */
    IPage<DemoMenu> page(@Param("page") Page<DemoMenu> page, @Param("command") MenuQueryCommand command);

    /**
     * 根据菜单ID查询菜单
     * @param menuId 菜单ID
     * @return 结果
     */
    DemoMenu findMenuByMenuId(@Param("menuId") Long menuId);

    /**
     * 根据当前用户信息获取菜单信息
     * @return 结果
     */
    List<DemoMenu> getMenuByLoinUser(@Param("userId") Long userId);

    /**
     * 根据当前登录的用户信息获取对应的菜单信息
     * @param userId 用户ID
     * @return 结果
     */
    List<RoleMenuDTO> getMenuByRoleId(@Param("userId")Long userId);
}
