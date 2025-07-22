package com.project.demo.code.mapper;

import com.project.demo.code.domain.DemoCartMetadata;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 购物车元信息表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Mapper
public interface DemoCartMetadataMapper extends BaseMapper<DemoCartMetadata> {

    /**
     * 根据用户id + 商家id查询购物车元信息
     * @param userId 用户ID
     * @param merchantId 商家ID
     * @return 结果
     */
    DemoCartMetadata selectByUserIdAndMerchantId(@Param("userId") Long userId,@Param("merchantId") Long merchantId);
}
