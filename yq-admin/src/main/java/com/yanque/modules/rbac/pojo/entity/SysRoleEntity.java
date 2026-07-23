package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 系统角色实体，对应 sys_role 表。 */
@Data
public class SysRoleEntity {
    /** 角色唯一标识。 */ private Long id;
    /** 系统内唯一的角色编码，例如 SUPER_ADMIN。 */ private String roleCode;
    /** 前端展示的角色名称。 */ private String roleName;
    /** 角色职责说明。 */ private String description;
    /** 启用状态：ACTIVE 或 INACTIVE。 */ private String status;
    /** 角色创建时间。 */ private Date createdAt;
    /** 角色最后更新时间。 */ private Date updatedAt;
}
