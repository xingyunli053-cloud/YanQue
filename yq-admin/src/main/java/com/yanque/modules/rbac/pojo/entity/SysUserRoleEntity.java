package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 用户角色关联实体，对应 sys_user_role 表。 */
@Data
public class SysUserRoleEntity {
    private Long id;
    private Long userId;
    private Long roleId;
    private Date createdAt;
}
