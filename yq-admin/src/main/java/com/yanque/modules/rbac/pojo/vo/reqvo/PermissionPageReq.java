package com.yanque.modules.rbac.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 权限条件分页请求。 */
@Data
public class PermissionPageReq {
    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;
    @Min(value = 1, message = "每页数量不能小于1")
    @Max(value = 100, message = "每页数量不能超过100")
    private Integer pageSize = 10;
    @Min(value = 0, message = "父权限ID不能小于0")
    private Long parentId;
    @Size(max = 100, message = "权限编码不能超过100个字符")
    private String permissionCode;
    @Size(max = 100, message = "权限名称不能超过100个字符")
    private String permissionName;
    @EnumValue(enumClass = PermissionTypeEnum.class, message = "权限类型只能是API、MENU或BUTTON")
    private String permissionType;
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
