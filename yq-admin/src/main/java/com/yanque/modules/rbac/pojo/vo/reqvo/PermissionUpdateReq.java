package com.yanque.modules.rbac.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 修改权限请求，值为 null 的字段保持不变。 */
@Data
public class PermissionUpdateReq {
    /** 新父权限 ID；null 时保持原层级。 */
    @Min(value = 0, message = "父权限ID不能小于0")
    private Long parentId;
    /** 新权限编码；null 时不修改。 */
    @Size(min = 1, max = 100, message = "权限编码长度必须在1到100个字符之间")
    private String permissionCode;
    /** 新权限名称；null 时不修改。 */
    @Size(min = 1, max = 100, message = "权限名称长度必须在1到100个字符之间")
    private String permissionName;
    /** 新权限类型；null 时不修改。 */
    @EnumValue(enumClass = PermissionTypeEnum.class, message = "权限类型只能是API、MENU或BUTTON")
    private String permissionType;
    /** API 路径匹配规则；null 时不修改。 */
    @Size(max = 255, message = "API路径不能超过255个字符")
    private String apiPath;
    /** 新排序值；null 时不修改。 */
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortNum;
    /** 新权限描述；null 时不修改。 */
    @Size(max = 255, message = "权限描述不能超过255个字符")
    private String description;
    /** 新启用状态；null 时不修改。 */
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
