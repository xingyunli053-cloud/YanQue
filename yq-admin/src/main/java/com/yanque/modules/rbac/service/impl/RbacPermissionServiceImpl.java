package com.yanque.modules.rbac.service.impl;

import com.yanque.commons.utils.RedisUtils;
import com.yanque.modules.rbac.mapper.SysPermissionMapper;
import com.yanque.modules.rbac.mapper.SysRoleMapper;
import com.yanque.modules.rbac.service.RbacPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 将用户的有效权限编码缓存为 Redis Set。
 *
 * <p>Set 的成员就是 permission_code，RBAC 拦截器通过 SISMEMBER 完成 O(1) 权限判断。</p>
 */
@Service
@RequiredArgsConstructor
public class RbacPermissionServiceImpl implements RbacPermissionService {

    private static final String USER_PERMISSION_KEY_PREFIX = "yanque:rbac:permission:";
    private static final String SUPER_ADMIN_ROLE_CODE = "SUPER_ADMIN";
    /** Redis Set 内的内部通配成员，不能作为真实 permission_code 使用。 */
    private static final String ALL_PERMISSION_MARKER = "*";

    private final SysPermissionMapper permissionMapper;
    private final SysRoleMapper roleMapper;
    private final RedisUtils redisUtils;

    @Override
    public void cacheUserPermissions(Long userId, Duration timeout) {
        String cacheKey = buildCacheKey(userId);
        // 同一用户重新登录时，必须先移除旧集合，避免遗留权限继续生效。
        redisUtils.delete(cacheKey);

        List<String> roleCodes = roleMapper.selectActiveRoleCodesByUserId(userId);
        boolean superAdmin = roleCodes != null && roleCodes.contains(SUPER_ADMIN_ROLE_CODE);
        List<String> permissionCodes = permissionMapper.selectActivePermissionCodesByUserId(userId);
        if (!superAdmin && (permissionCodes == null || permissionCodes.isEmpty())) {
            return;
        }

        // SUPER_ADMIN 写入通配成员，避免将每个 API 权限逐条复制到管理员的 Set 中。
        if (superAdmin) {
            redisUtils.addToSet(cacheKey, ALL_PERMISSION_MARKER);
        }
        if (permissionCodes != null && !permissionCodes.isEmpty()) {
            redisUtils.addToSet(cacheKey, permissionCodes.toArray(String[]::new));
        }
        redisUtils.expire(cacheKey, timeout);
    }

    @Override
    public boolean hasPermission(Long userId, String permissionCode) {
        String cacheKey = buildCacheKey(userId);
        // 先判断通配成员；SUPER_ADMIN 命中后，无需再检查具体 API 权限编码。
        return Boolean.TRUE.equals(redisUtils.isSetMember(cacheKey, ALL_PERMISSION_MARKER))
                || Boolean.TRUE.equals(redisUtils.isSetMember(cacheKey, permissionCode));
    }

    @Override
    public void evictUserPermissions(Long userId) {
        if (userId != null) {
            redisUtils.delete(buildCacheKey(userId));
        }
    }

    private String buildCacheKey(Long userId) {
        return USER_PERMISSION_KEY_PREFIX + userId;
    }
}
