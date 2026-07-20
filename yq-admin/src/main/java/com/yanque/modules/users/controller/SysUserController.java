package com.yanque.modules.users.controller;

import com.yanque.common.response.ApiResponse;
import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.req.UserAddReq;
import com.yanque.modules.users.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "添加用户")
    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody UserAddReq req) {
        return ApiResponse.success(sysUserService.create(req));
    }
}
