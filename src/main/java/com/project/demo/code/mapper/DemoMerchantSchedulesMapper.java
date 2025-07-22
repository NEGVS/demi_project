package com.project.demo.code.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.project.demo.code.domain.DemoMerchantSchedules;
import com.project.demo.code.dto.demo.MerchantSchedulesDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商家营业时间表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2025年01月03日 11:47:30
 */
@Mapper
public interface DemoMerchantSchedulesMapper extends BaseMapper<DemoMerchantSchedules> {

    /**
     * 获取商家营业时间配置
     *
     * @param merchantId 商家ID
     * @return 结果
     */
    List<MerchantSchedulesDTO> selectListByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 批量新增营业时间配置
     *
     * @param merchantSchedules 参数
     * @return 结果
     */
    int insertBatch(@Param("merchantSchedules") List<DemoMerchantSchedules> merchantSchedules);

    /**
     * 批量修改营业时间配置
     *
     * @param merchantSchedules 参数
     * @return 结果
     */
    int updateBatch(@Param("merchantSchedules") List<DemoMerchantSchedules> merchantSchedules);
}
