package com.project.demo.code.dto.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单返回DTO
 */
@Schema
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleMenuDTO implements Serializable {

    @Schema(description = "菜单id")
    private Long menuId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "父菜单ID")
    private Long parentId;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    private String menuType;

    @Schema(description = "子菜单")
    List<RoleMenuDTO> childrenList;
}
