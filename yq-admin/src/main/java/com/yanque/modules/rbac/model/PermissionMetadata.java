package com.yanque.modules.rbac.model;

import com.yanque.modules.rbac.enums.PermissionTypeEnum;

/** 启动扫描阶段使用的权限元数据，不直接暴露给接口层。 */
public record PermissionMetadata(
        String permissionCode,
        String permissionName,
        PermissionTypeEnum permissionType,
        String parentCode,
        String apiPath,
        int sortNum,
        String description) {
}
