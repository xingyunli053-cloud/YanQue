package com.yanque.modules.users.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.PageResult;
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
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResponse<LoginRes> login(@Valid @RequestBody LoginReq  req) {
        return ApiResponse.success(sysUserService.login(req));
    }

    @Operation(summary = "用户分页")
    @GetMapping
    public ApiResponse<PageResult<UserRes>> page(@Valid UserPageReq req) {
        return ApiResponse.success(sysUserService.page(req));
    }

    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public ApiResponse<UserRes> detail(@PathVariable Long id) {
        return ApiResponse.success(sysUserService.detail(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody UserCreateReq req) {
        return ApiResponse.success(sysUserService.create(req));
    }

    @Operation(summary = "修改用户")
    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id,
                                    @Valid @RequestBody UserUpdateReq req) {
        sysUserService.update(id, req);
        return ApiResponse.success();
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return ApiResponse.success();
    }
}
