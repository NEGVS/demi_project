package com.project.demo.code.service;

import com.project.demo.code.domain.DemoLoveTalk;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.common.Result;

/**
 * <p>
 * 土味情话表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2024年12月17日 11:57:31
 */
public interface DemoLoveTalkService extends IService<DemoLoveTalk> {

    /**
     * 随机土味情话
     * @return 随机土味情话
     */
    Result<String> randomLoveTalk();
}
