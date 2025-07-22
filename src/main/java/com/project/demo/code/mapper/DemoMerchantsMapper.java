package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.project.demo.code.command.demo.MerchantsQueryCommand;
import com.project.demo.code.command.demo.MerchantsExecuteCommand;
import com.project.demo.code.domain.DemoMerchants;
import com.project.demo.code.dto.demo.MerchantsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 商家表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Mapper
public interface DemoMerchantsMapper extends BaseMapper<DemoMerchants> {


    /**
     * 分页查询商家列表
     *
     * @param page    分页对象
     * @param command 参数对象
     * @return 结果
     */
    IPage<DemoMerchants> page(@Param("page") Page<DemoMerchants> page, @Param("command") MerchantsQueryCommand command);


    /**
     * 更新商家信息
     *
     * @param command 商家信息
     * @return 结果
     */
    int updateMerchantsById(@Param("command") MerchantsExecuteCommand command);

    /**
     * 根据ID查询商家信息 (业务验证用，查询字段 ，商家ID 、 版本 、商家所有者、状态)
     *
     * @param merchantId 商家ID
     * @return 结果
     */
    DemoMerchants getMerchantsById(@Param("merchantId") Long merchantId);

    /**
     * 获取商家信息
     *
     * @param merchantId 商家ID
     * @return 结果
     */
    MerchantsDTO selectMerchantsById(@Param("merchantId") Long merchantId);
}
