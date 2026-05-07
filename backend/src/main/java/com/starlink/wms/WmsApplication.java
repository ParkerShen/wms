package com.starlink.wms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 星瀚WMS - 仓储管理系统启动类
 *
 * @author starlink
 */
@SpringBootApplication
@MapperScan("com.starlink.wms.**.mapper")
public class WmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(WmsApplication.class, args);
    }
}
