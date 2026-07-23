package com.yanque.modules.rbac.pojo.vo.reqvo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 为角色全量分配权限，空集合表示清空角色权限。 */
@Data
public class RolePermissionAssignReq {
    /** 要全量授予角色的权限 ID 集合；空集合表示清空。 */
    @NotNull(message = "权限ID集合不能为空")
    private List<@NotNull(message = "权限ID不能为空") Long> permissionIds;
}
