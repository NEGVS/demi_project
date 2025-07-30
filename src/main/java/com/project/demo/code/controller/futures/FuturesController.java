package com.project.demo.code.controller.futures;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.project.demo.code.command.system.futures.FuturesQueryCommand;
import com.project.demo.code.domain.TradingData;
import com.project.demo.code.service.TradingDataService;
import com.project.demo.common.JSONAuthentication;
import com.project.demo.common.Result;
import com.project.demo.common.util.MyUtil;
import com.project.demo.task.AsyncTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "抓取数据")
@RestController
@Slf4j
@RequestMapping("/futures")
public class FuturesController {

    @Resource
    private TradingDataService tradingDataService;
    @Resource
    private AsyncTaskService asyncTaskService;

    /**
     * 查询爬取的期货数据
     */
    @PostMapping("/list")
    @Operation(summary = "查询爬取的期货数据")
    public Result<IPage<TradingData>> list(@RequestBody FuturesQueryCommand command) {
        return tradingDataService.selectList(command);
    }

    @GetMapping("/graspingDataV2")
    @Operation(summary = "同步期货数据")
    public Result<String> graspingDataV2(@RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate", required = false) String endDate, String mail) {
        log.info("同步期货数据入参：{},{},{}", startDate, endDate, mail);
        if (StrUtil.isBlank(startDate)) {
            return Result.error("（startDate）开始时间不能为空");
        }
        if (StrUtil.isBlank(endDate)) {
            endDate = DateUtil.format(new Date(), "yyyyMMdd");
        }
        List<String> list = MyUtil.calculateWorkdays(startDate, endDate);

        if (CollUtil.isEmpty(list)) {
            return Result.error("没有合适的数据：这几天都是休息日！");
        } else {
            log.info("开始同步数据：{}", list);
        }
//        .subList(0, list.size() - 1)
        asyncTaskService.executeAsyncTaskV2(list, mail);
        return Result.success("异步任务启动成功,请留意邮箱结果");
    }

    @GetMapping("/get")
    @Operation(summary = "查看错误数据")
    public Result<List<String>> getErrorList() {
        return Result.success(JSONAuthentication.errorList);
    }

}
