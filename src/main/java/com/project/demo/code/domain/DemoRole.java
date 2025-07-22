package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import java.util.Date;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色信息表
 * </p>
 *
 * @author hylogan
 * @since 2024年12月25日 16:44:06
 */
@Getter
@Setter
@TableName("demo_role")
@Schema(name = "DemoRole", description = "角色信息表")
public class DemoRole {

    @Schema(description = "角色ID")
    @TableId(value = "role_id", type = IdType.AUTO)
    private Long roleId;

    @Schema(description = "角色名称")
    @TableField("role_name")
    private String roleName;

    @Schema(description = "角色权限字符串")
    @TableField("role_key")
    private String roleKey;

    @Schema(description = "显示顺序")
    @TableField("role_sort")
    private Integer roleSort;

    @Schema(description = "角色状态（1正常 2停用）")
    @TableField("status")
    private String status;

    @Schema(description = "删除标志（1代表存在 2代表删除）")
    @TableField("deleted")
    private String deleted;

    @Schema(description = "版本号")
    @TableField("version")
    @Version
    private Integer version;

    @Schema(description = "创建者")
    @TableField("create_by")
    private String createBy;

    @Schema(description = "创建时间")
    @TableField("create_date")
    private Date createDate;

    @Schema(description = "更新者")
    @TableField("update_by")
    private String updateBy;

    @Schema(description = "更新时间")
    @TableField("update_date")
    private Date updateDate;
}
