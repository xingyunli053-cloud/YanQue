package com.yanque.modules.rbac.pojo.vo.reqvo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 新增或修改用户角色关联请求。 */
@Data
public class UserRoleSaveReq {
    /** 要关联角色的用户 ID。 */
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;
    /** 要分配给用户的角色 ID。 */
    @NotNull(message = "角色ID不能为空")
    @Min(value = 1, message = "角色ID必须大于0")
    private Long roleId;
}
