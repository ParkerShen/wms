package com.starlink.wms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * CORS 已在 SecurityConfig 中配置，此处处理其他 Web 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 后续可添加：拦截器、静态资源映射、视图控制器等
}
