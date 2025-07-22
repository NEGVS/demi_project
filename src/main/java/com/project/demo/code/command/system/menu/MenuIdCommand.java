package com.project.demo.code.command.system.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "菜单ID")
public class MenuIdCommand implements Serializable {
    @Schema(description = "菜单ID")
    private Long menuId;
}
