package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.annotation.PermissionMetas;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.rbac.pojo.vo.reqvo.RoleCreateReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionAssignReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RoleUpdateReq;
import com.yanque.modules.rbac.pojo.vo.resvo.RoleRes;
import com.yanque.modules.rbac.service.SysRoleService;
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

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/sysRole")
@RequiredArgsConstructor
@PermissionMeta(value = "system:role", name = "角色管理", type = PermissionTypeEnum.MENU,
        parentCode = "system", sort = 1020)
public class SysRoleController {
    private final SysRoleService roleService;

    @Operation(summary = "分页查询角色")
    @GetMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:role:page", name = "分页查询角色", type = PermissionTypeEnum.API,
                    parentCode = "system:role", sort = 1121),
            @PermissionMeta(value = "system:role:query", name = "查询角色", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:role", sort = 1020)
    })
    public ApiResponse<PageResult<RoleRes>> page(@Valid RolePageReq req) {
        return ApiResponse.success(roleService.page(req));
    }

    @Operation(summary = "查询角色详情")
    @GetMapping("/{id}")
    @PermissionMeta(value = "api:role:detail", name = "查询角色详情", type = PermissionTypeEnum.API,
            parentCode = "system:role", sort = 1122)
    public ApiResponse<RoleRes> detail(@PathVariable Long id) {
        return ApiResponse.success(roleService.detail(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:role:create", name = "新增角色接口", type = PermissionTypeEnum.API,
                    parentCode = "system:role", sort = 1123),
            @PermissionMeta(value = "system:role:create", name = "新增角色", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:role", sort = 1021)
    })
    public ApiResponse<Long> create(@Valid @RequestBody RoleCreateReq req) {
        return ApiResponse.success(roleService.create(req));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:role:update", name = "修改角色接口", type = PermissionTypeEnum.API,
                    parentCode = "system:role", sort = 1124),
            @PermissionMeta(value = "system:role:update", name = "修改角色", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:role", sort = 1022)
    })
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody RoleUpdateReq req) {
        roleService.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:role:delete", name = "删除角色接口", type = PermissionTypeEnum.API,
                    parentCode = "system:role", sort = 1125),
            @PermissionMeta(value = "system:role:delete", name = "删除角色", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:role", sort = 1023)
    })
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "角色分配权限")
    @PutMapping("/{id}/permissions")
    @PermissionMetas({
            @PermissionMeta(value = "api:role:assign-permissions", name = "角色分配权限接口", type = PermissionTypeEnum.API,
                    parentCode = "system:role", sort = 1126),
            @PermissionMeta(value = "system:role:assign-permissions", name = "分配角色权限", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:role", sort = 1024)
    })
    public ApiResponse<Void> assignPermissions(@PathVariable Long id,
                                               @Valid @RequestBody RolePermissionAssignReq req) {
        roleService.assignPermissions(id, req.getPermissionIds());
        return ApiResponse.success();
    }
}
