package com.project.demo.code.service.impl;

import com.project.demo.code.domain.DemoLoveTalk;
import com.project.demo.code.mapper.DemoLoveTalkMapper;
import com.project.demo.code.service.DemoLoveTalkService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.common.Result;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * <p>
 * 土味情话表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2024年12月17日 11:57:31
 */
@Service
public class DemoLoveTalkServiceImpl extends ServiceImpl<DemoLoveTalkMapper, DemoLoveTalk> implements DemoLoveTalkService {

    /**
     * 随机土味情话
     * @return 土味情话
     */
    @Override
    public Result<String> randomLoveTalk() {
        long count = count();
        Random random = new Random();
        long l = random.nextLong(count);
        DemoLoveTalk demoLoveTalk = getById(l + 1);
        if (demoLoveTalk == null){
            return Result.success("宝贝，我现在忙得像只忙碌的小蜜蜂，稍后再飞到你身边好吗？");
        }
        return Result.success(demoLoveTalk.getContent());
    }
}
