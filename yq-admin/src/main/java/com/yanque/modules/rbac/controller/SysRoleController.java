package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
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
public class SysRoleController {
    private final SysRoleService roleService;

    @Operation(summary = "分页查询角色")
    @GetMapping
    public ApiResponse<PageResult<RoleRes>> page(@Valid RolePageReq req) {
        return ApiResponse.success(roleService.page(req));
    }

    @Operation(summary = "查询角色详情")
    @GetMapping("/{id}")
    public ApiResponse<RoleRes> detail(@PathVariable Long id) {
        return ApiResponse.success(roleService.detail(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody RoleCreateReq req) {
        return ApiResponse.success(roleService.create(req));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody RoleUpdateReq req) {
        roleService.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        roleService.delete(id);
        return ApiResponse.success();
    }

    @Operation(summary = "角色分配权限")
    @PutMapping("/{id}/permissions")
    public ApiResponse<Void> assignPermissions(@PathVariable Long id,
                                               @Valid @RequestBody RolePermissionAssignReq req) {
        roleService.assignPermissions(id, req.getPermissionIds());
        return ApiResponse.success();
    }
}
