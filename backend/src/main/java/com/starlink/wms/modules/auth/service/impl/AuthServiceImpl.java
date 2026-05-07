package com.starlink.wms.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.starlink.wms.common.exception.BusinessException;
import com.starlink.wms.modules.auth.dto.LoginReq;
import com.starlink.wms.modules.auth.dto.LoginResp;
import com.starlink.wms.modules.auth.entity.SysUser;
import com.starlink.wms.modules.auth.mapper.SysUserMapper;
import com.starlink.wms.modules.auth.service.AuthService;
import com.starlink.wms.utils.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(SysUserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public LoginResp login(LoginReq req) {
        // 1. 根据用户名查询用户
        SysUser user = userMapper.selectOne(
                Wrappers.<SysUser>lambdaQuery()
                        .eq(SysUser::getUsername, req.getUsername())
                        .eq(SysUser::getDeleted, 0));

        if (user == null) {
            log.warn("登录失败 - 用户不存在: {}", req.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        // 2. 校验密码（使用 BCrypt）
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            log.warn("登录失败 - 密码错误: {}", req.getUsername());
            throw new BusinessException("用户名或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == null || user.getStatus() != 1) {
            log.warn("登录失败 - 用户已被禁用: {}", req.getUsername());
            throw new BusinessException("该账号已被禁用");
        }

        // 4. 生成 Token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername());
        log.info("登录成功 - userId: {}, username: {}", user.getId(), user.getUsername());

        // 5. 构建响应
        LoginResp.UserInfo userInfo = LoginResp.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .build();

        return LoginResp.builder()
                .token(token)
                .tokenType("Bearer")
                .user(userInfo)
                .build();
    }

    @Override
    public void logout(Long userId) {
        log.info("用户登出 - userId: {}", userId);
        // JWT 无状态，直接让前端清除 Token 即可
        // 如果需要黑名单机制，可以将 Token 加入 Redis 黑名单
    }

    @Override
    public LoginResp.UserInfo getCurrentUser(Long userId) {
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        return LoginResp.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .avatar(user.getAvatar())
                .email(user.getEmail())
                .build();
    }
}
