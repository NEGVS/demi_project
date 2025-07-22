package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.system.menu.MenuIdCommand;
import com.project.demo.code.command.system.menu.MenuQueryCommand;
import com.project.demo.code.domain.DemoMenu;
import com.project.demo.code.dto.rolemenu.RoleMenuDTO;
import com.project.demo.common.Result;

import java.util.List;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2024年12月24日 16:48:22
 */
public interface DemoMenuService extends IService<DemoMenu> {

    /**
     * 分页查询
     * @param command 查询参数
     * @return 结果
     */
    Result<IPage<DemoMenu>> selectList(MenuQueryCommand command);

    /**
     * 新增菜单
     * @param menu 新增菜单参数
     * @return 结果
     */
    Result<String> add(DemoMenu menu);

    /**
     * 修改菜单
     * @param menu 修改菜单参数
     * @return 结果
     */
    Result<String> edit(DemoMenu menu);

    /**
     * 删除菜单
     * @param command 菜单删除参数
     * @return 结果
     */
    Result<String> deleted(MenuIdCommand command);

    /**
     *  获取菜单详情
     * @param command 菜单ID
     * @return 结果
     */
    Result<DemoMenu> getMenuById(MenuIdCommand command);

    /**
     * 获取当前登录用户的菜单信息 （无层级的）
     * @return 结果
     */
    Result<List<DemoMenu>> getMenuByLoinUser();

    /**
     * 获取当前登录用户的菜单信息 (有层级的)
     * @return 结果
     */
    Result<List<RoleMenuDTO>> getMenuByRoleId();
}
