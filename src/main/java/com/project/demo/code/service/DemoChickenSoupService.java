package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.domain.DemoChickenSoup;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.common.Result;

import java.util.List;

/**
 * <p>
 * 鸡汤表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2025年04月07日 17:33:29
 */
public interface DemoChickenSoupService extends IService<DemoChickenSoup> {

        /**
         * 根据ID查询
         */
        DemoChickenSoup getById(Integer id);
        /**
         * 查询所有字段（支持动态条件）
         */
        // 查询所有，支持动态条件
        Result<IPage<DemoChickenSoup>> queryAll(DemoChickenSoup vo);
        /**
         * 根据ID更新数据
         */
        Integer update(DemoChickenSoup vo);

        /**
         * 根据ID删除数据（物理删除）
         */
        Integer deleteById(Integer id);

        /**
         * 新增数据
         */
        Integer insert(DemoChickenSoup vo);

}
