package com.project.demo.code.controller.test;

import cn.hutool.core.collection.CollUtil;
import com.project.demo.common.Result;
import com.project.demo.common.util.WxUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/wxRobot")
@Slf4j
public class WxRobotController {

    @Resource
    WxUtil wxUtil;

    // 潜水统计
    @GetMapping("/dianJuStatistics")
    public Result<Object> dianJuStatistics(@RequestParam Integer wxRoomId, @RequestParam Integer days) {
        log.info("潜水统计入参：{},{}", wxRoomId, days);
        if (wxRoomId == null) {
            return Result.error();
        }
        List<String> dive = wxUtil.dive(wxRoomId, days);
        log.info("潜水统计结果：{}", CollUtil.join(dive, ","));
        return Result.success(dive);
    }

    // 活跃统计
    @GetMapping("/active")
    public Result<Object> active(@RequestParam Integer wxRoomId, @RequestParam Integer type) {
        log.info("活跃统计入参：{},{}", wxRoomId, type);
        if (wxRoomId == null) {
            return Result.error();
        }
        List<String> dive = wxUtil.active(wxRoomId, type);
        log.info("活跃统计结果：{}", CollUtil.join(dive, ","));
        return Result.success(dive);
    }

    @GetMapping("/charmAndCoin")
    public Result<Object> charmAndCoin(@RequestParam Integer wxRoomId, @RequestParam Integer ranking,@RequestParam Integer type) {
        log.info("金币魅力入参：{},{},{}", wxRoomId,ranking, type);
        if (wxRoomId == null) {
            return Result.error();
        }
        if (type == null){
            type = 1;
        }
        if (ranking == null){
            ranking = 10;
        }
        List<String> dive = wxUtil.corn(wxRoomId, ranking ,type);
        log.info("活跃统计结果：{}", CollUtil.join(dive, ","));
        return Result.success(dive);
    }

    @GetMapping("/activeCount")
    public Result<Object> charmAndCoin(@RequestParam String userWxId, @RequestParam Integer type) {
        List<Integer> list = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : WxUtil.wxRootMap.entrySet()) {
            System.out.println(entry.getKey() + ":" + entry.getValue());
            Integer count = wxUtil.activeCount(Integer.valueOf(entry.getKey()), type, userWxId);
            list.add(count);
            result.add(entry.getValue() + ": " + count);
        }

        /// 计算List的总和,过滤掉null
        int sum = list.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
        result.add("总发言：" + sum);
        // 最终计算金额
        if (type == 2) {
            result.add("最终金额：" + (sum / 8888) * 33.44 + "元");
        }
        return Result.success(result);
    }
}
