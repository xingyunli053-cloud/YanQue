package com.yanque.modules.rbac.service;

import com.yanque.commons.utils.RedisUtils;
import com.yanque.modules.rbac.mapper.SysPermissionMapper;
import com.yanque.modules.rbac.mapper.SysRoleMapper;
import com.yanque.modules.rbac.service.impl.RbacPermissionServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RbacPermissionServiceTest {

    @Test
    void cachesPermissionCodesAsRedisSetAtLogin() {
        SysPermissionMapper permissionMapper = mock(SysPermissionMapper.class);
        SysRoleMapper roleMapper = mock(SysRoleMapper.class);
        RedisUtils redisUtils = mock(RedisUtils.class);
        RbacPermissionService service = new RbacPermissionServiceImpl(permissionMapper, roleMapper, redisUtils);
        Duration timeout = Duration.ofHours(2);
        when(permissionMapper.selectActivePermissionCodesByUserId(10L))
                .thenReturn(List.of("api:user:page", "api:user:create"));

        service.cacheUserPermissions(10L, timeout);

        verify(redisUtils).delete("yanque:rbac:permission:10");
        verify(redisUtils).addToSet("yanque:rbac:permission:10", "api:user:page", "api:user:create");
        verify(redisUtils).expire("yanque:rbac:permission:10", timeout);
    }

    @Test
    void checksPermissionThroughRedisSetMembership() {
        SysPermissionMapper permissionMapper = mock(SysPermissionMapper.class);
        SysRoleMapper roleMapper = mock(SysRoleMapper.class);
        RedisUtils redisUtils = mock(RedisUtils.class);
        RbacPermissionService service = new RbacPermissionServiceImpl(permissionMapper, roleMapper, redisUtils);
        when(redisUtils.isSetMember("yanque:rbac:permission:10", "api:user:page")).thenReturn(true);

        assertTrue(service.hasPermission(10L, "api:user:page"));
        verify(redisUtils).isSetMember("yanque:rbac:permission:10", "api:user:page");
    }

    @Test
    void cachesWildcardForSuperAdminAndAllowsEveryApi() {
        SysPermissionMapper permissionMapper = mock(SysPermissionMapper.class);
        SysRoleMapper roleMapper = mock(SysRoleMapper.class);
        RedisUtils redisUtils = mock(RedisUtils.class);
        RbacPermissionService service = new RbacPermissionServiceImpl(permissionMapper, roleMapper, redisUtils);
        Duration timeout = Duration.ofHours(2);
        when(roleMapper.selectActiveRoleCodesByUserId(10L)).thenReturn(List.of("SUPER_ADMIN"));
        when(permissionMapper.selectActivePermissionCodesByUserId(10L)).thenReturn(List.of());
        when(redisUtils.isSetMember("yanque:rbac:permission:10", "*")).thenReturn(true);

        service.cacheUserPermissions(10L, timeout);

        verify(redisUtils).addToSet("yanque:rbac:permission:10", "*");
        assertTrue(service.hasPermission(10L, "api:permission:delete"));
    }
}
