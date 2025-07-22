package com.project.demo.code.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.system.role.RoleIdCommand;
import com.project.demo.code.command.system.role.RoleQueryCommand;
import com.project.demo.code.command.system.rolemenu.PermissionExecuteCommand;
import com.project.demo.code.domain.DemoRole;
import com.project.demo.code.mapper.DemoRoleMapper;
import com.project.demo.code.service.DemoRoleService;
import com.project.demo.common.Result;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色信息表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2024年12月25日 16:44:06
 */
@Service
@Slf4j
public class DemoRoleServiceImpl extends ServiceImpl<DemoRoleMapper, DemoRole> implements DemoRoleService {

    @Resource
    private DemoRoleMapper roleMapper;

    /**
     * 查询角色列表
     *
     * @param command 查询条件
     * @return 结果
     */
    @Override
    public Result<IPage<DemoRole>> selectList(RoleQueryCommand command) {
        // 创建分页对象，参数分别是：当前页数(page)、每页显示条数(size)
        Page<DemoRole> page = new Page<>(command.getPageNum(), command.getPageSize());
        // 执行分页查询
        IPage<DemoRole> userPage = roleMapper.page(page, command);
        return Result.success(userPage);
    }

    /**
     * 新增角色
     * 1、验证参数
     * 2、验证必须的条件
     * 3、执行新增
     *
     * @param role 新增角色参数
     * @return 结果
     */
    @Override
    public Result<String> add(DemoRole role) {
        // 1、验证参数，方法名
        String errorMsg = validateParamRole(role, true);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        String loginName = SecurityUtils.getCurrentUsername();
        if (StrUtil.isBlank(loginName)){
            return Result.error(401, "请先登录");
        }
        role.setCreateBy(loginName);
        role.setUpdateBy(loginName);
        // 2、执行新增
        int row = roleMapper.insert(role);
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 编辑角色
     *
     * @param role 编辑角色参数
     * @return 结果
     */
    @Override
    public Result<String> edit(DemoRole role) {
        // 1、验证参数，方法名
        String errorMsg = validateParamRole(role, false);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        String loginName = SecurityUtils.getCurrentUsername();
        role.setUpdateBy(loginName);
        int row = roleMapper.updateById(role);
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 删除角色
     *
     * @param command 删除角色参数
     * @return 结果
     */
    @Override
    public Result<String> deleted(RoleIdCommand command) {
        // 1、参数验证
        String errorMsg = validateParamRoleId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 2、删除菜单
        int row = roleMapper.deleteById(command.getRoleId());
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 获取角色详情
     *
     * @param command 获取角色详情参数
     * @return 结果
     */
    @Override
    public Result<DemoRole> getMenuById(RoleIdCommand command) {
        // 1、验证参数
        String errorMsg = validateParamRoleId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 2、查询结果
        return Result.success(roleMapper.findRoleByRoleId(command.getRoleId()));
    }

    /**
     * 对某个角色添加权限
     *
     * @param command 设置角色权限参数
     * @return 结果
     */
    @Override
    public Result<String> addMenuByRoleId(PermissionExecuteCommand command) {
        // 1、校验参数
        String errorMsg = validateParamRoleId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 2、执行添加
        int row = roleMapper.insertRoleMenu(command.getRoleId(),command.getMenuIdList());
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 对某个角色移除权限
     *
     * @param command 设置角色权限参数
     * @return 结果
     */
    @Override
    public Result<String> removeMenuByRoleId(PermissionExecuteCommand command) {
        // 1、校验参数
        String errorMsg = validateParamRoleId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 2、执行移除
        int row = roleMapper.removeMenuByRoleId(command.getRoleId(),command.getMenuIdList());
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 验证参数
     *
     * @param role 入参
     * @return 结果
     */
    private String validateParamRole(DemoRole role, Boolean flag) {
        if (role == null) {
            return "参数为空";
        }
        if (StrUtil.isBlank(role.getRoleName())) {
            return "角色名称为空";
        }
        if (StrUtil.isBlank(role.getRoleKey())) {
            return "角色权限为空";
        }
        if (ObjectUtil.isNull(role.getRoleSort())) {
            return "角色显示顺序为空";
        }
        if (StrUtil.isBlank(role.getStatus())) {
            return "角色状态为空";
        }
        String errorMsg = verificationRoleBusiness(role, flag);
        if (StrUtil.isNotBlank(errorMsg)){
            throw new RuntimeException(errorMsg);
        }
        return errorMsg;
    }

    /**
     * 业务校验
     *
     * @param role 入参
     * @param flag 新增 or 编辑   true 新增 false 编辑
     * @return 结果
     */
    private String verificationRoleBusiness(DemoRole role, Boolean flag) {
        if (!flag) {
            if (role.getRoleId() == null) {
                return "角色ID为空";
            }
        }
        // 2.1、判断当前角色名称是否已经存在
        LambdaQueryWrapper<DemoRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DemoRole::getRoleName, role.getRoleName());
        // 用来判断编辑的时候
        queryWrapper.ne(role.getRoleId() != null, DemoRole::getRoleId, role.getRoleId());
        if (roleMapper.selectCount(queryWrapper) > 0) {
            return "角色名称已经存在";
        }
        return null;
    }

    /**
     * 验证参数 ( 针对角色ID )
     *
     * @param command 入参
     * @return 结果
     */
    private String validateParamRoleId(RoleIdCommand command) {
        if (command == null || command.getRoleId() == null) {
            return "角色ID为空";
        }
        return null;
    }

    private String validateParamRoleId(PermissionExecuteCommand command) {
        if (command == null || command.getRoleId() == null) {
            return "角色ID为空";
        }
        if (CollUtil.isEmpty(command.getMenuIdList())) {
            return "权限ID为空";
        }
        return null;
    }
}
