package com.yanque.modules.rbac.pojo.entity;

import lombok.Data;

import java.util.Date;

/** 系统权限实体，对应 sys_permission 表。 */
@Data
public class SysPermissionEntity {
    /** 权限唯一标识。 */ private Long id;
    /** 父权限 ID，根节点为 0。 */ private Long parentId;
    /** 系统内唯一的权限编码。 */ private String permissionCode;
    /** 前端展示的权限名称。 */ private String permissionName;
    /** 权限类型：API、MENU 或 BUTTON。 */ private String permissionType;
    /** API 权限对应的“请求方法 路径”规则。 */ private String apiPath;
    /** 同级权限排序值，数值越小越靠前。 */ private Integer sortNum;
    /** 权限的补充业务说明。 */ private String description;
    /** 启用状态：ACTIVE 或 INACTIVE。 */ private String status;
    /** 权限记录创建时间。 */ private Date createdAt;
    /** 权限记录最后更新时间。 */ private Date updatedAt;
}
