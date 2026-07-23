package com.yanque.modules.rbac.pojo.vo.reqvo;

import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.validation.EnumValue;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** 新增权限请求。 */
@Data
public class PermissionCreateReq {
    /** 父权限 ID；0 表示根权限。 */
    @Min(value = 0, message = "父权限ID不能小于0")
    private Long parentId = 0L;
    /** 全局唯一的权限编码。 */
    @NotBlank(message = "权限编码不能为空")
    @Size(max = 100, message = "权限编码不能超过100个字符")
    private String permissionCode;
    /** 前端显示的权限名称。 */
    @NotBlank(message = "权限名称不能为空")
    @Size(max = 100, message = "权限名称不能超过100个字符")
    private String permissionName;
    /** 权限类型：API、MENU 或 BUTTON。 */
    @NotBlank(message = "权限类型不能为空")
    @EnumValue(enumClass = PermissionTypeEnum.class, message = "权限类型只能是API、MENU或BUTTON")
    private String permissionType;
    /** API 类型权限的路径匹配规则，其他类型为空。 */
    @Size(max = 255, message = "API路径不能超过255个字符")
    private String apiPath;
    /** 同级节点排序值，越小越靠前。 */
    @NotNull(message = "排序值不能为空")
    @Min(value = 0, message = "排序值不能小于0")
    private Integer sortNum = 0;
    /** 权限的业务说明。 */
    @Size(max = 255, message = "权限描述不能超过255个字符")
    private String description;
    /** 权限启用状态；省略时由服务端使用默认值。 */
    @EnumValue(enumClass = CommonStatusEnum.class, message = "状态只能是ACTIVE或INACTIVE")
    private String status;
}
