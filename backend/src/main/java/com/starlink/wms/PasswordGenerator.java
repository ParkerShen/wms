package com.starlink.wms;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码生成工具
 * 运行 main 方法即可生成 BCrypt 加密后的密码
 */
public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encoded = encoder.encode(rawPassword);
        System.out.println("明文密码: " + rawPassword);
        System.out.println("BCrypt加密: " + encoded);
    }
}
