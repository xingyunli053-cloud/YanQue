package com.yanque.modules.rbac.pojo.vo.reqvo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 新增或修改用户角色关联请求。 */
@Data
public class UserRoleSaveReq {
    @NotNull(message = "用户ID不能为空")
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;
    @NotNull(message = "角色ID不能为空")
    @Min(value = 1, message = "角色ID必须大于0")
    private Long roleId;
}
