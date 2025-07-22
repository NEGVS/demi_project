package com.project.demo.code.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.command.demo.MerchantsQueryCommand;
import com.project.demo.code.command.demo.MerchantsExecuteCommand;
import com.project.demo.code.command.demo.MerchantsIdCommand;
import com.project.demo.code.domain.DemoMerchants;
import com.project.demo.code.dto.demo.MerchantsDTO;
import com.project.demo.common.Result;

/**
 * <p>
 * 商家表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
public interface DemoMerchantsService extends IService<DemoMerchants> {

    /**
     * 查询商家表列表
     * @param command 查询条件
     * @return 结果
     */
    Result<IPage<DemoMerchants>> selectList(MerchantsQueryCommand command);

    /**
     * 新增商家表
     * @param command 商家
     * @return 结果
     */
    Result<String> add(MerchantsExecuteCommand command);

    /**
     * 修改商家表
     *
     * @param command 商家信息
     * @return 结果
     */
    Result<String> edit(MerchantsExecuteCommand command);

    /**
     * 删除商家
     * @param command 商家信息
     * @return 结果
     */
    Result<String> delete(MerchantsIdCommand command);

    /**
     * 获取商家详情
     * @param merchantId 商家ID
     * @return 结果
     */
    MerchantsDTO getMerchantsId(Long merchantId);
}
