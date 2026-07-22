package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionCreateReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionTreeReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionUpdateReq;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionRes;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionTreeRes;
import com.yanque.modules.rbac.service.SysPermissionService;
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

@Tag(name = "权限管理")
@RestController
@RequestMapping("/api/sysPermission")
@RequiredArgsConstructor
public class SysPermissionController {
    private final SysPermissionService permissionService;

    @Operation(summary = "分页查询权限")
    @GetMapping
    public ApiResponse<PageResult<PermissionRes>> page(@Valid PermissionPageReq req) {
        return ApiResponse.success(permissionService.page(req));
    }

    @Operation(summary = "查询权限树")
    @GetMapping("/tree")
    public ApiResponse<List<PermissionTreeRes>> tree(@Valid PermissionTreeReq req) {
        return ApiResponse.success(permissionService.tree(req));
    }

    @Operation(summary = "查询权限详情")
    @GetMapping("/{id}")
    public ApiResponse<PermissionRes> detail(@PathVariable Long id) {
        return ApiResponse.success(permissionService.detail(id));
    }

    @Operation(summary = "新增权限")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody PermissionCreateReq req) {
        return ApiResponse.success(permissionService.create(req));
    }

    @Operation(summary = "修改权限")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody PermissionUpdateReq req) {
        permissionService.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.success();
    }
}
