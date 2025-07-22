package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @author hylogan
 * @since 2024年12月24日 16:48:22
 */
@Getter
@Setter
@TableName("demo_menu")
@Schema(name = "DemoMenu", description = "菜单权限表")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class DemoMenu {

    @Schema(description = "菜单ID")
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    @Schema(description = "菜单名称")
    @TableField("menu_name")
    private String menuName;

    @Schema(description = "父菜单ID")
    @TableField("parent_id")
    private Long parentId;

    @Schema(description = "显示顺序")
    @TableField("order_num")
    private Integer orderNum;

    @Schema(description = "路由地址")
    @TableField("path")
    private String path;

    @Schema(description = "组件路径")
    @TableField("component")
    private String component;

    @Schema(description = "路由参数")
    @TableField("query")
    private String query;

    @Schema(description = "路由名称")
    @TableField("route_name")
    private String routeName;

    @Schema(description = "是否为外链（1是 2否）")
    @TableField("is_frame")
    private Integer isFrame;

    @Schema(description = "是否缓存（1缓存 2不缓存）")
    @TableField("is_cache")
    private Integer isCache;

    @Schema(description = "菜单类型（M目录 C菜单 F按钮）")
    @TableField("menu_type")
    private String menuType;

    @Schema(description = "菜单状态（1显示 2隐藏）")
    @TableField("visible")
    private String visible;

    @Schema(description = "菜单状态（1正常 2停用）")
    @TableField("status")
    private String status;

    @Schema(description = "权限标识")
    @TableField("perms")
    private String perms;

    @Schema(description = "菜单图标")
    @TableField("icon")
    private String icon;

    @Schema(description = "删除标志（1代表存在 2代表删除）")
    @TableField("deleted")
    private String deleted;

    @Schema(description = "版本号")
    @TableField("version")
    @Version
    private Integer version;

    @Schema(description = "创建时间")
    @TableField(value = "create_date", fill = FieldFill.INSERT)
    private Date createDate;

    @Schema(description = "创建人")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "更新时间")
    @TableField(value = "update_date", fill = FieldFill.INSERT_UPDATE)
    private Date updateDate;

    @Schema(description = "更新人")
    @TableField("update_by")
    private String updateBy;
}
