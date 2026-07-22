package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 权限响应。 */
@Data
public class PermissionRes {
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
