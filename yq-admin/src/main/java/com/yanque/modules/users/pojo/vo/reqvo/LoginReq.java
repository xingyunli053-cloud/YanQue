package com.yanque.modules.users.pojo.vo.reqvo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @ClassName LoginReq
 * @Author mrzhang
 * @Date 2026/7/17
 * @Description 登录的请求对象.
 */
@Data
public class LoginReq {
    /** 用于身份认证的登录用户名。 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 与用户名对应的登录密码。 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
