package com.yanque.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("YanQue Admin API")
                        .version("1.0")
                        .description("YanQue 后台管理系统接口文档")
                        .contact(new Contact()
                                .name("YanQue")
                                .email("admin@yanque.com")));
    }
}
