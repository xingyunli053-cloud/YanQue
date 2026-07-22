package com.yanque.modules.rbac.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 角色条件分页请求。 */
@Data
public class RolePageReq {
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;
    @Size(max = 64, message = "角色编码不能超过64个字符")
    private String roleCode;
    @Size(max = 64, message = "角色名称不能超过64个字符")
    private String roleName;
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
