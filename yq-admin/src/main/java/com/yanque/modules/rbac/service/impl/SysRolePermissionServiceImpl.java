package com.yanque.modules.rbac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.rbac.mapper.SysPermissionMapper;
import com.yanque.modules.rbac.mapper.SysRoleMapper;
import com.yanque.modules.rbac.mapper.SysRolePermissionMapper;
import com.yanque.modules.rbac.mapper.SysUserRoleMapper;
import com.yanque.modules.rbac.pojo.entity.SysRolePermissionEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionSaveReq;
import com.yanque.modules.rbac.pojo.vo.resvo.RolePermissionRes;
import com.yanque.modules.rbac.service.SysRolePermissionService;
import com.yanque.modules.rbac.service.RbacPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRolePermissionServiceImpl implements SysRolePermissionService {
    private final SysRolePermissionMapper relationMapper;
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysUserRoleMapper userRoleMapper;
    private final RbacPermissionService rbacPermissionService;

    @Override
    public PageResult<RolePermissionRes> page(RolePermissionPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<RolePermissionRes> records = relationMapper.selectList(req);
        PageInfo<RolePermissionRes> pageInfo = new PageInfo<>(records);
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    @Override
    public RolePermissionRes detail(Long id) {
        getRelationOrThrow(id);
        return relationMapper.selectResById(id);
    }

    @Override
    @Transactional
    public Long create(RolePermissionSaveReq req) {
        validateRelation(req, null);
        SysRolePermissionEntity entity = new SysRolePermissionEntity();
        entity.setRoleId(req.getRoleId());
        entity.setPermissionId(req.getPermissionId());
        if (relationMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.RELATION_OPERATION_FAILED);
        }
        evictRoleUsers(req.getRoleId());
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, RolePermissionSaveReq req) {
        SysRolePermissionEntity current = getRelationOrThrow(id);
        validateRelation(req, id);
        SysRolePermissionEntity entity = new SysRolePermissionEntity();
        entity.setId(id);
        entity.setRoleId(req.getRoleId());
        entity.setPermissionId(req.getPermissionId());
        if (relationMapper.updateById(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.RELATION_OPERATION_FAILED);
        }
        evictRoleUsers(current.getRoleId());
        evictRoleUsers(req.getRoleId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysRolePermissionEntity current = getRelationOrThrow(id);
        if (relationMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.RELATION_OPERATION_FAILED);
        }
        evictRoleUsers(current.getRoleId());
    }

    private void validateRelation(RolePermissionSaveReq req, Long excludeId) {
        if (roleMapper.selectById(req.getRoleId()) == null) {
            throw BusinessException.of(CommonErrorCode.ROLE_NOT_FOUND);
        }
        if (permissionMapper.selectById(req.getPermissionId()) == null) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_NOT_FOUND);
        }
        if (relationMapper.countUnique(req.getRoleId(), req.getPermissionId(), excludeId) > 0) {
            throw BusinessException.of(CommonErrorCode.ROLE_PERMISSION_ALREADY_EXISTS);
        }
    }

    private SysRolePermissionEntity getRelationOrThrow(Long id) {
        SysRolePermissionEntity entity = id == null ? null : relationMapper.selectById(id);
        if (entity == null) throw BusinessException.of(CommonErrorCode.ROLE_PERMISSION_NOT_FOUND);
        return entity;
    }

    private void evictRoleUsers(Long roleId) {
        List<Long> userIds = userRoleMapper.selectUserIdsByRoleId(roleId);
        if (userIds != null) {
            userIds.forEach(rbacPermissionService::evictUserPermissions);
        }
    }
}
