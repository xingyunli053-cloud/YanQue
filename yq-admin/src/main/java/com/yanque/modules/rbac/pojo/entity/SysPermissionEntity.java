package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 系统权限实体，对应 sys_permission 表。 */
@Data
public class SysPermissionEntity {
    private Long id;
    private Long parentId;
    private String permissionCode;
    private String permissionName;
    private String permissionType;
    private String apiPath;
    private Integer sortNum;
    private String description;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
