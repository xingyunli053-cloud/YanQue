package com.yanque.modules.rbac.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/** 用户角色关联响应。 */
@Data
public class UserRoleRes {
    private Long id;
    private Long userId;
    private String username;
    private Long roleId;
    private String roleName;
    private Date createdAt;
}
