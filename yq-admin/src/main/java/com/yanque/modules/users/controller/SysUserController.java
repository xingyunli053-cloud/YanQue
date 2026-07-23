package com.yanque.modules.users.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.context.UserContext;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.annotation.PermissionMetas;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.users.pojo.vo.reqvo.LoginReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserCreateReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserPageReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserUpdateReq;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
import com.yanque.modules.users.pojo.vo.resvo.UserRes;
import com.yanque.modules.users.service.SysUserService;
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

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/sysUser")
@RequiredArgsConstructor
@PermissionMetas({
        @PermissionMeta(value = "system", name = "系统管理", type = PermissionTypeEnum.MENU, sort = 10),
        @PermissionMeta(value = "system:user", name = "用户管理", type = PermissionTypeEnum.MENU,
                parentCode = "system", sort = 1010)
})
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<LoginRes> login(@Valid @RequestBody LoginReq  req) {
        return ApiResponse.success(sysUserService.login(req));
    }

    @Operation(summary = "用户退出登录")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        sysUserService.logout(UserContext.getUserId(), UserContext.getSessionId());
        return ApiResponse.success();
    }

    @Operation(summary = "用户分页")
    @GetMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:user:page", name = "分页查询用户", type = PermissionTypeEnum.API,
                    parentCode = "system:user", sort = 1111),
            @PermissionMeta(value = "system:user:query", name = "查询用户", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:user", sort = 1010)
    })
    public ApiResponse<PageResult<UserRes>> page(@Valid UserPageReq req) {
        return ApiResponse.success(sysUserService.page(req));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    @PermissionMeta(value = "api:user:detail", name = "查询用户详情", type = PermissionTypeEnum.API,
            parentCode = "system:user", sort = 1112)
    public ApiResponse<UserRes> detail(@PathVariable Long id) {
        return ApiResponse.success(sysUserService.detail(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @PermissionMetas({
            @PermissionMeta(value = "api:user:create", name = "新增用户接口", type = PermissionTypeEnum.API,
                    parentCode = "system:user", sort = 1113),
            @PermissionMeta(value = "system:user:create", name = "新增用户", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:user", sort = 1011)
    })
    public ApiResponse<Long> create(@Valid @RequestBody UserCreateReq req) {
        return ApiResponse.success(sysUserService.create(req));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:user:update", name = "修改用户接口", type = PermissionTypeEnum.API,
                    parentCode = "system:user", sort = 1114),
            @PermissionMeta(value = "system:user:update", name = "修改用户", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:user", sort = 1012)
    })
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody UserUpdateReq req) {
        sysUserService.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PermissionMetas({
            @PermissionMeta(value = "api:user:delete", name = "删除用户接口", type = PermissionTypeEnum.API,
                    parentCode = "system:user", sort = 1115),
            @PermissionMeta(value = "system:user:delete", name = "删除用户", type = PermissionTypeEnum.BUTTON,
                    parentCode = "system:user", sort = 1013)
    })
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return ApiResponse.success();
    }
}
