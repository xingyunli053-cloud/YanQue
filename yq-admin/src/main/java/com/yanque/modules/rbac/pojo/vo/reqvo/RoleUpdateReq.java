package com.yanque.modules.rbac.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 修改角色请求，值为 null 的字段保持不变。 */
@Data
public class RoleUpdateReq {
    @Size(min = 1, max = 64, message = "角色编码长度必须在1到64个字符之间")
    private String roleCode;
    @Size(min = 1, max = 64, message = "角色名称长度必须在1到64个字符之间")
    private String roleName;
    @Size(max = 255, message = "角色描述不能超过255个字符")
    private String description;
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
