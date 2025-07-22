package com.project.demo.code.service;

import com.project.demo.code.command.system.UpdateBaseInfoCommand;
import com.project.demo.code.command.system.UserCommand;
import com.project.demo.common.Result;
import com.project.demo.code.domain.DemoUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.project.demo.code.domain.common.MyUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author hylogan
 * @since 2024年06月06日 15:28:26
 */
@Service
public interface UserService extends IService<DemoUser> {

    /**
     * 登录接口
     *
     * @param user 用户输入
     * @return 用户信息
     */
    Result<Object> login(UserCommand user);

    /**
     * 根据用户名获取用户信息
     *
     * @param username 用户名称
     * @return 用户信息
     * @throws UsernameNotFoundException 用户找不到
     */
    MyUserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * 注册接口
     *
     * @param user 用户输入
     * @return 注册结果
     */
    Result<Object> register(UserCommand user, HttpServletRequest request);

    /**
     * 修改用户信息
     *
     * @param command 修改信息
     * @return 处理结果
     */
    Result<Object> updateBaseInfo(UpdateBaseInfoCommand command);
}
