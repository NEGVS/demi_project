package com.project.demo.tradingDetail.service.impl;

import com.project.demo.tradingDetail.domain.TransactionDetails;
import com.project.demo.tradingDetail.mapper.TransactionDetailsMapper;
import com.project.demo.tradingDetail.service.ITransactionDetailsService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Table for storing futures transaction detailsService业务层处理
 *
 * @author AndyFan
 * @date 2025-07-23
 */
@Service
public class TransactionDetailsServiceImpl implements ITransactionDetailsService {

    @Resource
    private TransactionDetailsMapper transactionDetailsMapper;

    /**
     * 查询Table for storing futures transaction details
     *
     * @param id Table for storing futures transaction details主键
     * @return Table for storing futures transaction details
     */
    @Override
    public TransactionDetails selectTransactionDetailsById(Long id) {
        return transactionDetailsMapper.selectTransactionDetailsById(id);
    }

    /**
     * 查询Table for storing futures transaction details列表
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return Table for storing futures transaction details
     */
    @Override
    public List<TransactionDetails> selectTransactionDetailsList(TransactionDetails transactionDetails) {
        return transactionDetailsMapper.selectTransactionDetailsList(transactionDetails);
    }

    @Override
    public List<String> selectMemberNames() {
        //提取 memberName
        return transactionDetailsMapper.selectMemberNames().stream().map(TransactionDetails::getMemberName).map(String::trim).filter(memberName -> !memberName.isEmpty()).collect(Collectors.toList());
    }

    /**
     * 获取连续3天以上买入的数据
     *
     * @param transactionDetails transactionDetails
     * @return List<TransactionDetails>
     */
    public List<TransactionDetails> selectTransactionDetaislsList(TransactionDetails transactionDetails) {

        return transactionDetailsMapper.selectTransactionDetailsList(transactionDetails);
    }

    /**
     * 新增Table for storing futures transaction details
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return 结果
     */
    @Override
    public int insertTransactionDetails(TransactionDetails transactionDetails) {
        return transactionDetailsMapper.insertTransactionDetails(transactionDetails);
    }

    /**
     * 修改Table for storing futures transaction details
     *
     * @param transactionDetails Table for storing futures transaction details
     * @return 结果
     */
    @Override
    public int updateTransactionDetails(TransactionDetails transactionDetails) {
        return transactionDetailsMapper.updateTransactionDetails(transactionDetails);
    }

    /**
     * 批量删除Table for storing futures transaction details
     *
     * @param ids 需要删除的Table for storing futures transaction details主键
     * @return 结果
     */
    @Override
    public int deleteTransactionDetailsByIds(Long[] ids) {
        return transactionDetailsMapper.deleteTransactionDetailsByIds(ids);
    }

    /**
     * 删除Table for storing futures transaction details信息
     *
     * @param id Table for storing futures transaction details主键
     * @return 结果
     */
    @Override
    public int deleteTransactionDetailsById(Long id) {
        return transactionDetailsMapper.deleteTransactionDetailsById(id);
    }
}
