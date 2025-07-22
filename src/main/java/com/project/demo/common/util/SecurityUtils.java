package com.project.demo.common.util;

import com.project.demo.code.domain.common.MyUserDetails;
import com.project.demo.common.exception.DemoProjectException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtils {

    /**
     * 获取当前用户的用户名
     *
     * @return 当前用户的用户名，如果用户未认证，则返回 null
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
        return null;
    }

    /**
     * 获取当前用户的认证对象
     *
     * @return 当前用户的认证对象
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 判断当前用户是否已认证
     *
     * @return 如果用户已认证，则返回 true；否则返回 false
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    /**
     * 获取当前用户的详细信息
     *
     * @return 当前用户的详细信息，如果用户未认证，则返回 null
     */
    public static MyUserDetails getCurrentUserDetails() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
                return (MyUserDetails) authentication.getPrincipal();
            }
        }catch ( Exception e){
            throw new RuntimeException("获取用户信息异常");
        }
        return null;
    }

    /**
     * 获取当前用户的角色
     *
     * @return 当前用户的角色列表
     */
    public static List<String> getCurrentUserRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * 获取当前用户的userId
     */
    public static Long getCurrentUserId() {
        MyUserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return userDetails.getUserId();
        }
        return null;
    }

    /**
     * 获取当前用户的userId
     */
    public static String getLoginUserId() {
        MyUserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            return userDetails.getUserId().toString();
        } else {
            throw new DemoProjectException("获取用户信息异常");
        }
    }
}
