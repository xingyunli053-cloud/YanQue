package com.yanque.modules.rbac.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.annotation.PermissionMetas;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
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
@PermissionMeta(value = "system:permission", name = "权限管理", type = PermissionTypeEnum.MENU,
        parentCode = "system", sort = 1030)
public class SysPermissionController {
    private final SysPermissionService permissionService;

    @Operation(summary = "分页查询权限")
    @GetMapping
    @PermissionMeta(value = "api:permission:page", name = "分页查询权限", type = PermissionTypeEnum.API,
            parentCode = "system:permission", sort = 1131)
    public ApiResponse<PageResult<PermissionRes>> page(@Valid PermissionPageReq req) {
        return ApiResponse.success(permissionService.page(req));
    }

    @Operation(summary = "查询权限树")
    @GetMapping("/tree")
    @PermissionMeta(value = "api:permission:tree", name = "查询权限树", type = PermissionTypeEnum.API,
            parentCode = "system:permission", sort = 1132)
    public ApiResponse<List<PermissionTreeRes>> tree(@Valid PermissionTreeReq req) {
        return ApiResponse.success(permissionService.tree(req));
    }

    @Operation(summary = "查询权限详情")
    @GetMapping("/{id}")
    @PermissionMeta(value = "api:permission:detail", name = "查询权限详情", type = PermissionTypeEnum.API,
            parentCode = "system:permission", sort = 1133)
    public ApiResponse<PermissionRes> detail(@PathVariable Long id) {
        return ApiResponse.success(permissionService.detail(id));
    }

    @Operation(summary = "新增权限")
    @PostMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:permission:create", name = "新增权限接口", type = PermissionTypeEnum.API,
                    parentCode = "system:permission", sort = 1134),
            @PermissionMeta(value = "system:permission:create", name = "新增权限", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:permission", sort = 1031)
    })
    public ApiResponse<Long> create(@Valid @RequestBody PermissionCreateReq req) {
        return ApiResponse.success(permissionService.create(req));
    }

    @Operation(summary = "修改权限")
    @PutMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:permission:update", name = "修改权限接口", type = PermissionTypeEnum.API,
                    parentCode = "system:permission", sort = 1135),
            @PermissionMeta(value = "system:permission:update", name = "修改权限", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:permission", sort = 1032)
    })
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody PermissionUpdateReq req) {
        permissionService.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:permission:delete", name = "删除权限接口", type = PermissionTypeEnum.API,
                    parentCode = "system:permission", sort = 1136),
            @PermissionMeta(value = "system:permission:delete", name = "删除权限", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:permission", sort = 1033)
    })
    public ApiResponse<Void> delete(@PathVariable Long id) {
        permissionService.delete(id);
        return ApiResponse.success();
    }
}
