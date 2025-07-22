package com.project.demo.code.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.demo.code.command.system.UpdateBaseInfoCommand;
import com.project.demo.code.command.system.UserCommand;
import com.project.demo.code.domain.DemoUser;
import com.project.demo.code.domain.common.MyUserDetails;
import com.project.demo.code.mapper.UserMapper;
import com.project.demo.code.service.UserService;
import com.project.demo.common.Result;
import com.project.demo.common.jwt.TokenUtil;
import com.project.demo.common.util.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author hylogan
 * @since 2024年06月06日 15:28:26
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, DemoUser> implements UserService {

    @Resource
    private UserMapper userMapper;
    @Resource
    private TokenUtil tokenUtil;
    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * 1、校验参数是否正确
     * 2、验证用户是否存在
     * 3、验证密码是否争取
     * 4、生成token，将用户信息存入redis中
     * 5、返回token
     * @desc 暂时使用 无过期时间的token，依赖redis中的用户信息来判断用户登录是否失效
     * @param userCommand 用户输入
     * @return token
     */
    @Override
    public Result<Object> login(UserCommand userCommand) {
        long start = System.currentTimeMillis();
        // 1、参数校验
        String message = validateParam(userCommand);

        if (StrUtil.isNotBlank(message)) return Result.error(message);
        // 2、验证用户是否存在
        DemoUser user = findUserByUserName(userCommand.getUsername());
        if (user == null){
            return Result.error("用户不存在");
        }
        // 3、验证用户密码是否正确
        boolean compare = passwordEncoder.matches(userCommand.getPassword(), user.getPassword());
        if (!compare){
            return Result.error("用户名或密码错误");
        }
        // 4、生成token
        MyUserDetails userDetails = new MyUserDetails(user,user.getId(),getPermission(user.getId()));
        // 生成token(无过期时间的)
        String token = tokenUtil.createToken(userDetails);
        long end = System.currentTimeMillis();
        log.info("登录耗时：{} 毫秒", (end - start));
        return Result.success(token);
    }

    @Override
    public MyUserDetails loadUserByUsername(String username) {
        DemoUser user = userMapper.selectOne(new LambdaQueryWrapper<DemoUser>().eq(DemoUser::getUsername, username));
        if (ObjectUtil.isNotEmpty(user)) {
            return new MyUserDetails(user, user.getId(), null);
        }
        return null;
    }

    @Override
    public Result<Object> register(UserCommand userCommand, HttpServletRequest request) {
        log.info("注册用户serviceImpl入参：{}", JSONUtil.toJsonStr(userCommand));
        String userName = userCommand.getUsername();
        String password = userCommand.getPassword();
        if (ObjectUtil.isEmpty(userName) || ObjectUtil.isEmpty(password)) {
            return Result.error("用户名或密码不能为空");
        }
        DemoUser user = findUserByUserName(userName);
        if (ObjectUtil.isNotEmpty(user)) {
            return Result.error("用户名已被注册");
        }
        userCommand.setPassword(passwordEncoder.encode(password));
        DemoUser demoUser = new DemoUser();
        BeanUtils.copyProperties(userCommand, demoUser);
        String ip = request.getRemoteAddr();
        demoUser.setRegistrationIp(ip);
        int row = userMapper.insert(demoUser);
        if (row > 0) {
            return Result.success().setMessage("注册成功");
        } else {
            return Result.error("注册失败");
        }
    }

    /**
     * 修改用户信息
     *
     * @param command 修改信息
     * @return 处理结果
     */
    @Override
    public Result<Object> updateBaseInfo(UpdateBaseInfoCommand command) {
        log.info("修改用户信息入参：{}", JSONUtil.toJsonStr(command));
        if (SecurityUtils.isAuthenticated()) {
            MyUserDetails userDetails = SecurityUtils.getCurrentUserDetails();
            log.info("SecurityContextHolder中的用户信息：{}", JSONUtil.toJsonStr(userDetails));
            if (ObjectUtil.isNotEmpty(userDetails)) {
                assert userDetails != null;
                DemoUser user = userMapper.selectById(userDetails.getId());
                if (ObjectUtil.isNotEmpty(user) && Objects.equals(command.getId(), user.getId())) {
                    if (ObjectUtil.isNotEmpty(command.getNickName())) {
                        user.setNickName(command.getNickName());
                    }
                    if (ObjectUtil.isNotEmpty(command.getAvatar())) {
                        user.setAvatar(command.getAvatar());
                    }
                    if (ObjectUtil.isNotEmpty(command.getGender())) {
                        user.setGender(command.getGender());
                    }
                    if (ObjectUtil.isNotEmpty(command.getAvatarThumbnail())) {
                        user.setAvatarThumbnail(command.getAvatarThumbnail());
                    }
                    user.setUpdateBy("me");
                    int row = userMapper.updateById(user);
                    if (row > 0) {
                        return Result.success().setMessage("修改成功");
                    }
                } else {
                    return Result.error("用户信息错误");
                }
            }
        }
        return Result.error("系统繁忙");
    }


    /**
     * 校验参数
     *
     * @param user 参数信息
     * @return 结果
     */
    private String validateParam(UserCommand user) {
        if (ObjectUtil.isEmpty(user)) {
            return "参数为空";
        }
        String userName = user.getUsername();
        String password = user.getPassword();
        if (ObjectUtil.isEmpty(userName) || ObjectUtil.isEmpty(password)) {
            return "用户名或密码不能为空";
        }
        return null;
    }


    /**
     * 根据用户名查询用户是否存在
     * @param username 用户名
     * @return 结果
     */
    private DemoUser findUserByUserName(String username) {
        return userMapper.selectOne(new LambdaQueryWrapper<DemoUser>().eq(DemoUser::getUsername, username));
    }

    /**
     * 根据用户id查询用户权限
     * TODO 暂时全部权限
     * @param id 用户ID
     * @return 权限
     */
    private Set<String> getPermission(Long id) {
        if (id != null) {
            Set<String> perms = new HashSet<String>();
            perms.add("*:*:*");
            return perms;
        }
        return null;
    }
}
