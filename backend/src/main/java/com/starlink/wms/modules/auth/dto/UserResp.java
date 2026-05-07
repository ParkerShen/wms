package com.starlink.wms.modules.auth.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户响应 DTO（脱敏，不返回密码）
 */
@Data
public class UserResp {
    private Long id;
    private String username;
    private String realName;
    private String email;
    private String phone;
    private String avatar;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
    /** 用户角色ID列表 */
    private List<Long> roleIds;
}
