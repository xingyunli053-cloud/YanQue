package com.yanque.modules.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.rbac.mapper.SysPermissionMapper;
import com.yanque.modules.rbac.mapper.SysRoleMapper;
import com.yanque.modules.rbac.mapper.SysRolePermissionMapper;
import com.yanque.modules.rbac.mapper.SysUserRoleMapper;
import com.yanque.modules.rbac.pojo.entity.SysRoleEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.RoleCreateReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RoleUpdateReq;
import com.yanque.modules.rbac.pojo.vo.resvo.RoleRes;
import com.yanque.modules.rbac.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl implements SysRoleService {
    private final SysRoleMapper roleMapper;
    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final SysUserRoleMapper userRoleMapper;

    @Override
    public PageResult<RoleRes> page(RolePageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SysRoleEntity> entities = roleMapper.selectList(req);
        PageInfo<SysRoleEntity> pageInfo = new PageInfo<>(entities);
        // 分页结果直接携带权限ID，前端可以在角色列表中展示权限摘要，避免逐条请求详情。
        List<RoleRes> records = entities.stream().map(entity -> {
            RoleRes response = toRes(entity);
            response.setPermissionIds(rolePermissionMapper.selectPermissionIdsByRoleId(entity.getId()));
            return response;
        }).toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    @Override
    public RoleRes detail(Long id) {
        RoleRes response = toRes(getRoleOrThrow(id));
        response.setPermissionIds(rolePermissionMapper.selectPermissionIdsByRoleId(id));
        return response;
    }

    @Override
    @Transactional
    public Long create(RoleCreateReq req) {
        String roleCode = StrUtil.trim(req.getRoleCode());
        ensureRoleCodeUnique(roleCode, null);
        SysRoleEntity entity = BeanUtil.copyProperties(req, SysRoleEntity.class);
        entity.setRoleCode(roleCode);
        entity.setRoleName(StrUtil.trim(req.getRoleName()));
        entity.setStatus(StrUtil.blankToDefault(req.getStatus(), CommonStatusEnum.ACTIVE.name()));
        if (roleMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.ROLE_OPERATION_FAILED);
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, RoleUpdateReq req) {
        getRoleOrThrow(id);
        if (!hasUpdateField(req)) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "至少需要传入一个待修改字段");
        }
        if (req.getRoleCode() != null) {
            req.setRoleCode(StrUtil.trim(req.getRoleCode()));
            ensureRoleCodeUnique(req.getRoleCode(), id);
        }
        SysRoleEntity entity = BeanUtil.copyProperties(req, SysRoleEntity.class);
        entity.setId(id);
        if (req.getRoleName() != null) entity.setRoleName(StrUtil.trim(req.getRoleName()));
        if (roleMapper.updateById(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.ROLE_OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getRoleOrThrow(id);
        rolePermissionMapper.deleteByRoleId(id);
        userRoleMapper.deleteByRoleId(id);
        if (roleMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.ROLE_OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void assignPermissions(Long id, List<Long> permissionIds) {
        getRoleOrThrow(id);
        List<Long> distinctIds = new ArrayList<>(new LinkedHashSet<>(permissionIds));
        if (distinctIds.stream().anyMatch(permissionId -> permissionId == null || permissionId <= 0)) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "权限ID必须大于0");
        }
        if (!distinctIds.isEmpty()
                && permissionMapper.selectExistingIds(distinctIds).size() != distinctIds.size()) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_NOT_FOUND);
        }
        rolePermissionMapper.deleteByRoleId(id);
        if (!distinctIds.isEmpty()) rolePermissionMapper.batchInsert(id, distinctIds);
    }

    private SysRoleEntity getRoleOrThrow(Long id) {
        SysRoleEntity role = id == null ? null : roleMapper.selectById(id);
        if (role == null) throw BusinessException.of(CommonErrorCode.ROLE_NOT_FOUND);
        return role;
    }

    private void ensureRoleCodeUnique(String roleCode, Long currentId) {
        SysRoleEntity sameCode = roleMapper.selectByRoleCode(roleCode);
        if (sameCode != null && !sameCode.getId().equals(currentId)) {
            throw BusinessException.of(CommonErrorCode.ROLE_CODE_ALREADY_EXISTS);
        }
    }

    private boolean hasUpdateField(RoleUpdateReq req) {
        return req.getRoleCode() != null || req.getRoleName() != null
                || req.getDescription() != null || req.getStatus() != null;
    }

    private RoleRes toRes(SysRoleEntity entity) {
        return BeanUtil.copyProperties(entity, RoleRes.class);
    }
}
