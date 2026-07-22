package com.yanque.modules.users.pojo.vo.resvo;

import lombok.Data;

import java.util.Date;

/**
 * 用户响应对象，不包含密码等敏感字段。
 */
@Data
public class UserRes {

    private Long id;
    private String username;
    private String nickname;
    private String realName;
    private String phone;
    private String email;
    private String unionId;
    private String status;
    private Date createdAt;
    private Date updatedAt;
}
