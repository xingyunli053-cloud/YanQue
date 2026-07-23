package com.yanque.modules.rbac.pojo.vo.reqvo;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

/** 用户角色关联条件分页请求。 */
@Data
public class UserRolePageReq {
    /** 当前页码，从 1 开始。 */
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    /** 每页记录数，最大 100。 */
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;
    /** 按用户 ID 精确筛选关联关系。 */
    @Min(value = 1, message = "用户ID必须大于0")
    private Long userId;
    /** 按角色 ID 精确筛选关联关系。 */
    @Min(value = 1, message = "角色ID必须大于0")
    private Long roleId;
}
