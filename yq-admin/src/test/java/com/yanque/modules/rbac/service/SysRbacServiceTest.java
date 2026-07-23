package com.yanque.modules.rbac.service;

import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.rbac.mapper.SysPermissionMapper;
import com.yanque.modules.rbac.mapper.SysRoleMapper;
import com.yanque.modules.rbac.mapper.SysRolePermissionMapper;
import com.yanque.modules.rbac.mapper.SysUserRoleMapper;
import com.yanque.modules.rbac.pojo.entity.SysPermissionEntity;
import com.yanque.modules.rbac.pojo.entity.SysRoleEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionTreeReq;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionTreeRes;
import com.yanque.modules.rbac.model.PermissionMetadata;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.rbac.service.impl.SysPermissionServiceImpl;
import com.yanque.modules.rbac.service.impl.SysRoleServiceImpl;
import com.yanque.modules.rbac.service.impl.SysUserRoleServiceImpl;
import com.yanque.modules.users.mapper.SysUserMapper;
import com.yanque.modules.users.pojo.entity.SysUserEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;

class SysRbacServiceTest {

    @Test
    void assignPermissionsDeduplicatesAndReplacesRelations() {
        SysRoleMapper roleMapper = mock(SysRoleMapper.class);
        SysPermissionMapper permissionMapper = mock(SysPermissionMapper.class);
        SysRolePermissionMapper relationMapper = mock(SysRolePermissionMapper.class);
        SysUserRoleMapper userRoleMapper = mock(SysUserRoleMapper.class);
        RbacPermissionService permissionCache = mock(RbacPermissionService.class);
        SysRoleEntity role = new SysRoleEntity();
        role.setId(1L);
        when(roleMapper.selectById(1L)).thenReturn(role);
        when(permissionMapper.selectExistingIds(List.of(1L, 2L))).thenReturn(List.of(1L, 2L));
        SysRoleServiceImpl service = new SysRoleServiceImpl(
                roleMapper, permissionMapper, relationMapper, userRoleMapper, permissionCache);

        service.assignPermissions(1L, List.of(1L, 2L, 1L));

        verify(relationMapper).deleteByRoleId(1L);
        verify(relationMapper).batchInsert(1L, List.of(1L, 2L));
    }

    @Test
    void treeBuildsParentChildRelationship() {
        SysPermissionMapper permissionMapper = mock(SysPermissionMapper.class);
        SysRolePermissionMapper relationMapper = mock(SysRolePermissionMapper.class);
        RbacPermissionService permissionCache = mock(RbacPermissionService.class);
        SysPermissionEntity root = permission(1L, 0L, "系统管理");
        SysPermissionEntity child = permission(2L, 1L, "用户管理");
        when(permissionMapper.selectTreeList(org.mockito.ArgumentMatchers.any()))
                .thenReturn(List.of(root, child));
        SysPermissionServiceImpl service = new SysPermissionServiceImpl(permissionMapper, relationMapper, permissionCache);

        List<PermissionTreeRes> tree = service.tree(new PermissionTreeReq());

        assertEquals(1, tree.size());
        assertEquals(1, tree.get(0).getChildren().size());
        assertEquals(2L, tree.get(0).getChildren().get(0).getId());
    }

    @Test
    void deletePermissionWithChildrenIsRejected() {
        SysPermissionMapper permissionMapper = mock(SysPermissionMapper.class);
        SysRolePermissionMapper relationMapper = mock(SysRolePermissionMapper.class);
        RbacPermissionService permissionCache = mock(RbacPermissionService.class);
        when(permissionMapper.selectById(1L)).thenReturn(permission(1L, 0L, "系统管理"));
        when(permissionMapper.countByParentId(1L)).thenReturn(1);
        SysPermissionServiceImpl service = new SysPermissionServiceImpl(permissionMapper, relationMapper, permissionCache);

        BusinessException exception = assertThrows(BusinessException.class, () -> service.delete(1L));

        assertEquals(CommonErrorCode.PERMISSION_HAS_CHILDREN.getCode(), exception.getCode());
    }

    @Test
    void assignUserRolesDeduplicatesAndReplacesRelations() {
        SysUserRoleMapper relationMapper = mock(SysUserRoleMapper.class);
        SysUserMapper userMapper = mock(SysUserMapper.class);
        SysRoleMapper roleMapper = mock(SysRoleMapper.class);
        RbacPermissionService permissionCache = mock(RbacPermissionService.class);
        SysUserEntity user = new SysUserEntity();
        user.setId(1L);
        when(userMapper.selectById(1L)).thenReturn(user);
        when(roleMapper.selectExistingIds(List.of(1L, 2L))).thenReturn(List.of(1L, 2L));
        SysUserRoleServiceImpl service = new SysUserRoleServiceImpl(relationMapper, userMapper, roleMapper, permissionCache);

        service.assignRoles(1L, List.of(1L, 2L, 1L));

        verify(relationMapper).deleteByUserId(1L);
        verify(relationMapper).batchInsert(1L, List.of(1L, 2L));
    }

    @Test
    void permissionMetadataSyncOnlyInsertsMissingRecords() {
        SysPermissionMapper permissionMapper = mock(SysPermissionMapper.class);
        SysRolePermissionMapper relationMapper = mock(SysRolePermissionMapper.class);
        RbacPermissionService permissionCache = mock(RbacPermissionService.class);
        Map<String, SysPermissionEntity> storedPermissions = new ConcurrentHashMap<>();
        AtomicLong nextId = new AtomicLong(1L);
        when(permissionMapper.selectByPermissionCode(any())).thenAnswer(invocation ->
                storedPermissions.get(invocation.getArgument(0)));
        doAnswer(invocation -> {
            SysPermissionEntity entity = invocation.getArgument(0);
            entity.setId(nextId.getAndIncrement());
            storedPermissions.put(entity.getPermissionCode(), entity);
            return 1;
        }).when(permissionMapper).insert(any(SysPermissionEntity.class));
        SysPermissionServiceImpl service = new SysPermissionServiceImpl(
                permissionMapper, relationMapper, permissionCache);

        List<PermissionMetadata> metadata = List.of(
                new PermissionMetadata("system", "系统管理", PermissionTypeEnum.MENU,
                        "", null, 10, ""),
                new PermissionMetadata("api:user:page", "分页查询用户", PermissionTypeEnum.API,
                        "system", "GET /yq-admin/api/sysUser", 1111, ""));

        service.syncMissingPermissions(metadata);
        service.syncMissingPermissions(metadata);

        assertEquals(2, storedPermissions.size());
        assertEquals(1L, storedPermissions.get("api:user:page").getParentId());
        assertEquals("GET /yq-admin/api/sysUser", storedPermissions.get("api:user:page").getApiPath());
        verify(permissionMapper, times(2)).insert(any(SysPermissionEntity.class));
    }

    private SysPermissionEntity permission(Long id, Long parentId, String name) {
        SysPermissionEntity entity = new SysPermissionEntity();
        entity.setId(id);
        entity.setParentId(parentId);
        entity.setPermissionName(name);
        entity.setPermissionCode("permission:" + id);
        entity.setPermissionType("MENU");
        entity.setSortNum(id.intValue());
        entity.setStatus("ACTIVE");
        return entity;
    }
}
