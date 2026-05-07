package com.starlink.wms.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 新增/编辑用户请求
 */
@Data
public class UserCreateReq {

    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 64, message = "用户名长度2-64")
    private String username;

    private String password;

    private String realName;

    private String email;

    private String phone;

    private Integer status;

    /** 分配的角色ID列表 */
    private List<Long> roleIds;
}
