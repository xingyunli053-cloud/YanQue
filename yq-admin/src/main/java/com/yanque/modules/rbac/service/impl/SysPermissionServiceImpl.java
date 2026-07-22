package com.yanque.modules.rbac.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.rbac.mapper.SysPermissionMapper;
import com.yanque.modules.rbac.mapper.SysRolePermissionMapper;
import com.yanque.modules.rbac.pojo.entity.SysPermissionEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionCreateReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionTreeReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionUpdateReq;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionRes;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionTreeRes;
import com.yanque.modules.rbac.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {
    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    @Override
    public PageResult<PermissionRes> page(PermissionPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SysPermissionEntity> entities = permissionMapper.selectList(req);
        PageInfo<SysPermissionEntity> pageInfo = new PageInfo<>(entities);
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(),
                entities.stream().map(this::toRes).toList());
    }

    @Override
    public List<PermissionTreeRes> tree(PermissionTreeReq req) {
        List<SysPermissionEntity> entities = permissionMapper.selectTreeList(req);
        Map<Long, PermissionTreeRes> nodes = new LinkedHashMap<>();
        entities.forEach(entity -> nodes.put(entity.getId(), toTreeRes(entity)));
        List<PermissionTreeRes> roots = new ArrayList<>();
        entities.forEach(entity -> {
            PermissionTreeRes node = nodes.get(entity.getId());
            PermissionTreeRes parent = nodes.get(entity.getParentId());
            if (entity.getParentId() != null && entity.getParentId() != 0 && parent != null) {
                parent.getChildren().add(node);
            } else {
                roots.add(node);
            }
        });
        return roots;
    }

    @Override
    public PermissionRes detail(Long id) {
        return toRes(getPermissionOrThrow(id));
    }

    @Override
    @Transactional
    public Long create(PermissionCreateReq req) {
        String permissionCode = StrUtil.trim(req.getPermissionCode());
        ensureCodeUnique(permissionCode, null);
        validateParent(null, req.getParentId());
        validateApiPath(req.getPermissionType(), req.getApiPath());
        SysPermissionEntity entity = BeanUtil.copyProperties(req, SysPermissionEntity.class);
        entity.setPermissionCode(permissionCode);
        entity.setPermissionName(StrUtil.trim(req.getPermissionName()));
        entity.setStatus(StrUtil.blankToDefault(req.getStatus(), CommonStatusEnum.ACTIVE.name()));
        if (!PermissionTypeEnum.API.name().equals(entity.getPermissionType())) entity.setApiPath(null);
        if (permissionMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_OPERATION_FAILED);
        }
        return entity.getId();
    }

    @Override
    @Transactional
    public void update(Long id, PermissionUpdateReq req) {
        SysPermissionEntity current = getPermissionOrThrow(id);
        if (!hasUpdateField(req)) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "至少需要传入一个待修改字段");
        }
        if (req.getPermissionCode() != null) {
            req.setPermissionCode(StrUtil.trim(req.getPermissionCode()));
            ensureCodeUnique(req.getPermissionCode(), id);
        }
        if (req.getParentId() != null) validateParent(id, req.getParentId());
        String effectiveType = StrUtil.blankToDefault(req.getPermissionType(), current.getPermissionType());
        String effectiveApiPath = req.getApiPath() == null ? current.getApiPath() : req.getApiPath();
        validateApiPath(effectiveType, effectiveApiPath);
        SysPermissionEntity entity = BeanUtil.copyProperties(req, SysPermissionEntity.class);
        entity.setId(id);
        if (req.getPermissionName() != null) entity.setPermissionName(StrUtil.trim(req.getPermissionName()));
        if (!PermissionTypeEnum.API.name().equals(effectiveType)) entity.setApiPath("");
        if (permissionMapper.updateById(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getPermissionOrThrow(id);
        if (permissionMapper.countByParentId(id) > 0) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_HAS_CHILDREN);
        }
        rolePermissionMapper.deleteByPermissionId(id);
        if (permissionMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_OPERATION_FAILED);
        }
    }

    private void validateParent(Long currentId, Long parentId) {
        long normalizedParentId = parentId == null ? 0L : parentId;
        if (normalizedParentId == 0L) return;
        if (normalizedParentId < 0 || normalizedParentId == (currentId == null ? -1L : currentId)) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_PARENT_INVALID);
        }
        getPermissionOrThrow(normalizedParentId);
        if (currentId != null
                && permissionMapper.countDescendantByIdAndCandidate(currentId, normalizedParentId) > 0) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_PARENT_INVALID, "不能将子权限设置为父权限");
        }
    }

    private void validateApiPath(String type, String apiPath) {
        if (PermissionTypeEnum.API.name().equals(type) && StrUtil.isBlank(apiPath)) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "API权限必须填写API路径");
        }
    }

    private void ensureCodeUnique(String code, Long currentId) {
        SysPermissionEntity sameCode = permissionMapper.selectByPermissionCode(code);
        if (sameCode != null && !sameCode.getId().equals(currentId)) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_CODE_ALREADY_EXISTS);
        }
    }

    private SysPermissionEntity getPermissionOrThrow(Long id) {
        SysPermissionEntity permission = id == null ? null : permissionMapper.selectById(id);
        if (permission == null) throw BusinessException.of(CommonErrorCode.PERMISSION_NOT_FOUND);
        return permission;
    }

    private boolean hasUpdateField(PermissionUpdateReq req) {
        return req.getParentId() != null || req.getPermissionCode() != null
                || req.getPermissionName() != null || req.getPermissionType() != null
                || req.getApiPath() != null || req.getSortNum() != null
                || req.getDescription() != null || req.getStatus() != null;
    }

    private PermissionRes toRes(SysPermissionEntity entity) {
        return BeanUtil.copyProperties(entity, PermissionRes.class);
    }

    private PermissionTreeRes toTreeRes(SysPermissionEntity entity) {
        return BeanUtil.copyProperties(entity, PermissionTreeRes.class);
    }
}
