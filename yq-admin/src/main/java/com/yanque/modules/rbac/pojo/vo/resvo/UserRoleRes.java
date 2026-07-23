package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 用户角色关联响应。 */
@Data
public class UserRoleRes {
    /** 用户角色关联记录 ID。 */ private Long id;
    /** 被分配角色的用户 ID。 */ private Long userId;
    /** 被分配角色的登录用户名。 */ private String username;
    /** 分配给用户的角色 ID。 */ private Long roleId;
    /** 分配给用户的角色名称。 */ private String roleName;
    /** 分配关系创建时间。 */ private Date createdAt;
}
