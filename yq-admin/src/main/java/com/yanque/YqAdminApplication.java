package com.yanque;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScans(value = {@MapperScan(value = "com.yanque.modules.users.mapper")})
@SpringBootApplication
public class YqAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(YqAdminApplication.class, args);
    }
}
