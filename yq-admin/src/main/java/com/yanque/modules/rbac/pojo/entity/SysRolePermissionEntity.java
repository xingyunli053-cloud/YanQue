package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 角色权限关联实体，对应 sys_role_permission 表。 */
@Data
public class SysRolePermissionEntity {
    private Long id;
    private Long roleId;
    private Long permissionId;
    private Date createdAt;
}
