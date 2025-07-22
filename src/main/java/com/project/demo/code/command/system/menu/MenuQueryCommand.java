package com.project.demo.code.command.system.menu;


import com.project.demo.code.domain.common.PageModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
@EqualsAndHashCode(callSuper = true)
@Data
public class MenuQueryCommand extends PageModel implements Serializable {
}
