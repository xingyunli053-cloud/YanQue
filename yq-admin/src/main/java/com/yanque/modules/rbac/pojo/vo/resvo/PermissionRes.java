package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 权限响应。 */
@Data
public class PermissionRes {
    /** 权限唯一标识。 */ private Long id;
    /** 父权限 ID，根节点为 0。 */ private Long parentId;
    /** 系统内唯一的权限编码。 */ private String permissionCode;
    /** 前端展示的权限名称。 */ private String permissionName;
    /** 权限类型：API、MENU 或 BUTTON。 */ private String permissionType;
    /** API 权限对应的路径匹配规则。 */ private String apiPath;
    /** 同级节点排序值。 */ private Integer sortNum;
    /** 权限业务说明。 */ private String description;
    /** 启用状态：ACTIVE 或 INACTIVE。 */ private String status;
    /** 权限创建时间。 */ private Date createdAt;
    /** 权限最后更新时间。 */ private Date updatedAt;
}
