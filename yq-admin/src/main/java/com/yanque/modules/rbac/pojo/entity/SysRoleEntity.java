package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 系统角色实体，对应 sys_role 表。 */
@Data
public class SysRoleEntity {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
