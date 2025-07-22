package com.project.demo.code.command.system.rolemenu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 权限执行命令 ( 角色添加 - 菜单权限 )
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Schema(description = "权限执行命令( 角色 - 菜单权限 )")
public class PermissionExecuteCommand {

    @Schema(description = "菜单id")
    private List<Long> menuIdList;

    @Schema(description = "角色id")
    private Long roleId;
}
