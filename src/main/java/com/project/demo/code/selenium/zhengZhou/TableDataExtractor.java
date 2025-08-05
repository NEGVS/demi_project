package com.project.demo.code.selenium.zhengZhou;

/**
 * @Description
 * @Author Andy Fan
 * @Date 2025/8/4 09:56
 * @ClassName TableDataExtractor
 */

import com.project.demo.code.mapper.mapper.TransactionDetailsMapper;
import com.project.demo.code.tradingDetail.domain.TransactionDetails;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TableDataExtractor {
    @Resource
    TransactionDetailsMapper transactionDetailsMapper;

    // 数据结构定义
    static class FuturesData {
        String rank;
        String tradingMember;
        String tradingVolume;
        String tradingChange;
        String longPositionMember;
        String longPositionVolume;
        String longPositionChange;
        String shortPositionMember;
        String shortPositionVolume;
        String shortPositionChange;

        public FuturesData(String rank, String tradingMember, String tradingVolume, String tradingChange,
                           String longPositionMember, String longPositionVolume, String longPositionChange,
                           String shortPositionMember, String shortPositionVolume, String shortPositionChange) {
            this.rank = rank;
            this.tradingMember = tradingMember;
            this.tradingVolume = tradingVolume;
            this.tradingChange = tradingChange;
            this.longPositionMember = longPositionMember;
            this.longPositionVolume = longPositionVolume;
            this.longPositionChange = longPositionChange;
            this.shortPositionMember = shortPositionMember;
            this.shortPositionVolume = shortPositionVolume;
            this.shortPositionChange = shortPositionChange;
        }

        @Override
        public String toString() {
            return String.format("Rank: %s, Trading: %s (%s, %s), Long: %s (%s, %s), Short: %s (%s, %s)",
                    rank, tradingMember, tradingVolume, tradingChange,
                    longPositionMember, longPositionVolume, longPositionChange,
                    shortPositionMember, shortPositionVolume, shortPositionChange);
        }
    }

    static class CommodityData {
        String title;
        String name;
        String date;
        List<String> headers;
        List<FuturesData> rows;

        public CommodityData(String title, String name, String date) {
            this.title = title;
            this.name = name;
            this.date = date;
            this.headers = new ArrayList<>();
            this.rows = new ArrayList<>();
        }

        public void addHeader(List<String> headers) {
            this.headers.addAll(headers);
        }

        public void addRow(FuturesData data) {
            rows.add(data);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("标题: ").append(title).append("\n");
            sb.append("品种: ").append(name).append(", 日期: ").append(date).append("\n");
            sb.append("表头: ").append(headers).append("\n");
            for (FuturesData row : rows) {
                sb.append(row).append("\n");
            }
            return sb.toString();
        }
    }

    public static List<CommodityData> extractAllTableData(String filePath) {
        List<CommodityData> allCommodities = new ArrayList<>();
        try {
            // 解析 HTML 文件
            File file = new File(filePath);
            Document doc = Jsoup.parse(file, "UTF-8");
            CommodityData currentCommodity = null;

            // 提取 <title>
            String title = doc.select("title").text();

            // 获取所有 <tr> 元素
            Elements rows = doc.select("table tr");

            for (Element row : rows) {
                Elements cols = row.select("td");

                // 处理标题行（品种或合约和日期）
                if (cols.size() == 1 && cols.first().hasAttr("colspan")) {
                    String headerText = cols.first().text();
                    if (headerText.contains("品种：") || headerText.contains("合约：")) {
                        String[] parts = headerText.split("[:：]");
                        if (parts.length >= 3) {
                            String name = parts[1].split("日期")[0].trim();
                            String date = parts[2].trim();
                            currentCommodity = new CommodityData(title, name, date);
                            allCommodities.add(currentCommodity);
                        }
                    }
                    continue;
                }

                // 处理表头行
                if (cols.size() == 10 && cols.get(0).text().equals("名次") && currentCommodity != null) {
                    List<String> headers = new ArrayList<>();
                    for (Element col : cols) {
                        headers.add(col.text());
                    }
                    currentCommodity.addHeader(headers);
                    continue;
                }

                // 处理数据行
                if (cols.size() == 10 && !cols.get(0).text().equals("名次") && currentCommodity != null) {
                    FuturesData data = new FuturesData(
                            cols.get(0).text(), // 名次
                            cols.get(1).text(), // 交易会员
                            cols.get(2).text(), // 成交量
                            cols.get(3).text(), // 交易增减
                            cols.get(4).text(), // 持买会员
                            cols.get(5).text(), // 持买量
                            cols.get(6).text(), // 持买增减
                            cols.get(7).text(), // 持卖会员
                            cols.get(8).text(), // 持卖量
                            cols.get(9).text()  // 持卖增减
                    );
                    currentCommodity.addRow(data);
                }
            }

            return allCommodities;

        } catch (Exception e) {
            log.error("解析文件失败: {}", e.getMessage());
            return null;
        }
    }

    public void addCommodity(String filePath) {
        if (filePath == null) {
            filePath = "/Users/andy_mac/Documents/CodeSpace/andyProject0/demi_project/src/main/java/com/project/demo/selenium/file/aaa.txt";
        }
        List<TransactionDetails> transactionDetails = new ArrayList<>();
        // 提取所有品种数据
        List<CommodityData> allCommodities = extractAllTableData(filePath);
        // 输出结果
        if (allCommodities != null && !allCommodities.isEmpty()) {
            for (CommodityData commodity : allCommodities) {
                for (FuturesData row : commodity.rows) {
                    // Transaction Type 1
                    transactionDetails.add(createSingleTransactionDetail(
                            row.rank,
                            row.tradingMember,
                            commodity.name,
                            1L,
                            row.tradingVolume,
                            row.tradingChange,
                            commodity.date,
                            "郑州证券交易所",
                            commodity.name
                    ));
                    // Transaction Type 2
                    transactionDetails.add(createSingleTransactionDetail(
                            row.rank,
                            row.longPositionMember,
                            commodity.name,
                            2L,
                            row.longPositionVolume,
                            row.longPositionChange,
                            commodity.date,
                            "郑州证券交易所",
                            commodity.name
                    ));
                    // Transaction Type 3
                    transactionDetails.add(createSingleTransactionDetail(
                            row.rank,
                            row.shortPositionMember,
                            commodity.name,
                            3L,
                            row.shortPositionVolume,
                            row.shortPositionChange,
                            commodity.date,
                            "郑州证券交易所",
                            commodity.name
                    ));
                }
            }
            int size = transactionDetails.size();
            try {
                System.out.println("读取数据 size:" + size);
                int i = transactionDetailsMapper.insertTransactionDetailsList(transactionDetails);
                if (i > 0) {
                    log.info("insert into transaction_details success, size:{}", transactionDetails.size());
                } else {
                    log.error("insert into transaction_details failed, size:{}", transactionDetails.size());
                }
            } catch (Exception e) {
                log.error("insert into transaction_details failed, size:{}", transactionDetails.size());
                e.printStackTrace();
            }


        } else {
            log.error("无法解析表格数据");
        }
    }


    /**
     * 创建单个交易明细
     */
    public static TransactionDetails createSingleTransactionDetail(
            String rank,
            String memberName,
            String contractCode,
            Long transactionType,
            String totalVolume,
            String volumeChange,
            String reportDate,
            String exchangeName,
            String futuresVariety
    ) {
        //合约代码
//                transactionDetails3.setContractCode(shfeProductData.getINSTRUMENTID());
//                //交易类型：成交、持买单、持卖单
//                transactionDetails3.setTransactionType(3L);
//                //交易总量
//                transactionDetails3.setTotalVolume(Long.valueOf(shfeProductData.getCJ3().isEmpty() ? "0" : shfeProductData.getCJ3()));
//                //增减量
//                transactionDetails3.setVolumeChange(Long.valueOf(shfeProductData.getCJ3_CHG().isEmpty() ? "0" : shfeProductData.getCJ3_CHG()));
//                //日期
//                transactionDetails3.setTransactionDate(shfeData.getReport_date());
//                //期货品种
//                transactionDetails3.setFuturesVariety(shfeProductData.getPRODUCTNAME());
//                //交易所名称
//                transactionDetails3.setExchangeName("上海期货交易所");
        TransactionDetails details = new TransactionDetails();
        details.setRanking(rank);
        details.setMemberName(memberName);
        details.setContractCode(contractCode);
        details.setTransactionType(transactionType);
        totalVolume = totalVolume.replace(",", "").replace("-", "");
        details.setTotalVolume(Long.parseLong(totalVolume.isEmpty() ? "0" : totalVolume));
        volumeChange = volumeChange.replace(",", "").replace("-", "");
        details.setVolumeChange(Long.parseLong(volumeChange.isEmpty() ? "0" : volumeChange));
        details.setTransactionDate(reportDate);
        details.setFuturesVariety(futuresVariety);
        details.setExchangeName(exchangeName);
        details.setCreatedTime(new Date());
        return details;
    }
}