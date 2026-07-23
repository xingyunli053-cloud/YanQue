package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/** 角色响应；详情接口会携带已分配的权限ID。 */
@Data
public class RoleRes {
    /** 角色唯一标识。 */ private Long id;
    /** 系统内唯一的角色编码。 */ private String roleCode;
    /** 前端展示的角色名称。 */ private String roleName;
    /** 角色职责说明。 */ private String description;
    /** 启用状态：ACTIVE 或 INACTIVE。 */ private String status;
    /** 角色创建时间。 */ private Date createdAt;
    /** 角色最后更新时间。 */ private Date updatedAt;
    /** 当前角色已拥有的权限 ID 集合。 */private List<Long> permissionIds;
}
