package com.project.demo.common.secuity;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.project.demo.code.domain.DemoUser;
import com.project.demo.code.domain.common.MyUserDetails;
import com.project.demo.code.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserDetailServiceImpl implements UserDetailsService {
    @Resource
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<DemoUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DemoUser::getUsername, username);
        DemoUser user = userMapper.selectOne(wrapper);


        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            throw new RuntimeException("用户不存在");
        } else if (user.getDeleted() != 1) {
            log.info("登录用户：{} 已被删除.", username);
            throw new RuntimeException("用户不存在");
        }

        if (ObjectUtil.isEmpty(user)) {
            //用户名不存在
            throw new RuntimeException("用户信息不存在！");
        } else {
            //查找角色，实际应该查询权限，但我数据库没有设计所以就查角色就好了
            List<String> roles = new ArrayList<>();
            if (username.equals("admin")) {
                roles.add("ROLE_管理员");
            } else {
                roles.add("普通用户");
            }
            user.setRoles(roles);
            return new MyUserDetails(user);
        }
    }
}
