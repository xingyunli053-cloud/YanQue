package com.yanque.modules.rbac.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 权限树条件查询请求。 */
@Data
public class PermissionTreeReq {
    /** 同时匹配权限编码和名称的关键字。 */
    @Size(max = 100, message = "权限关键字不能超过100个字符")
    private String keyword;
    /** 需要返回的权限类型。 */
    @EnumValue(enumClass = PermissionTypeEnum.class, message = "权限类型只能是API、MENU或BUTTON")
    private String permissionType;
    /** 需要返回的权限启用状态。 */
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
