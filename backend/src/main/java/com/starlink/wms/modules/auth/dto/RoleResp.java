package com.starlink.wms.modules.auth.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class RoleResp {
    private Long id;
    private String roleName;
    private String roleKey;
    private Integer sortOrder;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    /** 已分配的菜单ID列表 */
    private List<Long> menuIds;
}
