package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.demo.code.command.demo.ProductsQueryCommand;
import com.project.demo.code.command.demo.ProductIdCommand;
import com.project.demo.code.domain.DemoProducts;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Mapper
public interface DemoProductsMapper extends BaseMapper<DemoProducts> {


    /**
     * 分页查询商品表
     *
     * @param page    分页对象
     * @param command 参数对象
     * @return 结果
     */
    IPage<DemoProducts> page(@Param("page") Page<DemoProducts> page, @Param("command") ProductsQueryCommand command);

    /**
     * 查询商品详情
     *
     * @param command 商品ID
     * @return 结果
     */
    DemoProducts getProductById(@Param("command") ProductIdCommand command);

    /**
     * 删除商品
     *
     * @param command 商品ID
     * @return 结果
     */
    int remove(@Param("command") ProductIdCommand command);

    /**
     * 上架商品
     *
     * @param command 商品ID
     * @return 结果
     */
    int activate(@Param("command") ProductIdCommand command);

    /**
     * 下架商品
     *
     * @param command 商品ID
     * @return 结果
     */
    int deactivate(@Param("command") ProductIdCommand command);


    /**
     * 根据商品ID查询商品详情（购物车校验 主要 单价 - 库存）
     * @param productId 商品ID
     * @return 商品信息
     */
    DemoProducts selectProductById(Long productId);
}
