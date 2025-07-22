package com.project.demo.code.command.system.role;


import com.project.demo.code.domain.common.PageModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Data
public class RoleQueryCommand extends PageModel implements Serializable {
}
