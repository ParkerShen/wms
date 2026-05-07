package com.starlink.wms.common.constant;

/**
 * 系统常量
 */
public interface Constants {

    /** Token 前缀 */
    String TOKEN_PREFIX = "Bearer ";

    /** 验证码 Redis 键前缀 */
    String CAPTCHA_KEY = "captcha:";

    /** 登录用户 Redis 键前缀 */
    String LOGIN_USER_KEY = "login:user:";

    /** 菜单缓存 Redis 键前缀 */
    String MENU_KEY = "menu:";

    /** 默认管理员用户ID */
    Long ADMIN_USER_ID = 1L;
}
