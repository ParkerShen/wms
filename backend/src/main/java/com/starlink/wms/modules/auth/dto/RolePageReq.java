package com.starlink.wms.modules.auth.dto;

import lombok.Data;

@Data
public class RolePageReq {
    private int page = 1;
    private int pageSize = 20;
    private String roleName;
    private Integer status;
}
