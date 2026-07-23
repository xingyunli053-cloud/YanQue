package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 用户角色关联实体，对应 sys_user_role 表。 */
@Data
public class SysUserRoleEntity {
    /** 关联记录唯一标识。 */ private Long id;
    /** 被分配角色的用户 ID。 */ private Long userId;
    /** 分配给用户的角色 ID。 */ private Long roleId;
    /** 关联建立时间。 */ private Date createdAt;
}
