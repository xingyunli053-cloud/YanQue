package com.yanque.commons.config;

import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.interceptor.JwtTokenInterceptor;
import com.yanque.commons.interceptor.SignInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 拦截器配置。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtTokenInterceptor jwtTokenInterceptor;
    private final SignInterceptor signInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtTokenInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(JwtConstants.LOGIN_PATH);

        // 签名拦截器必须注册在 token 拦截器之后，才能读取 UserContext 中的 uid 和签名密钥。
        registry.addInterceptor(signInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(JwtConstants.LOGIN_PATH);
    }
}
