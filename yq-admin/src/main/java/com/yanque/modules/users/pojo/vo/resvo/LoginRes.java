package com.yanque.modules.users.pojo.vo.resvo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LoginRes
 * @Author mrzhang
 * @Date 2026/7/17
 * @Description 登录成功之后的响应实体.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRes {

    /** JWT token，后续请求放在 Authorization 请求头中 */
    private String token;

    /** 请求签名密钥，前端用它生成 X-Sign 随机字符串. */
    private String signSecret;

}
