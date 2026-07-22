package com.yanque.commons.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 用户认证配置。
 *
 * <p>属性来源于 application.yaml 中的 {@code app.auth} 配置段。</p>
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    /** 使用 HS256 算法签发 JWT 时使用的密钥。 */
    private String jwtSecret;

    /** 登录 token 的有效期，单位为秒。 */
    private long tokenExpireSeconds = 24 * 60 * 60;

    /** 登录 token 写入 Redis 时使用的键前缀，完整键为此前缀加用户 ID。 */
    private String redisKeyPrefix = "yanque:auth:token:";
}
