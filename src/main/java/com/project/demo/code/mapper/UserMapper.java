package com.project.demo.code.mapper;

import com.project.demo.code.domain.DemoUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author hylogan
 * @since 2024年06月06日 15:28:26
 */
@Mapper
public interface UserMapper extends BaseMapper<DemoUser> {

}
