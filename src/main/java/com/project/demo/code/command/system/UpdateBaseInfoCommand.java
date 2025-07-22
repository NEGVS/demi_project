package com.project.demo.code.command.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@Schema(name = "UpdateBaseInfoCommand", description = "修改用户基本信息Command")
public class UpdateBaseInfoCommand {

    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户昵称")
    private String nickName;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "用户头像缩略图")
    private String avatarThumbnail;

    @Schema(description = "性别")
    private Integer gender;
}
