package com.yanque.modules.rbac.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.rbac.mapper.SysRoleMapper;
import com.yanque.modules.rbac.mapper.SysUserRoleMapper;
import com.yanque.modules.rbac.pojo.entity.SysUserRoleEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRolePageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRoleSaveReq;
import com.yanque.modules.rbac.pojo.vo.resvo.UserRoleRes;
import com.yanque.modules.rbac.service.SysUserRoleService;
import com.yanque.modules.rbac.service.RbacPermissionService;
import com.yanque.modules.users.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashSet;

@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl implements SysUserRoleService {
    private final SysUserRoleMapper relationMapper;
    private final SysUserMapper userMapper;
    private final SysRoleMapper roleMapper;
    private final RbacPermissionService rbacPermissionService;

    @Override
    public PageResult<UserRoleRes> page(UserRolePageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<UserRoleRes> records = relationMapper.selectList(req);
        PageInfo<UserRoleRes> pageInfo = new PageInfo<>(records);
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    @Override
    public UserRoleRes detail(Long id) {
        getRelationOrThrow(id);
        return relationMapper.selectResById(id);
    }

    @Override
    @Transactional
    public Long create(UserRoleSaveReq req) {
        validateRelation(req, null);
        SysUserRoleEntity entity = new SysUserRoleEntity();
        entity.setUserId(req.getUserId());
        entity.setRoleId(req.getRoleId());
        if (relationMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.RELATION_OPERATION_FAILED);
        }
        rbacPermissionService.evictUserPermissions(req.getUserId());
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, UserRoleSaveReq req) {
        SysUserRoleEntity current = getRelationOrThrow(id);
        validateRelation(req, id);
        SysUserRoleEntity entity = new SysUserRoleEntity();
        entity.setId(id);
        entity.setUserId(req.getUserId());
        entity.setRoleId(req.getRoleId());
        if (relationMapper.updateById(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.RELATION_OPERATION_FAILED);
        }
        rbacPermissionService.evictUserPermissions(current.getUserId());
        rbacPermissionService.evictUserPermissions(req.getUserId());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        SysUserRoleEntity current = getRelationOrThrow(id);
        if (relationMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.RELATION_OPERATION_FAILED);
        }
        rbacPermissionService.evictUserPermissions(current.getUserId());
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        getUserOrThrow(userId);
        return relationMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        getUserOrThrow(userId);
        List<Long> distinctIds = new ArrayList<>(new LinkedHashSet<>(roleIds));
        if (distinctIds.stream().anyMatch(roleId -> roleId == null || roleId <= 0)) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "角色ID必须大于0");
        }
        if (!distinctIds.isEmpty() && roleMapper.selectExistingIds(distinctIds).size() != distinctIds.size()) {
            throw BusinessException.of(CommonErrorCode.ROLE_NOT_FOUND);
        }
        relationMapper.deleteByUserId(userId);
        if (!distinctIds.isEmpty()) relationMapper.batchInsert(userId, distinctIds);
        rbacPermissionService.evictUserPermissions(userId);
    }

    private void validateRelation(UserRoleSaveReq req, Long excludeId) {
        getUserOrThrow(req.getUserId());
        if (roleMapper.selectById(req.getRoleId()) == null) {
            throw BusinessException.of(CommonErrorCode.ROLE_NOT_FOUND);
        }
        if (relationMapper.countUnique(req.getUserId(), req.getRoleId(), excludeId) > 0) {
            throw BusinessException.of(CommonErrorCode.USER_ROLE_ALREADY_EXISTS);
        }
    }

    private void getUserOrThrow(Long userId) {
        if (userId == null || userMapper.selectById(userId) == null) {
            throw BusinessException.of(CommonErrorCode.USER_DETAIL_NOT_FOUND);
        }
    }

    private SysUserRoleEntity getRelationOrThrow(Long id) {
        SysUserRoleEntity entity = id == null ? null : relationMapper.selectById(id);
        if (entity == null) throw BusinessException.of(CommonErrorCode.USER_ROLE_NOT_FOUND);
        return entity;
    }
}
