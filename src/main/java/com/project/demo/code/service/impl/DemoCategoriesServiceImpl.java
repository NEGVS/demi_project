package com.project.demo.code.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.demo.CategoriesIdCommand;
import com.project.demo.code.command.demo.CategoriesQueryCommand;
import com.project.demo.code.domain.DemoCategories;
import com.project.demo.code.mapper.DemoCategoriesMapper;
import com.project.demo.code.service.DemoCategoriesService;
import com.project.demo.common.Result;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Service
public class DemoCategoriesServiceImpl extends ServiceImpl<DemoCategoriesMapper, DemoCategories> implements DemoCategoriesService {

    @Resource
    private DemoCategoriesMapper categoriesMapper;

    @Override
    public Result<String> add(DemoCategories command) {
        if (command == null) {
            return Result.error("参数错误");
        }
        if (StrUtil.isBlank(command.getCategoryName())) {
            return Result.error("分类名称不能为空");
        }
        if (command.getParentCategoryId() == null) {
            command.setParentCategoryId(0L);
        }
        if (categoriesMapper.selectCount(new LambdaQueryWrapper<DemoCategories>().eq(DemoCategories::getCategoryName, command.getCategoryName())) > 0) {
            return Result.error("分类名称已存在");
        }
        command.setCreateBy(SecurityUtils.getLoginUserId());
        command.setUpdateBy(SecurityUtils.getLoginUserId());
        int row = categoriesMapper.insert(command);
        if (row > 0) {
            return Result.success();
        }
        return Result.error();
    }

    @Override
    public Result<IPage<DemoCategories>> selectList(CategoriesQueryCommand command) {
        // 创建分页对象，参数分别是：当前页数(page)、每页显示条数(size)
        Page<DemoCategories> page = new Page<>(command.getPageNum(), command.getPageSize());
        // 执行分页查询
        return Result.success(categoriesMapper.page(page, command));
    }

    @Override
    public Result<String> edit(DemoCategories command) {
        if (command == null || command.getCategoryId() == null) {
            return Result.error("参数错误");
        }
        if (StrUtil.isBlank(command.getCategoryName())) {
            return Result.error("分类名称不能为空");
        }
        if (command.getParentCategoryId() == null) {
            command.setParentCategoryId(0L);
        }
        // 查询分类名称是否已存在
        LambdaQueryWrapper<DemoCategories> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DemoCategories::getCategoryName, command.getCategoryName());
        queryWrapper.ne(DemoCategories::getCategoryId, command.getCategoryId());
        if (categoriesMapper.selectCount(queryWrapper) > 0) {
            return Result.error("分类名称已存在");
        }
        command.setUpdateBy(SecurityUtils.getLoginUserId());
        if (categoriesMapper.updateById(command) > 0) {
            return Result.success();
        }
        return Result.error();
    }

    @Override
    public Result<String> deleted(CategoriesIdCommand command) {
        if (command == null || command.getCategoryId() == null) {
            return Result.error("参数错误");
        }
        if (categoriesMapper.deleted(command.getCategoryId() , SecurityUtils.getLoginUserId()) > 0) {
            return Result.success();
        }
        return Result.error();
    }
}
