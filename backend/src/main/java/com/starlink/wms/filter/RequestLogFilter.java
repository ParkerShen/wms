package com.starlink.wms.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * 请求日志过滤器
 * 记录每次请求的方法、URL、IP、耗时、状态码
 */
@Component
@Order(1)
public class RequestLogFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestLogFilter.class);
    private static final Logger apiLog = LoggerFactory.getLogger("com.starlink.wms.filter.RequestLogFilter");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // 生成请求追踪ID
        String traceId = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        MDC.put("traceId", traceId);

        long startTime = System.currentTimeMillis();
        String method = req.getMethod();
        String uri = req.getRequestURI();
        String query = req.getQueryString();
        String ip = getClientIp(req);

        try {
            chain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = res.getStatus();
            String fullPath = query != null ? uri + "?" + query : uri;

            // 跳过静态资源
            if (!uri.contains(".") && !uri.startsWith("/static")) {
                apiLog.info("[{}] {} {} {} {}ms", traceId, method, fullPath, status, duration);
                log.info("请求追踪 [{}] {} {} → {} ({}ms) IP: {}", traceId, method, fullPath, status, duration, ip);
            }

            MDC.clear();
        }
    }

    private String getClientIp(HttpServletRequest req) {
        String ip = req.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) ip = req.getRemoteAddr();
        return ip;
    }
}
