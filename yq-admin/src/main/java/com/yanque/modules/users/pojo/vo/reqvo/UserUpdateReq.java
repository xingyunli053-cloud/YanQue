package com.yanque.modules.users.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改用户请求。值为 null 的字段保持不变。
 */
@Data
public class UserUpdateReq {

    /** 新登录用户名；null 时不修改。 */
    @Size(min = 1, max = 64, message = "用户名长度必须在1到64个字符之间")
    private String username;

    /** 新登录密码；null 时不修改。 */
    @Size(min = 6, max = 64, message = "密码长度必须在6到64个字符之间")
    private String password;

    /** 新展示昵称；null 时不修改。 */
    @Size(max = 64, message = "昵称长度不能超过64个字符")
    private String nickname;

    /** 新真实姓名；null 时不修改。 */
    @Size(max = 64, message = "真实姓名长度不能超过64个字符")
    private String realName;

    /** 新联系电话；null 时不修改。 */
    @Size(max = 32, message = "手机号长度不能超过32个字符")
    private String phone;

    /** 新联系邮箱；null 时不修改。 */
    @Email(message = "邮箱格式不正确")
    @Size(max = 128, message = "邮箱长度不能超过128个字符")
    private String email;

    /** 新飞书账号唯一标识；null 时不修改。 */
    @Size(max = 128, message = "unionId长度不能超过128个字符")
    private String unionId;

    /** 新账号启用状态；null 时不修改。 */
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
