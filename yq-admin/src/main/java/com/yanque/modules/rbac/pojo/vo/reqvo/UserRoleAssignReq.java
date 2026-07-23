package com.yanque.modules.rbac.pojo.vo.reqvo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/** 为用户全量分配角色，空集合表示清空用户角色。 */
@Data
public class UserRoleAssignReq {
    /** 要全量分配给用户的角色 ID 集合；空集合表示清空。 */
    @NotNull(message = "角色ID集合不能为空")
    private List<@NotNull(message = "角色ID不能为空") Long> roleIds;
}
