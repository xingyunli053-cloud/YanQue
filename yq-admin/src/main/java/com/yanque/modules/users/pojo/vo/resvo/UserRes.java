package com.yanque.modules.users.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/**
 * 用户响应对象，不包含密码等敏感字段。
 */
@Data
public class UserRes {

    /** 用户唯一标识。 */ private Long id;
    /** 登录用户名。 */ private String username;
    /** 系统展示昵称。 */ private String nickname;
    /** 用户真实姓名。 */ private String realName;
    /** 用户联系电话。 */ private String phone;
    /** 用户联系邮箱。 */ private String email;
    /** 飞书账号唯一标识。 */ private String unionId;
    /** 账号启用状态：ACTIVE 或 INACTIVE。 */ private String status;
    /** 用户创建时间。 */ private Date createdAt;
    /** 用户最后更新时间。 */ private Date updatedAt;
}
