package com.starlink.wms.modules.auth.dto;

import lombok.Data;

/**
 * 用户分页查询参数
 */
@Data
public class UserPageReq {
    private int page = 1;
    private int pageSize = 20;
    private String username;
    private String realName;
    private Integer status;
}
