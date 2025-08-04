package com.project.demo.code.tradingDetail.service;


import com.project.demo.code.tradingDetail.domain.TransactionDetails;

import java.util.List;


/**
 * Table for storing futures transaction detailsService接口
 *
 * @author AndyFan
 * @date 2025-07-23
 */
public interface ITransactionDetailsService {
    /**
     * 新增Table for storing futures transaction details
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return 结果
     */
    public int insertTransactionDetails(TransactionDetails transactionDetails);

    /**
     * 批量删除Table for storing futures transaction details
     *
     * @param ids 需要删除的Table for storing futures transaction details主键集合
     * @return 结果
     */
    public int deleteTransactionDetailsByIds(Long[] ids);

    /**
     * 删除Table for storing futures transaction details信息
     *
     * @param id Table for storing futures transaction details主键
     * @return 结果
     */
    public int deleteTransactionDetailsById(Long id);

    /**
     * 修改Table for storing futures transaction details
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return 结果
     */
    public int updateTransactionDetails(TransactionDetails transactionDetails);


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

    public List<String> selectMemberNames();


}
