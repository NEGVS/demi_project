package com.project.demo.code.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.demo.ProductIdCommand;
import com.project.demo.code.command.demo.ProductsQueryCommand;
import com.project.demo.code.domain.DemoCategories;
import com.project.demo.code.domain.DemoMerchants;
import com.project.demo.code.domain.DemoProducts;
import com.project.demo.code.mapper.DemoCategoriesMapper;
import com.project.demo.code.mapper.DemoMerchantsMapper;
import com.project.demo.code.mapper.DemoProductsMapper;
import com.project.demo.code.service.DemoProductsService;
import com.project.demo.common.Result;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Service
@Slf4j
public class DemoProductsServiceImpl extends ServiceImpl<DemoProductsMapper, DemoProducts> implements DemoProductsService {


    @Resource
    private DemoProductsMapper productsMapper;

    @Resource
    private DemoMerchantsMapper merchantsMapper;

    @Resource
    private DemoCategoriesMapper categoriesMapper;

    /**
     * 查询商品列表
     *
     * @param command 查询参数
     * @return 商品列表
     */
    @Override
    public Result<IPage<DemoProducts>> selectList(ProductsQueryCommand command) {
        // 创建分页对象，参数分别是：当前页数(page)、每页显示条数(size)
        Page<DemoProducts> page = new Page<>(command.getPageNum(), command.getPageSize());
        // 执行分页查询
        return Result.success(productsMapper.page(page, command));
    }

    @Override
    public Result<DemoProducts> getProductById(ProductIdCommand command) {
        String errorMsg = validateParamProductId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        return Result.success(productsMapper.getProductById(command));
    }

    /**
     * 新增商品
     *
     * @param command 新增参数
     * @return 结果
     */
    @Override
    public Result<String> add(DemoProducts command) {
        // 1、验证参数
        String errorMsg = validateParameters(command, true);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 2、执行新增
        String loginName = SecurityUtils.getCurrentUsername();
        if (StrUtil.isBlank(loginName)) {
            return Result.error(401, "请先登录");
        }
        command.setCreateBy(loginName);
        command.setUpdateBy(loginName);
        if (productsMapper.insert(command) > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 修改商品
     *
     * @param command 新增参数
     * @return 结果
     */
    @Override
    public Result<String> edit(DemoProducts command) {
        String errorMsg = validateParameters(command, false);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        // 查询当前商品信息
        DemoProducts product = productsMapper.selectById(command.getProductId());
        if (product == null){
            return Result.error("商品不存在");
        }
        // 2、执行新增
        String loginName = SecurityUtils.getCurrentUsername();
        if (StrUtil.isBlank(loginName)) {
            return Result.error(401, "请先登录");
        }
        command.setVersion(command.getVersion());
        command.setUpdateBy(loginName);
        if (productsMapper.updateById(command) > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 删除商品
     * @param command 商品ID
     * @return 结果
     */
    @Override
    public Result<String> deleted(ProductIdCommand command) {
        String errorMsg = validateParamProductId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        if (productsMapper.remove(command) > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 上架商品
     * @param command 商品ID
     * @return 结果
     */
    @Override
    public Result<String> activate(ProductIdCommand command) {
        String errorMsg = validateParamProductId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        if (productsMapper.activate(command) > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 下架商品
     * @param command 商品ID
     * @return 结果
     */
    @Override
    public Result<String> deactivate(ProductIdCommand command) {
        String errorMsg = validateParamProductId(command);
        if (StrUtil.isNotBlank(errorMsg)) {
            return Result.error(errorMsg);
        }
        if (productsMapper.deactivate(command) > 0) {
            return Result.success();
        }
        return Result.error();
    }

    /**
     * 根据商品ID集合查询商品信息
     *
     * @param productIdList 商品ID集合
     * @return 商品Map （字段  仅查询商品ID、价格、折扣价、库存、状态）
     */
    @Override
    public Map<Long, DemoProducts> selectMapByIds(List<Long> productIdList) {
        if (CollUtil.isNotEmpty(productIdList)) {
            LambdaQueryWrapper<DemoProducts> queryWrapper = new LambdaQueryWrapper<>();
            // 仅查询商品ID、价格、折扣价、库存、状态
            queryWrapper.select(DemoProducts::getProductId, DemoProducts::getPrice, DemoProducts::getDiscountPrice, DemoProducts::getStock, DemoProducts::getStatus);
            queryWrapper.eq(DemoProducts::getDeleted, 1);
            queryWrapper.eq(DemoProducts::getStatus, 1);
            queryWrapper.in(DemoProducts::getProductId, productIdList);
            List<DemoProducts> productList = productsMapper.selectList(queryWrapper);
            if (CollUtil.isNotEmpty(productList)) {
                return productList.stream().collect(Collectors.toMap(DemoProducts::getProductId, product -> product));
            }
        }
        return null;
    }

    /**
     * 验证参数
     *
     * @param command 参数
     * @param flag    是否新增 新增 or 编辑   true 新增 false 编辑
     * @return 结果
     */
    private String validateParameters(DemoProducts command, Boolean flag) {
        if (command == null) {
            return "参数为空";
        }
        if (command.getMerchantId() == null || command.getMerchantId() == 0) {
            return "商家ID不能为空";
        }
        if (command.getCategoryId() == null || command.getCategoryId() == 0) {
            return "商品分类ID不能为空";
        }
        if (StrUtil.isBlank(command.getProductName())) {
            return "商品名称不能为空";
        }
        if (command.getPrice() == null || command.getPrice().compareTo(BigDecimal.ZERO) == 0) {
            return "商品单价不能为空";
        }
        if (command.getStock() == null ) {
            return "商品库存不能为空";
        }
        return verificationProductsBusiness(command, flag);
    }

    /**
     * 商品业务验证
     *
     * @param command 参数
     * @param flag    是否新增 新增 or 编辑   true 新增 false 编辑
     * @return 结果
     */
    private String verificationProductsBusiness(DemoProducts command, Boolean flag) {
        // 1、商品分类、商家是否存在
        if (merchantsMapper.selectCount(new LambdaQueryWrapper<DemoMerchants>().eq(DemoMerchants::getMerchantId, command.getMerchantId())) == 0) {
            return "商家不存在";
        }
        if (categoriesMapper.selectCount(new LambdaQueryWrapper<DemoCategories>().eq(DemoCategories::getCategoryId, command.getCategoryId())) == 0) {
            return "商品分类不存在";
        }
        if (!flag && command.getProductId() == null) {
            return "商品ID不能为空";
        }
        // 2、商品名称是否重复（同一个商家中）
        LambdaQueryWrapper<DemoProducts> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DemoProducts::getProductName, command.getProductName())
                .eq(DemoProducts::getMerchantId, command.getMerchantId());
        queryWrapper.ne(command.getProductId() != null, DemoProducts::getProductId, command.getProductId());
        if (productsMapper.selectCount(queryWrapper) > 0) {
            return "商品名称重复";
        }
        return null;
    }

    /**
     * 验证参数 ( 针对商品ID )
     *
     * @param command 入参
     * @return 结果
     */
    private String validateParamProductId(ProductIdCommand command) {
        if (command == null || command.getProductId() == null) {
            return "商品ID为空";
        }
        return null;
    }
}
