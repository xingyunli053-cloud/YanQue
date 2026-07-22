package com.yanque.modules.rbac.pojo.vo.reqvo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** 新增或修改角色权限关联请求。 */
@Data
public class RolePermissionSaveReq {
    @NotNull(message = "角色ID不能为空")
    @Min(value = 1, message = "角色ID必须大于0")
    private Long roleId;
    @NotNull(message = "权限ID不能为空")
    @Min(value = 1, message = "权限ID必须大于0")
    private Long permissionId;
}
