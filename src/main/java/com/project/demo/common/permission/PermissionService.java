package com.project.demo.common.permission;


import cn.hutool.core.util.StrUtil;
import com.project.demo.code.domain.common.MyUserDetails;
import com.project.demo.common.util.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 自定义权限实现
 */
@Service("hy")
public class PermissionService {
    public static final String ALL_PERMISSION = "*:*:*";

    /**
     * 判断用户是否拥有该权限
     */
    public boolean hasPermi(String permission) {
        if (StrUtil.isEmpty(permission)) {
            return false;
        }
        MyUserDetails loginUser = SecurityUtils.getCurrentUserDetails();
        if (loginUser == null || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        PermissionContextHolder.setContext(permission);
        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 判断是否包含权限
     *
     * @param permissions 权限列表
     * @param permission  权限字符串
     * @return 用户是否具备某权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(StrUtil.trim(permission));
    }
}
