package com.project.demo.code.controller.demo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.command.demo.ProductsQueryCommand;
import com.project.demo.code.command.demo.ProductIdCommand;
import com.project.demo.code.domain.DemoProducts;
import com.project.demo.code.service.DemoProductsService;
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
 * 商品表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@RestController
@RequestMapping("/demo/product")
@Tag(name = "商品")
public class DemoProductsController {

    @Resource
    private DemoProductsService productsService;

    /**
     * 查询商品列表
     */
    @PostMapping("list")
    @Operation(description = "查询商品列表(分页)", summary = "查询商品列表(分页)")
    public Result<IPage<DemoProducts>> list (@RequestBody ProductsQueryCommand command) {
        return productsService.selectList(command);
    }

    /**
     * 查询商品详情
     */
    @PostMapping("/getProductById")
    @Operation(description = "查询商品详情", summary = "查询商品详情")
    public Result<DemoProducts> getProductById(@RequestBody ProductIdCommand command) {
        return productsService.getProductById(command);
    }

    /**
     * 新增商品
     */
    @PostMapping("/add")
    @Operation(description = "新增商品", summary = "新增商品")
    public Result<String> add(@RequestBody DemoProducts command) {
        return productsService.add(command);
    }

    /**
     * 编辑商品
     */
    @PostMapping("/edit")
    @Operation(description = "编辑商品", summary = "编辑商品")
    public Result<String> edit(@RequestBody DemoProducts command) {
        return productsService.edit(command);
    }


    /**
     * 删除商品
     */
    @PostMapping("/deleted")
    @Operation(description = "删除商品", summary = "删除商品")
    public Result<String> deleted(@RequestBody ProductIdCommand command) {
        return productsService.deleted(command);
    }

    /**
     * 上架
     */
    @PostMapping("/activate")
    @Operation(description = "上架商品", summary = "上架商品")
    public Result<String> activate(@RequestBody ProductIdCommand command) {
        return productsService.activate(command);
    }

    /**
     * 下架
     */
    @PostMapping("/deactivate")
    @Operation(description = "下架商品", summary = "下架商品")
    public Result<String> deactivate(@RequestBody ProductIdCommand command) {
        return productsService.deactivate(command);
    }

}
