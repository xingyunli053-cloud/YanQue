package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRolePageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRoleAssignReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRoleSaveReq;
import com.yanque.modules.rbac.pojo.vo.resvo.UserRoleRes;
import com.yanque.modules.rbac.service.SysUserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户角色关联管理")
@RestController
@RequestMapping("/api/sysUserRole")
@RequiredArgsConstructor
public class SysUserRoleController {
    private final SysUserRoleService service;

    @Operation(summary = "分页查询用户角色关联")
    @GetMapping
    @PermissionMeta(value = "api:user-role:page", name = "分页查询用户角色关联", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1141)
    public ApiResponse<PageResult<UserRoleRes>> page(@Valid UserRolePageReq req) {
        return ApiResponse.success(service.page(req));
    }

    @Operation(summary = "查询用户角色关联详情")
    @GetMapping("/{id}")
    @PermissionMeta(value = "api:user-role:detail", name = "查询用户角色关联详情", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1142)
    public ApiResponse<UserRoleRes> detail(@PathVariable Long id) {
        return ApiResponse.success(service.detail(id));
    }

    @Operation(summary = "新增用户角色关联")
    @PostMapping
    @PermissionMeta(value = "api:user-role:create", name = "新增用户角色关联", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1143)
    public ApiResponse<Long> create(@Valid @RequestBody UserRoleSaveReq req) {
        return ApiResponse.success(service.create(req));
    }

    @Operation(summary = "修改用户角色关联")
    @PutMapping("/{id}")
    @PermissionMeta(value = "api:user-role:update", name = "修改用户角色关联", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1144)
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody UserRoleSaveReq req) {
        service.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除用户角色关联")
    @DeleteMapping("/{id}")
    @PermissionMeta(value = "api:user-role:delete", name = "删除用户角色关联", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1145)
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "查询用户已分配的角色ID")
    @GetMapping("/user/{userId}/roles")
    @PermissionMeta(value = "api:user-role:list", name = "查询用户已分配角色", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1146)
    public ApiResponse<List<Long>> getUserRoles(@PathVariable Long userId) {
        return ApiResponse.success(service.getRoleIdsByUserId(userId));
    }

    @Operation(summary = "为用户批量分配角色")
    @PutMapping("/user/{userId}/roles")
    @PermissionMeta(value = "api:user-role:assign", name = "为用户分配角色", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1147)
    public ApiResponse<Void> assignUserRoles(@PathVariable Long userId,
                                             @Valid @RequestBody UserRoleAssignReq req) {
        service.assignRoles(userId, req.getRoleIds());
        return ApiResponse.success();
    }
}
