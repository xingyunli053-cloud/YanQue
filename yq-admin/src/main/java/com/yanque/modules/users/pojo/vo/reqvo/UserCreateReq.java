package com.yanque.modules.users.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateReq {

    /** 系统内唯一的登录用户名。 */
    @NotBlank(message = "用户名不能为空")
    @Size(max = 64, message = "用户名长度不能超过64个字符")
    private String username;

    /** 新账号的登录密码，长度为 6 至 64 位。 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度必须在6到64个字符之间")
    private String password;

    /** 用户在系统中的展示昵称。 */
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    /** 用户真实姓名。 */
    @Size(max = 64, message = "真实姓名长度不能超过64个字符")
    private String realName;

    /** 用户联系电话。 */
    @Size(max = 32, message = "手机号长度不能超过32个字符")
    private String phone;

    /** 用户联系邮箱。 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    /** 飞书账号的唯一标识。 */
    @Size(max = 128, message = "unionId长度不能超过128个字符")
    private String unionId;

    /** 账号启用状态；省略时由服务端使用默认值。 */
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
