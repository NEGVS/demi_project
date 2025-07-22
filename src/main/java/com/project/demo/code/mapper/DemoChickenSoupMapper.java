package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.demo.code.domain.DemoChickenSoup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 鸡汤表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年04月07日 17:33:29
 */
@Mapper
public interface DemoChickenSoupMapper extends BaseMapper<DemoChickenSoup> {

    /**
     * 根据ID查询
     */
    DemoChickenSoup selectById(@Param("id") Integer id);

    /**
     * 查询所有字段（支持动态条件）
     */
    List<DemoChickenSoup> selectAll(DemoChickenSoup query);

    /**
     * 根据ID更新数据
     */
    int updateById(DemoChickenSoup vo);

    /**
     * 根据ID删除数据（物理删除）
     */
    int deleteById(@Param("id") Integer id);

    /**
     * 新增数据
     */
    int insert(DemoChickenSoup vo);
}
