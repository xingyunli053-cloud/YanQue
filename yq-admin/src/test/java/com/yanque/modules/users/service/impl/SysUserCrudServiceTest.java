package com.yanque.modules.users.service.impl;

import com.github.pagehelper.PageHelper;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.config.AuthProperties;
import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.exception.BusinessException;
import com.yanque.commons.utils.RedisUtils;
import com.yanque.modules.users.mapper.SysUserMapper;
import com.yanque.modules.rbac.service.RbacPermissionService;
import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.vo.reqvo.UserPageReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserUpdateReq;
import com.yanque.modules.users.pojo.vo.resvo.UserRes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SysUserCrudServiceTest {

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
        authProperties.setRedisKeyPrefix(REDIS_KEY_PREFIX);
        service = new SysUserServiceImpl(redisUtils, sysUserMapper, authProperties, permissionCache);
    }

    @AfterEach
    void tearDown() {
        PageHelper.clearPage();
    }

    @Test
    void pageReturnsSafeUserResponses() {
        SysUserEntity user = user(10L, "admin");
        user.setPassword("should-not-be-returned");
        when(sysUserMapper.selectList(org.mockito.ArgumentMatchers.any(UserPageReq.class)))
                .thenReturn(List.of(user));

        UserPageReq request = new UserPageReq();
        PageResult<UserRes> result = service.page(request);

        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getRecords().size());
        assertEquals(10L, result.getRecords().get(0).getId());
        assertEquals("admin", result.getRecords().get(0).getUsername());
    }

    @Test
    void detailThrowsWhenUserDoesNotExist() {
        when(sysUserMapper.selectById(99L)).thenReturn(null);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> service.detail(99L));

        assertEquals(CommonErrorCode.USER_DETAIL_NOT_FOUND.getCode(), exception.getCode());
    }

    @Test
    void updateChangesProvidedFields() {
        when(sysUserMapper.selectById(10L)).thenReturn(user(10L, "old-name"));
        when(sysUserMapper.selectByUsername("new-name")).thenReturn(null);
        when(sysUserMapper.updateById(org.mockito.ArgumentMatchers.any(SysUserEntity.class)))
                .thenReturn(1);

        UserUpdateReq request = new UserUpdateReq();
        request.setUsername("new-name");
        request.setNickname("新昵称");
        service.update(10L, request);

        org.mockito.ArgumentCaptor<SysUserEntity> captor =
                org.mockito.ArgumentCaptor.forClass(SysUserEntity.class);
        verify(sysUserMapper).updateById(captor.capture());
        assertEquals(10L, captor.getValue().getId());
        assertEquals("new-name", captor.getValue().getUsername());
        assertEquals("新昵称", captor.getValue().getNickname());
    }

    @Test
    void deleteRemovesUserAndLoginState() {
        when(sysUserMapper.selectById(10L)).thenReturn(user(10L, "admin"));
        when(sysUserMapper.deleteById(10L)).thenReturn(1);

        service.delete(10L);

        verify(sysUserMapper).deleteById(10L);
        verify(redisUtils).delete(REDIS_KEY_PREFIX + 10L);
        verify(redisUtils).delete(JwtConstants.SIGN_SECRET_KEY_PREFIX + 10L);
        verify(permissionCache).evictUserPermissions(10L);
    }

    private SysUserEntity user(Long id, String username) {
        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        user.setUsername(username);
        user.setStatus("ACTIVE");
        return user;
    }
}
