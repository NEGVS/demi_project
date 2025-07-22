package com.project.demo.code.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.system.menu.MenuIdCommand;
import com.project.demo.code.command.system.menu.MenuQueryCommand;
import com.project.demo.code.domain.DemoMenu;
import com.project.demo.code.dto.rolemenu.RoleMenuDTO;
import com.project.demo.code.mapper.DemoMenuMapper;
import com.project.demo.code.service.DemoMenuService;
import com.project.demo.common.Result;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2024年12月24日 16:48:22
 */
@Service
@Slf4j
public class DemoMenuServiceImpl extends ServiceImpl<DemoMenuMapper, DemoMenu> implements DemoMenuService {

    @Resource
    private DemoMenuMapper menuMapper;

    /**
     * 分页查询
     *
     * @param command 查询参数
     * @return 结果
     */
    @Override
    public Result<IPage<DemoMenu>> selectList(MenuQueryCommand command) {
        // 创建分页对象，参数分别是：当前页数(page)、每页显示条数(size)
        Page<DemoMenu> page = new Page<>(command.getPageNum(), command.getPageSize());
        // 执行分页查询
        return Result.success(menuMapper.page(page, command));
    }

    /**
     * 新增菜单
     * 1、验证参数
     * 2、验证必须的条件
     * 3、执行新增
     *
     * @param menu 新增菜单参数
     * @return 结果
     */
    @Override
    public Result<String> add(DemoMenu menu) {
        // 1、验证参数，方法名
        String errorMsg = validateParameters(menu,true);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        String loginName = SecurityUtils.getCurrentUsername();
        menu.setCreateBy(loginName);
        menu.setUpdateBy(loginName);
        // 3、执行新增
        int row = menuMapper.insert(menu);
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 修改菜单
     *
     * @param menu 修改菜单参数
     * @return 结果
     */
    @Override
    public Result<String> edit(DemoMenu menu) {
        // 1、验证参数，方法名
        String errorMsg = validateParameters(menu,false);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        String loginName = SecurityUtils.getCurrentUsername();
        menu.setUpdateBy(loginName);
        int row = menuMapper.updateById(menu);
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 删除菜单
     *
     * @param command 菜单删除参数
     * @return 结果
     */
    @Override
    public Result<String> deleted(MenuIdCommand command) {
        // 1、参数验证
        String errorMsg = validateParamMenuId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 2、检测当前菜单下是否有别的菜单
        if (menuMapper.selectCount(new LambdaQueryWrapper<DemoMenu>().eq(DemoMenu::getParentId, command.getMenuId())) > 0) {
            return Result.error("当前菜单下有子菜单，不能删除");
        }
        // 3、删除菜单
        int row = menuMapper.deleteById(command.getMenuId());
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 获取菜单详情
     *
     * @param command 菜单ID
     * @return 结果
     */
    @Override
    public Result<DemoMenu> getMenuById(MenuIdCommand command) {
        // 1、验证参数
        String errorMsg = validateParamMenuId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 2、查询结果
        return Result.success(menuMapper.findMenuByMenuId(command.getMenuId()));
    }

    /**
     * 获取当前用户菜单信息 （无层级的）
     *
     * @return 结果
     */
    @Override
    public Result<List<DemoMenu>> getMenuByLoinUser() {
        // 1、获取登录的用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        // 2、获取当前登录用户的角色
        return Result.success(menuMapper.getMenuByLoinUser(userId));
    }

    /**
     * 获取角色菜单信息 （有层级的）
     *
     * @return 结果
     */
    @Override
    public Result<List<RoleMenuDTO>> getMenuByRoleId() {
        // 1、获取登录的用户ID
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return Result.error(401, "请先登录");
        }
        // 2、查询当前用户的所有角色,以及对应的菜单
        List<RoleMenuDTO> menuList = menuMapper.getMenuByRoleId(userId);
        // 如果无数据，直接返回
        if (CollUtil.isEmpty(menuList)) {
            return Result.success();
        }
        // 3、递归处理成有层级关系的结构，父级菜单id是0的为主菜单
        return Result.success(selectMenuAllList(menuList));
    }

    /**
     * 验证参数
     *
     * @param menu 入参
     * @return 结果
     */
    private String validateParameters(DemoMenu menu,Boolean flag) {
        if (menu == null) {
            return "参数为空";
        }
        if (StrUtil.isBlank(menu.getMenuName())) {
            return "菜单名称为空";
        }
        return verificationMenuBusiness(menu , flag);
    }

    /**
     * 业务校验
     *
     * @param menu 入参
     * @param flag 新增 or 编辑   true 新增 false 编辑
     * @return 结果
     */
    private String verificationMenuBusiness(DemoMenu menu, Boolean flag) {
        // 2、判断父菜单是否有值
        if (menu.getParentId() != null && menu.getParentId() != 0) {
            // 判断父菜单是否存在
            if (menuMapper.selectCount(new LambdaQueryWrapper<DemoMenu>().eq(DemoMenu::getMenuId, menu.getParentId())) == 0) {
                return "父菜单不存在";
            }
        }
        if (!flag) {
            if (menu.getMenuId() == null) {
                return "菜单ID为空";
            }
        }
        // 2.1、判断当前菜单名称是否已经存在
        LambdaQueryWrapper<DemoMenu> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DemoMenu::getMenuName, menu.getMenuName());
        // 用来判断编辑的时候
        queryWrapper.ne(menu.getMenuId() != null, DemoMenu::getMenuId, menu.getMenuId());
        if (menuMapper.selectCount(queryWrapper) > 0) {
            return "菜单名称已经存在";
        }
        if (flag){
            menu.setCreateDate(new Date());
        }
        menu.setUpdateDate(new Date());
        return null;
    }

    /**
     * 验证参数 ( 针对菜单ID )
     *
     * @param command 入参
     * @return 结果
     */
    private String validateParamMenuId(MenuIdCommand command) {
        if (command == null || command.getMenuId() == null) {
            return "菜单ID为空";
        }
        return null;
    }


    /**
     * 递归查找当前菜单的子菜单
     *
     * @param root 当前菜单
     * @param all  所有的菜单
     * @return 当前菜单的子菜单
     */
    private List<RoleMenuDTO> getChildrenList(RoleMenuDTO root, List<RoleMenuDTO> all) {
        return all.stream().filter(children -> ObjectUtil.isNotNull(children.getParentId()) && children.getParentId().equals(root.getMenuId())).peek(children -> children.setChildrenList(getChildrenList(children, all))).collect(Collectors.toList());
    }

    /**
     * 递归查找所有菜单的子菜单
     *
     * @param menuList 菜单列表
     * @return 结果
     */
    private List<RoleMenuDTO> selectMenuAllList(List<RoleMenuDTO> menuList) {
        return menuList.stream().filter(menu -> menu.getParentId() != null ).peek((menu) -> menu.setChildrenList(getChildrenList(menu, menuList))).collect(Collectors.toList());
    }
}
