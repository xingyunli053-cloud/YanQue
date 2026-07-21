package com.yanque.modules.users.controller;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.modules.users.pojo.vo.reqvo.LoginReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserCreateReq;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
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

    @Operation(summary = "用户登录")
    @PostMapping
    public ApiResponse<LoginRes> login(@Valid @RequestBody LoginReq  req) {
        return ApiResponse.success(sysUserService.login(req));
    }


    //@Operation(summary = "添加用户")
    //@PostMapping
    //public ApiResponse<Long> create(@Valid @RequestBody UserCreateReq req) {
    //    return ApiResponse.success(sysUserService.create(req));
    //}
}
