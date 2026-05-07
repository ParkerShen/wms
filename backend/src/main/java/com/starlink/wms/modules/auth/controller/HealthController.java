package com.starlink.wms.modules.auth.controller;

import com.starlink.wms.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查 Controller - 验证服务是否启动成功
 */
@RestController
public class HealthController {

    @Autowired
    private Environment env;

    @GetMapping("/api/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("status", "UP");
        info.put("appName", env.getProperty("spring.application.name"));
        info.put("activeProfile", String.join(",", env.getActiveProfiles()));
        info.put("time", LocalDateTime.now().toString());
        return Result.success(info);
    }
}
