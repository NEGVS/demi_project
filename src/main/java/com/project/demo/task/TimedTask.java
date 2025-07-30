package com.project.demo.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.demo.code.domain.TradingData;
import com.project.demo.code.mapper.TradingDataMapper;
import com.project.demo.common.util.MyUtil;
import com.project.demo.common.utils.XuLiAlUtil;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * 定时任务
 */
@Slf4j
@Component
public class TimedTask {

    @Resource
    private AsyncTaskService asyncTaskService;
    @Resource
    private TradingDataMapper tradingDataMapper;
    @Resource
    private JavaMailSender mailSender;

    // 定义定时任务
    @Scheduled(cron = "0 0 16 * * ?")
    public void performTask() {
        System.out.println("执行定时任务: " + java.time.LocalDateTime.now());
        String nowDate = DateUtil.format(new Date(), "yyyyMMdd");
        List<String> dateList = MyUtil.calculateWorkdays(nowDate, nowDate);
        if (CollUtil.isEmpty(dateList)){
            log.info("今天没有数据，不同步");
            return;
        }
        String queryDate = DateUtil.format(new Date(), "yyyy-MM-dd");

        // 使用 LambdaQueryWrapper 构造删除条件
        LambdaQueryWrapper<TradingData> queryWrapper = new LambdaQueryWrapper<>();
        // 将传入的日期的数据全部删除，重新插入
        queryWrapper.eq(TradingData::getDate, queryDate);
        Long row = tradingDataMapper.selectCount(queryWrapper);
        if (row > 0){
            log.info("今天的数据已同步，定时任务不执行");
        }else{
            asyncTaskService.executeAsyncTaskV2(dateList,"565109070@qq.com");
            try {
                String join = CollUtil.join(dateList, ",");
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("hylogan@qq.com");
                message.setTo("1933525074@qq.com");
                message.setSubject("同步任务");
                message.setText(join + "任务同步完成");
                mailSender.send(message);
            }catch (Exception e){
                log.info("定时任务发送邮件me失败");
            }

        }
    }

//    @Scheduled(cron = "0 1 0 * * ? ")
    public void xuLiAL() throws JsonProcessingException {
        log.info("蓄力AL签到、分享");
        XuLiAlUtil.sign(null);
        XuLiAlUtil.click(null);
        XuLiAlUtil.click(null);
    }

//    @Scheduled(cron = "59 59 23 * * ? ")
    public void newDay() throws MessagingException {
        // 创建一个复杂邮件对象
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        // 发送方
        helper.setFrom("hylogan@qq.com");
        // 接收方
        helper.setTo("2550799384@qq.com");
        // 邮件主题
        helper.setSubject("元旦快乐");

        // 邮件内容，支持 HTML 富文本
        String content = """
            <html>
                <body>
                    <h2 style="color: #409EFF;">元旦快乐！🎉</h2>
                    <p>愿新的一年充满希望与欢乐，所有的梦想都能实现！</p>
                    <p>愿你每天都能笑口常开，<b>健康平安</b>，<i>幸福相伴</i>！</p>
                    <p style="font-size: 14px; color: #888;">新的一年，加油哦！</p>
                    <img src="cid:happyNewYearImage" style="width: 300px; height: auto;" />
                </body>
            </html>
        """;
        helper.setText(content, true);

        // 添加内嵌图片（cid）
        helper.addInline("happyNewYearImage", new File("src/main/resources/static/happy_new_year.png"));

        // 发送邮件
        mailSender.send(mimeMessage);
    }

}
