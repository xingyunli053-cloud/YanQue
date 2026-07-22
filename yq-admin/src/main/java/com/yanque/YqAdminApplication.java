package com.yanque;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** 只扫描 Mapper 包，避免将 Service 接口误注册成 MyBatis Mapper。 */
@MapperScan({"com.yanque.modules.users.mapper", "com.yanque.modules.rbac.mapper"})
@SpringBootApplication
public class YqAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(YqAdminApplication.class, args);
    }
}
