package com.yanque.commons.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.config.AuthProperties;
import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.context.UserContext;
import com.yanque.commons.exception.BusinessException;
import com.yanque.commons.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

/**
 * 登录 token 拦截器，负责校验 JWT 和 Redis 登录状态。
 */
@Component
@RequiredArgsConstructor
public class JwtTokenInterceptor implements HandlerInterceptor {

    private final RedisUtils redisUtils;
    private final AuthProperties authProperties;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 防御性清理，避免线程池复用时读取到残留用户信息。
        UserContext.clear();

        // 1. 从 Authorization 请求头获取 token，兼容 "Bearer token" 和直接传 token。
        String token = resolveToken(request);
        if (StrUtil.isBlank(token)) {
            throw BusinessException.of(CommonErrorCode.TOKEN_NOT_FOUND);
        }

        // 2. token 格式错误时 Hutool 会抛出解析异常，统一转换为业务异常。
        JWT jwt;
        try {
            jwt = JWTUtil.parseToken(token);
        } catch (RuntimeException exception) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }

        // 3. 使用登录签发时相同的密钥校验签名，签名不一致说明 token 被篡改。
        String jwtSecret = authProperties.getJwtSecret();
        if (StrUtil.isBlank(jwtSecret) || !verifySignature(jwt, jwtSecret)) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }

        // 4. 从已验签的 token 中解析 uid 和过期时间。
        Long userId = parseLongClaim(jwt, JwtConstants.JWT_CLAIM_USER_ID);
        Long expireTime = parseLongClaim(jwt, JwtConstants.JWT_CLAIM_EXPIRE_TIME);
        String sessionId = parseStringClaim(jwt, JwtConstants.JWT_CLAIM_ID);
        if (expireTime <= System.currentTimeMillis()) {
            throw BusinessException.of(CommonErrorCode.TOKEN_EXPIRED);
        }

        // 5. Redis 中必须存在与当前 uid 完全匹配的 token，否则登录状态已失效。
        String cachedToken = redisUtils.get(authProperties.getRedisKeyPrefix() + userId);
        if (StrUtil.isBlank(cachedToken) || !token.equals(cachedToken)) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }

        // 6. 获取该用户的请求签名密钥，并将用户信息保存到当前线程。
        String signSecret = redisUtils.get(JwtConstants.SIGN_SECRET_KEY_PREFIX + userId);
        if (StrUtil.isBlank(signSecret)) {
            throw BusinessException.of(CommonErrorCode.SIGN_SECRET_NOT_FOUND);
        }
        UserContext.set(userId, signSecret, sessionId);

        // 7. 所有校验通过，放行请求。
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception exception) {
        // 请求完成后必须清理 ThreadLocal，防止用户信息在线程复用时串号。
        UserContext.clear();
    }

    private String resolveToken(HttpServletRequest request) {
        String authorization = request.getHeader(JwtConstants.AUTHORIZATION_HEADER);
        if (StrUtil.isBlank(authorization)) {
            return null;
        }
        String value = authorization.trim();
        if (value.regionMatches(true, 0,
                JwtConstants.BEARER_PREFIX, 0, JwtConstants.BEARER_PREFIX.length())) {
            value = value.substring(JwtConstants.BEARER_PREFIX.length()).trim();
        }
        return value;
    }

    private boolean verifySignature(JWT jwt, String jwtSecret) {
        try {
            return jwt.setKey(jwtSecret.getBytes(StandardCharsets.UTF_8)).verify();
        } catch (RuntimeException exception) {
            return false;
        }
    }

    private Long parseLongClaim(JWT jwt, String claimName) {
        Object value = jwt.getPayload(claimName);
        if (value == null) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }
        try {
            return Long.valueOf(value.toString());
        } catch (NumberFormatException exception) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }
    }

    private String parseStringClaim(JWT jwt, String claimName) {
        Object value = jwt.getPayload(claimName);
        if (value == null || StrUtil.isBlank(value.toString())) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }
        return value.toString();
    }
}
