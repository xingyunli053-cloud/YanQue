package com.yanque.commons.interceptor;

import cn.hutool.jwt.JWTUtil;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.config.AuthProperties;
import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.context.UserContext;
import com.yanque.commons.exception.BusinessException;
import com.yanque.commons.utils.RedisUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenInterceptorTest {

    private static final String JWT_SECRET = "test-jwt-secret-with-at-least-32-bytes";
    private static final String REDIS_KEY_PREFIX = "test:auth:token:";
    private static final Long USER_ID = 10L;

    private RedisUtils redisUtils;
    private JwtTokenInterceptor interceptor;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        redisUtils = mock(RedisUtils.class);
        AuthProperties authProperties = new AuthProperties();
        authProperties.setJwtSecret(JWT_SECRET);
        authProperties.setRedisKeyPrefix(REDIS_KEY_PREFIX);
        interceptor = new JwtTokenInterceptor(redisUtils, authProperties);
        response = new MockHttpServletResponse();
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void rejectsRequestWhenTokenIsMissing() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(new MockHttpServletRequest(), response, new Object()));

        assertEquals(CommonErrorCode.TOKEN_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void rejectsTamperedToken() {
        String token = createToken(System.currentTimeMillis() + 60_000);
        String tamperedToken = token.substring(0, token.length() - 1)
                + (token.endsWith("a") ? "b" : "a");
        MockHttpServletRequest request = requestWithToken(tamperedToken);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(request, response, new Object()));

        assertEquals(CommonErrorCode.TOKEN_INVALID.getCode(), exception.getCode());
    }

    @Test
    void rejectsExpiredToken() {
        MockHttpServletRequest request = requestWithToken(createToken(System.currentTimeMillis() - 1));

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(request, response, new Object()));

        assertEquals(CommonErrorCode.TOKEN_EXPIRED.getCode(), exception.getCode());
    }

    @Test
    void rejectsTokenMissingFromRedis() {
        String token = createToken(System.currentTimeMillis() + 60_000);
        MockHttpServletRequest request = requestWithToken(token);
        when(redisUtils.get(REDIS_KEY_PREFIX + USER_ID)).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(request, response, new Object()));

        assertEquals(CommonErrorCode.TOKEN_INVALID.getCode(), exception.getCode());
    }

    @Test
    void storesUserInThreadLocalAndAllowsValidRequest() throws Exception {
        String token = createToken(System.currentTimeMillis() + 60_000);
        MockHttpServletRequest request = requestWithToken(token);
        when(redisUtils.get(REDIS_KEY_PREFIX + USER_ID)).thenReturn(token);
        when(redisUtils.get(JwtConstants.SIGN_SECRET_KEY_PREFIX + USER_ID)).thenReturn("sign-secret");

        assertTrue(interceptor.preHandle(request, response, new Object()));
        assertEquals(USER_ID, UserContext.getUserId());
        assertEquals("sign-secret", UserContext.getSignSecret());

        interceptor.afterCompletion(request, response, new Object(), null);
        assertNull(UserContext.getUserId());
        assertNull(UserContext.getSignSecret());
    }

    private MockHttpServletRequest requestWithToken(String token) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(JwtConstants.AUTHORIZATION_HEADER, JwtConstants.BEARER_PREFIX + token);
        return request;
    }

    private String createToken(long expireTime) {
        Map<String, Object> payload = new HashMap<>();
        payload.put(JwtConstants.JWT_CLAIM_USER_ID, USER_ID);
        payload.put(JwtConstants.JWT_CLAIM_EXPIRE_TIME, expireTime);
        payload.put(JwtConstants.JWT_CLAIM_ID, "test-jti");
        return JWTUtil.createToken(payload, JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }
}
