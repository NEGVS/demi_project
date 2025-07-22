package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.demo.CategoriesIdCommand;
import com.project.demo.code.command.demo.CategoriesQueryCommand;
import com.project.demo.code.domain.DemoCategories;
import com.project.demo.common.Result;

/**
 * <p>
 * 商品分类表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
public interface DemoCategoriesService extends IService<DemoCategories> {

    Result<String> add(DemoCategories command);

    Result<IPage<DemoCategories>> selectList(CategoriesQueryCommand command);

    Result<String> edit(DemoCategories command);

    Result<String> deleted(CategoriesIdCommand command);
}
