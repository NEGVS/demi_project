package com.project.demo.code.command.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(name = "UserCommand", description = "用户注册、登录Command")
public class UserCommand {
    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(hidden = true, description = "第三方程序ID")
    private String thirdPartyId;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户头像缩略图")
    private String avatarThumbnail;

    @Schema(description = "性别")
    private Integer gender;

}
