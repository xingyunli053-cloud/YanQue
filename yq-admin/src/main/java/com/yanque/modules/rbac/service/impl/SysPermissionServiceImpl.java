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
import com.yanque.modules.rbac.model.PermissionMetadata;
import com.yanque.modules.rbac.pojo.entity.SysPermissionEntity;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionCreateReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionTreeReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionUpdateReq;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionRes;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionTreeRes;
import com.yanque.modules.rbac.service.SysPermissionService;
import com.yanque.modules.rbac.service.RbacPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl implements SysPermissionService {
    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;
    private final RbacPermissionService rbacPermissionService;

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
        evictUsers(rolePermissionMapper.selectUserIdsByPermissionId(id));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getPermissionOrThrow(id);
        if (permissionMapper.countByParentId(id) > 0) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_HAS_CHILDREN);
        }
        List<Long> affectedUserIds = rolePermissionMapper.selectUserIdsByPermissionId(id);
        rolePermissionMapper.deleteByPermissionId(id);
        if (permissionMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_OPERATION_FAILED);
        }
        evictUsers(affectedUserIds);
    }

    @Override
    @Transactional
    public void syncMissingPermissions(List<PermissionMetadata> metadataList) {
        Map<String, PermissionMetadata> metadataByCode = new LinkedHashMap<>();
        for (PermissionMetadata metadata : metadataList) {
            PermissionMetadata previous = metadataByCode.putIfAbsent(metadata.permissionCode(), metadata);
            if (previous != null && !previous.equals(metadata)) {
                throw new IllegalStateException("发现重复且内容不一致的权限编码：" + metadata.permissionCode());
            }
        }

        Set<String> processingCodes = new HashSet<>();
        for (PermissionMetadata metadata : metadataByCode.values()) {
            syncMissingPermission(metadata, metadataByCode, processingCodes);
        }
    }

    private Long syncMissingPermission(PermissionMetadata metadata,
                                       Map<String, PermissionMetadata> metadataByCode,
                                       Set<String> processingCodes) {
        SysPermissionEntity existing = permissionMapper.selectByPermissionCode(metadata.permissionCode());
        // 已有权限可能被管理员禁用、调整层级或授予角色；启动同步绝不覆盖这些人工数据。
        if (existing != null) {
            return existing.getId();
        }
        if (!processingCodes.add(metadata.permissionCode())) {
            throw new IllegalStateException("权限元数据存在循环父子关系：" + metadata.permissionCode());
        }

        Long parentId = 0L;
        if (StrUtil.isNotBlank(metadata.parentCode())) {
            SysPermissionEntity parent = permissionMapper.selectByPermissionCode(metadata.parentCode());
            if (parent == null) {
                PermissionMetadata parentMetadata = metadataByCode.get(metadata.parentCode());
                if (parentMetadata == null) {
                    throw new IllegalStateException("未找到父权限编码：" + metadata.parentCode());
                }
                parentId = syncMissingPermission(parentMetadata, metadataByCode, processingCodes);
            } else {
                parentId = parent.getId();
            }
        }

        SysPermissionEntity entity = new SysPermissionEntity();
        entity.setParentId(parentId);
        entity.setPermissionCode(metadata.permissionCode());
        entity.setPermissionName(metadata.permissionName());
        entity.setPermissionType(metadata.permissionType().name());
        entity.setApiPath(metadata.apiPath());
        entity.setSortNum(metadata.sortNum());
        entity.setDescription(StrUtil.emptyToNull(metadata.description()));
        entity.setStatus(CommonStatusEnum.ACTIVE.name());
        if (permissionMapper.insert(entity) != 1) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_OPERATION_FAILED);
        }
        processingCodes.remove(metadata.permissionCode());
        return entity.getId();
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

    private void evictUsers(List<Long> userIds) {
        if (userIds != null) {
            userIds.forEach(rbacPermissionService::evictUserPermissions);
        }
    }
}
