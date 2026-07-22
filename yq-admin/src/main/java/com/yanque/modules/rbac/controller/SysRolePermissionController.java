package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionSaveReq;
import com.yanque.modules.rbac.pojo.vo.resvo.RolePermissionRes;
import com.yanque.modules.rbac.service.SysRolePermissionService;
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

@Tag(name = "角色权限关联管理")
@RestController
@RequestMapping("/api/sysRolePermission")
@RequiredArgsConstructor
public class SysRolePermissionController {
    private final SysRolePermissionService service;

    @Operation(summary = "分页查询角色权限关联")
    @GetMapping
    public ApiResponse<PageResult<RolePermissionRes>> page(@Valid RolePermissionPageReq req) {
        return ApiResponse.success(service.page(req));
    }

    @Operation(summary = "查询角色权限关联详情")
    @GetMapping("/{id}")
    public ApiResponse<RolePermissionRes> detail(@PathVariable Long id) {
        return ApiResponse.success(service.detail(id));
    }

    @Operation(summary = "新增角色权限关联")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody RolePermissionSaveReq req) {
        return ApiResponse.success(service.create(req));
    }

    @Operation(summary = "修改角色权限关联")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody RolePermissionSaveReq req) {
        service.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除角色权限关联")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.success();
    }
}
