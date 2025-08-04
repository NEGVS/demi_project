package com.project.demo.code.mapper.mapper;

import com.project.demo.code.tradingDetail.domain.TransactionDetails;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * Table for storing futures transaction detailsMapper接口
 *
 * @author AndyFan
 * @date 2025-07-23
 */
@Mapper
public interface TransactionDetailsMapper {
    /**
     * 查询Table for storing futures transaction details
     *
     * @param id Table for storing futures transaction details主键
     * @return Table for storing futures transaction details
     */
    public TransactionDetails selectTransactionDetailsById(Long id);

    /**
     * 查询Table for storing futures transaction details列表
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return Table for storing futures transaction details集合
     */
    public List<TransactionDetails> selectTransactionDetailsList(TransactionDetails transactionDetails);

    /**
     * select distinct memberName
     *
     * @return List<TransactionDetails>
     */
    public List<TransactionDetails> selectMemberNames();

    /**
     * 新增Table for storing futures transaction details
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return 结果
     */
    public int insertTransactionDetails(TransactionDetails transactionDetails);

    /**
     * 插入交易明细信息
     *
     * @param list 输入参数
     * @return int 返回插入交易明细信息结果
     */
    int insertTransactionDetailsList(List<TransactionDetails> list);

    /**
     * 修改Table for storing futures transaction details
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return 结果
     */
    public int updateTransactionDetails(TransactionDetails transactionDetails);

    /**
     * 删除Table for storing futures transaction details
     *
     * @param id Table for storing futures transaction details主键
     * @return 结果
     */
    public int deleteTransactionDetailsById(Long id);

    /**
     * 批量删除Table for storing futures transaction details
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTransactionDetailsByIds(Long[] ids);
}
