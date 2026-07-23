package com.yanque.modules.users.service.impl;

import cn.hutool.jwt.JWT;
import com.yanque.commons.config.AuthProperties;
import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.utils.RedisUtils;
import com.yanque.modules.users.mapper.SysUserMapper;
import com.yanque.modules.rbac.service.RbacPermissionService;
import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.vo.reqvo.LoginReq;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysUserServiceImplTest {

    private static final String JWT_SECRET = "test-jwt-secret-with-at-least-32-bytes";
    private static final long EXPIRE_SECONDS = 3600L;
    private static final String REDIS_KEY_PREFIX = "test:auth:token:";

    private RedisUtils redisUtils;
    private SysUserMapper sysUserMapper;
    private RbacPermissionService permissionCache;
    private SysUserServiceImpl service;

    @BeforeEach
    void setUp() {
        redisUtils = mock(RedisUtils.class);
        sysUserMapper = mock(SysUserMapper.class);
        permissionCache = mock(RbacPermissionService.class);

        AuthProperties authProperties = new AuthProperties();
        authProperties.setJwtSecret(JWT_SECRET);
        authProperties.setTokenExpireSeconds(EXPIRE_SECONDS);
        authProperties.setRedisKeyPrefix(REDIS_KEY_PREFIX);
        service = new SysUserServiceImpl(redisUtils, sysUserMapper, authProperties, permissionCache);
    }

    @Test
    void loginCreatesJwtAndStoresTokenAndSignSecret() {
        SysUserEntity user = new SysUserEntity();
        user.setId(10L);
        user.setUsername("admin");
        user.setPassword("123456");
        user.setStatus(CommonStatusEnum.ACTIVE.name());
        when(sysUserMapper.selectByUsername("admin")).thenReturn(user);

        LoginReq request = new LoginReq();
        request.setUsername("admin");
        request.setPassword("123456");

        LoginRes result = service.login(request);

        assertNotNull(result.getToken());
        assertNotNull(result.getSignSecret());

        JWT jwt = JWT.of(result.getToken()).setKey(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
        assertTrue(jwt.verify());
        assertEquals(10L, ((Number) jwt.getPayload(JwtConstants.JWT_CLAIM_USER_ID)).longValue());
        assertTrue(((Number) jwt.getPayload(JwtConstants.JWT_CLAIM_EXPIRE_TIME)).longValue()
                > System.currentTimeMillis());

        Duration expectedTtl = Duration.ofSeconds(EXPIRE_SECONDS);
        verify(redisUtils).set(REDIS_KEY_PREFIX + 10L, result.getToken(), expectedTtl);
        verify(redisUtils).set(
                JwtConstants.SIGN_SECRET_KEY_PREFIX + 10L,
                result.getSignSecret(),
                expectedTtl);
        verify(permissionCache).cacheUserPermissions(10L, expectedTtl);
    }

    @Test
    void logoutClearsCurrentSessionNoncesAndLoginState() {
        String sessionId = "session-001";
        String sessionNonceKey = JwtConstants.SIGN_NONCE_SESSION_KEY_PREFIX + "10:" + sessionId;
        Set<String> nonceKeys = Set.of("yanque:sign:nonce:10:session-001:nonce-a");
        when(redisUtils.getSetMembers(sessionNonceKey)).thenReturn(nonceKeys);

        service.logout(10L, sessionId);

        verify(redisUtils).delete(nonceKeys);
        verify(redisUtils).delete(sessionNonceKey);
        verify(redisUtils).delete(REDIS_KEY_PREFIX + 10L);
        verify(redisUtils).delete(JwtConstants.SIGN_SECRET_KEY_PREFIX + 10L);
        verify(permissionCache).evictUserPermissions(10L);
    }
}
