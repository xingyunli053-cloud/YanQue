package com.yanque.common.response;

import lombok.Getter;

/**
 * @ClassName CommonCode
 * @Author mrzhang
 * @Date 2026/7/17
 * @Description 统一返回状态码的实现类.
 */
@Getter
public enum CommonErrorCode implements IErrorCode{
    SUCCESS(200,"操作成功")
    ,
    FAILED(500,"服务器开小差了,请稍后再试")
    ,
    UNAUTHORIZED(401,"未授权")
    ,
    NOT_FOUND(404,"访问的资源不存在"),
    PARAM_VALID_FAILED(40001, "请求参数校验失败"),

    USERNAME_OR_PASSWORD_NOT_NULL(10001, "用户名称或者是密码不能为空" ),
    USER_NOT_EXIST(10002,"用户名称和用户密码不正确" ),
    USER_NOT_ACTIVE(10003,  "用户是禁用状态" ),
    USER_DETAIL_NOT_FOUND(10004, "用户不存在"),
    USERNAME_ALREADY_EXISTS(10005, "用户名已存在"),
    USER_OPERATION_FAILED(10006, "用户操作失败"),

    TOKEN_NOT_FOUND(11001, "Token不能为空"),
    TOKEN_INVALID(11002, "Token不合法或登录状态已失效"),
    TOKEN_EXPIRED(11003, "Token已过期"),

    SIGN_HEADER_MISSING(12001, "请求签名参数不能为空"),
    SIGN_TIMESTAMP_INVALID(12002, "请求时间戳不合法"),
    SIGN_REQUEST_EXPIRED(12003, "请求签名已过期"),
    SIGN_NONCE_REPEATED(12004, "请求已执行，请勿重复提交"),
    SIGN_SECRET_NOT_FOUND(12005, "请求签名密钥不存在"),
    SIGN_INVALID(12006, "请求签名校验失败");


    private Integer code;
    private String message;


    //构造器.
    CommonErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }



}
