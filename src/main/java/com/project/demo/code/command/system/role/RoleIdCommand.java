package com.project.demo.code.command.system.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "角色ID")
public class RoleIdCommand implements Serializable {
    @Schema(description = "角色ID")
    private Long roleId;
}
