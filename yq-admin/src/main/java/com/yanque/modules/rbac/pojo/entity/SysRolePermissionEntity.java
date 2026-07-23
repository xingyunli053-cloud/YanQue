package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 角色权限关联实体，对应 sys_role_permission 表。 */
@Data
public class SysRolePermissionEntity {
    /** 关联记录唯一标识。 */ private Long id;
    /** 被授予权限的角色 ID。 */ private Long roleId;
    /** 授予角色的权限 ID。 */ private Long permissionId;
    /** 关联建立时间。 */ private Date createdAt;
}
