package com.project.demo.code.controller.demo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.command.demo.MerchantsQueryCommand;
import com.project.demo.code.command.demo.MerchantsExecuteCommand;
import com.project.demo.code.command.demo.MerchantsIdCommand;
import com.project.demo.code.domain.DemoMerchants;
import com.project.demo.code.dto.demo.MerchantsDTO;
import com.project.demo.code.service.DemoMerchantsService;
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
 * 商家表 前端控制器
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@RestController
@RequestMapping("/demo/merchant")
@Tag(name = "商家")
public class DemoMerchantsController {

    @Resource
    private DemoMerchantsService merchantsService;

    /**
     * 查询商家（分页）
     */
    @PostMapping("list")
    @Operation(description = "查询商品列表(分页)", summary = "查询商品列表(分页)")
    public Result<IPage<DemoMerchants>> list(@RequestBody MerchantsQueryCommand command) {
        return merchantsService.selectList(command);
    }

    /**
     * 新增商家
     */
    @PostMapping("add")
    @Operation(description = "新增商家", summary = "新增商家")
    public Result<String> add(@RequestBody MerchantsExecuteCommand command) {
        return merchantsService.add(command);
    }

    /**
     * 编辑商家
     */
    @PostMapping("edit")
    @Operation(description = "编辑商家", summary = "编辑商家")
    public Result<String> edit(@RequestBody MerchantsExecuteCommand command) {
        return merchantsService.edit(command);
    }


    /**
     * 删除商家
     */
    @PostMapping("delete")
    @Operation(description = "删除商家", summary = "删除商家")
    public Result<String> delete(@RequestBody MerchantsIdCommand command) {
        return merchantsService.delete(command);
    }

    /**
     * 根据主键ID获取商家详情
     */
    @PostMapping("getMerchantsId")
    @Operation(description = "获取商家详情", summary = "获取商家详情")
    public Result<MerchantsDTO> getMerchantsId(@RequestBody MerchantsIdCommand command) {
        if (command == null){
            return Result.error("参数错误");
        }
        return Result.success(merchantsService.getMerchantsId(command.getMerchantId()));
    }

}
