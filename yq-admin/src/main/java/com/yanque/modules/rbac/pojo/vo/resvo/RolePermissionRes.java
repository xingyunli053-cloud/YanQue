package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 角色权限关联响应。 */
@Data
public class RolePermissionRes {
    /** 角色权限关联记录 ID。 */ private Long id;
    /** 已授权的角色 ID。 */ private Long roleId;
    /** 已授权的角色名称。 */ private String roleName;
    /** 被授予的权限 ID。 */ private Long permissionId;
    /** 被授予的权限名称。 */ private String permissionName;
    /** 授权关系创建时间。 */ private Date createdAt;
}
