package com.project.demo.code.controller.demo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.command.demo.CategoriesIdCommand;
import com.project.demo.code.command.demo.CategoriesQueryCommand;
import com.project.demo.code.domain.DemoCategories;
import com.project.demo.code.service.DemoCategoriesService;
import com.project.demo.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 商品分类表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@RestController
@RequestMapping("/demo/categories")
@Tag(name = "商品分类")
public class DemoCategoriesController {

    @Resource
    private DemoCategoriesService categoriesService;

    /**
     * 新增分类
     */
    @PostMapping("/add")
    @Operation(description = "新增分类", summary = "新增分类")
    public Result<String> add(@RequestBody DemoCategories command) {
        return categoriesService.add(command);
    }

    /**
     * 查询分类
     */
    @PostMapping("list")
    @Operation(description = "查询分类列表(分页)", summary = "查询分类列表(分页)")
    public Result<IPage<DemoCategories>> list (@RequestBody CategoriesQueryCommand command) {
        return categoriesService.selectList(command);
    }
    /**
     * 修改分类
     */
    @PostMapping("/edit")
    @Operation(description = "修改分类", summary = "修改分类")
    public Result<String> edit(@RequestBody DemoCategories command) {
        return categoriesService.edit(command);
    }
    /**
     * 删除分类
     */
    @PostMapping("/deleted")
    @Operation(description = "删除分类", summary = "删除分类")
    public Result<String> deleted(@RequestBody CategoriesIdCommand command) {
        return categoriesService.deleted(command);
    }



}
