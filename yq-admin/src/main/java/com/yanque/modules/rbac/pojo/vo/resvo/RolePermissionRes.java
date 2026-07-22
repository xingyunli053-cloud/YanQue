package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 角色权限关联响应。 */
@Data
public class RolePermissionRes {
    private Long id;
    private Long roleId;
    private String roleName;
    private Long permissionId;
    private String permissionName;
    private Date createdAt;
}
