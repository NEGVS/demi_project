package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.demo.code.command.demo.CategoriesQueryCommand;
import com.project.demo.code.domain.DemoCategories;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商品分类表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Mapper
public interface DemoCategoriesMapper extends BaseMapper<DemoCategories> {

    IPage<DemoCategories> page(@Param("page") Page<DemoCategories> page, @Param("command") CategoriesQueryCommand command);

    int deleted(@Param("categoryId") Long categoryId, @Param("updateBy") String updateBy);
}
