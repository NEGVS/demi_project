package com.project.demo.code.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author hylogan
 * @since 2024年06月06日 15:28:26
 */
@Getter
@Setter
@TableName("demo_user")
@Schema(name = "DemoUser", description = "用户表")
public class DemoUser {

    @Schema(description = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @Schema(description = "用户名")
    @TableField("username")
    private String username;

    @Schema(description = "密码")
    @TableField("password")
    @JsonIgnore
    private String password;

    @Schema(description = "用户昵称")
    @TableField("nickName")
    private String nickName;

    @Schema(description = "第三方程序ID")
    @TableField("third_party_id")
    @JsonIgnore
    private String thirdPartyId;

    @Schema(description = "用户头像")
    @TableField("avatar")
    private String avatar;

    @Schema(description = "用户头像缩略图")
    @TableField("avatar_thumbnail")
    private String avatarThumbnail;

    @Schema(description = "性别")
    @TableField("gender")
    private Integer gender;

    @Schema(description = "用户类型")
    @TableField("user_type")
    private String userType;

    @Schema(description = "最后登录时间")
    @TableField("last_login_time")
    private Date lastLoginTime;

    @Schema(description = "注册IP地址")
    @TableField("registration_ip")
    @JsonIgnore
    private String registrationIp;

    @Schema(description = "删除标志 1 有效，其他无效")
    @TableField("deleted")
    @JsonIgnore
    private Integer deleted;

    @Schema(description = "版本")
    @TableField("version")
    @Version
    @JsonIgnore
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

    @Schema(description = "角色")
    @TableField(exist = false)
    private List<String> roles;

}
