package com.starlink.wms.modules.auth.controller;

import com.starlink.wms.common.Result;
import com.starlink.wms.modules.auth.dto.LoginReq;
import com.starlink.wms.modules.auth.dto.LoginResp;
import com.starlink.wms.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 认证 Controller - 处理登录/登出/获取用户信息
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody LoginReq req) {
        log.info("用户登录请求: {}", req.getUsername());
        LoginResp resp = authService.login(req);
        return Result.success("登录成功", resp);
    }

    /**
     * 用户登出
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public Result<Void> logout(@RequestAttribute(required = false) Long userId) {
        if (userId != null) {
            authService.logout(userId);
        }
        return Result.success("退出成功");
    }

    /**
     * 获取当前登录用户信息
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public Result<LoginResp.UserInfo> me(@RequestAttribute Long userId) {
        LoginResp.UserInfo userInfo = authService.getCurrentUser(userId);
        return Result.success(userInfo);
    }
}
