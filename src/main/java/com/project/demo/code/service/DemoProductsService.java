package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.demo.ProductsQueryCommand;
import com.project.demo.code.command.demo.ProductIdCommand;
import com.project.demo.code.domain.DemoProducts;
import com.project.demo.common.Result;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商品表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
public interface DemoProductsService extends IService<DemoProducts> {

    /**
     * 分页查询商品表
     * @param command 查询参数
     * @return 结果
     */
    Result<IPage<DemoProducts>> selectList(ProductsQueryCommand command);

    /**
     * 查询商品详情
     * @param command 商品ID
     * @return 结果
     */
    Result<DemoProducts> getProductById(ProductIdCommand command);

    /**
     * 新增商品
     * @param command 新增参数
     * @return 结果
     */
    Result<String> add(DemoProducts command);

    /**
     * 编辑商品
     * @param command 编辑参数
     * @return 结果
     */
    Result<String> edit(DemoProducts command);

    /**
     * 删除商品
     * @param command 商品ID
     * @return 结果
     */
    Result<String> deleted(ProductIdCommand command);

    /**
     * 上架商品
     * @param command 商品ID
     * @return 结果
     */
    Result<String> activate(ProductIdCommand command);

    /**
     * 下架商品
     * @param command 商品ID
     * @return 结果
     */
    Result<String> deactivate(ProductIdCommand command);

    /**
     * 根据商品ID集合获取商品信息
     * @param productIdList 商品ID集合
     * @return 商品map
     */
    Map<Long, DemoProducts> selectMapByIds(List<Long> productIdList);
}
