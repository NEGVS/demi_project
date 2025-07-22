package com.project.demo.code.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.domain.DemoChickenSoup;
import com.project.demo.code.mapper.DemoChickenSoupMapper;
import com.project.demo.code.service.DemoChickenSoupService;
import com.project.demo.common.Result;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 鸡汤表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2025年04月07日 17:33:29
 */
@Service
public class DemoChickenSoupServiceImpl extends ServiceImpl<DemoChickenSoupMapper, DemoChickenSoup> implements DemoChickenSoupService {

    @Resource
    private DemoChickenSoupMapper demochickensoupMapper;

    /**
     * 根据ID查询
     */
    @Override
    public DemoChickenSoup getById(Integer id) {
        return demochickensoupMapper.selectById(id);
    }

    /**
     * 查询所有字段（支持动态条件）
     */
    @Override
    public Result<IPage<DemoChickenSoup>> queryAll(DemoChickenSoup vo) {
        // 分页查询
        // 使用mybatisPlus的分页查询，并且构造分页查询参数
        // 构建分页参数
        Page<DemoChickenSoup> page = new Page<>(vo.getPageNum(), vo.getPageSize());
        // 执行查询，并且返回结果
        return Result.success(demochickensoupMapper.selectPage(page, null));
    }

    /**
     * 根据ID更新数据
     */
    @Override
    public Integer update(DemoChickenSoup record) {
        return demochickensoupMapper.updateById(record);
    }

    /**
     * 根据ID删除数据（物理删除）
     */
    @Override
    public Integer deleteById(Integer id) {
        return demochickensoupMapper.deleteById(id);
    }

    /**
     * 新增数据
     */
    @Override
    public Integer insert(DemoChickenSoup vo) {
        return demochickensoupMapper.insert(vo);
    }
}
