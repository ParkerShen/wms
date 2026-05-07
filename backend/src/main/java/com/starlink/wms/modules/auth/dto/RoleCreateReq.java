package com.starlink.wms.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class RoleCreateReq {
    private Long id;
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    @NotBlank(message = "角色标识不能为空")
    private String roleKey;
    private Integer sortOrder;
    private Integer status;
    private String remark;
    /** 分配的菜单ID列表 */
    private List<Long> menuIds;
}
