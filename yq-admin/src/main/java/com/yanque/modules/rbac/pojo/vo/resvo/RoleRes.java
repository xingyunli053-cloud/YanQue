package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/** 角色响应；详情接口会携带已分配的权限ID。 */
@Data
public class RoleRes {
    private Long id;
    private String roleCode;
    private String roleName;
    private String description;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private List<Long> permissionIds;
}
