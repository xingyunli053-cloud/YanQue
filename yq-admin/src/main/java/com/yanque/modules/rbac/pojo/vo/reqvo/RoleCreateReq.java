package com.yanque.modules.rbac.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 新增角色请求。 */
@Data
public class RoleCreateReq {
    /** 全局唯一的角色编码。 */
    @NotBlank(message = "角色编码不能为空")
    @Size(max = 64, message = "角色编码不能超过64个字符")
    private String roleCode;
    /** 前端展示的角色名称。 */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 64, message = "角色名称不能超过64个字符")
    private String roleName;
    /** 角色职责的补充说明。 */
    @Size(max = 255, message = "角色描述不能超过255个字符")
    private String description;
    /** 角色启用状态，省略时使用默认状态。 */
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
