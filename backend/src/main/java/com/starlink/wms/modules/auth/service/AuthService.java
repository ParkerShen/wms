package com.starlink.wms.modules.auth.service;

import com.starlink.wms.modules.auth.dto.LoginReq;
import com.starlink.wms.modules.auth.dto.LoginResp;

/**
 * 认证服务接口
 */
public interface AuthService {

    /**
     * 用户登录
     */
    LoginResp login(LoginReq req);

    /**
     * 用户登出
     */
    void logout(Long userId);

    /**
     * 获取当前登录用户信息
     */
    LoginResp.UserInfo getCurrentUser(Long userId);
}
