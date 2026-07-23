package com.yanque.modules.rbac.service;

import java.time.Duration;

/** 当前用户权限 Redis Set 的读写服务。 */
public interface RbacPermissionService {

    /** 登录成功后查询数据库权限，并写入 Redis Set。 */
    void cacheUserPermissions(Long userId, Duration timeout);

    /** 使用 Redis SISMEMBER 判断用户是否拥有某个权限编码。 */
    boolean hasPermission(Long userId, String permissionCode);

    /** 用户角色或角色权限变化后清理缓存，下一次登录会重新生成。 */
    void evictUserPermissions(Long userId);
}
