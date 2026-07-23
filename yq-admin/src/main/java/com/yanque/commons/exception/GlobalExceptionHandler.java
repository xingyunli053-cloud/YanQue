package com.yanque.commons.exception;


import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @ClassName GlobalExceptionHandler
 * @Author mrzhang
 * @Date 2026/7/17
 * @Description 全局异常异常处理器.
 */

@RestControllerAdvice
@Slf4j // 日志
public class GlobalExceptionHandler {

    /*
     * 处理业务异常, 捕获的是自定义异常信息.
    */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e){
        log.error("业务异常捕获 , message = {}:",e.getMessage(), e);
        ApiResponse<Void> response = ApiResponse.fail(e.getCode(), e.getMessage());

        // token 与签名密钥相关异常统一返回 HTTP 401。
        if (isUnauthorized(e.getCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        if (CommonErrorCode.SIGN_NONCE_REPEATED.getCode().equals(e.getCode())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        if (CommonErrorCode.PERMISSION_DENIED.getCode().equals(e.getCode())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        return ResponseEntity.ok(response);
    }

    private boolean isUnauthorized(Integer code) {
        return CommonErrorCode.UNAUTHORIZED.getCode().equals(code)
                || CommonErrorCode.TOKEN_NOT_FOUND.getCode().equals(code)
                || CommonErrorCode.TOKEN_INVALID.getCode().equals(code)
                || CommonErrorCode.TOKEN_EXPIRED.getCode().equals(code)
                || CommonErrorCode.SIGN_HEADER_MISSING.getCode().equals(code)
                || CommonErrorCode.SIGN_TIMESTAMP_INVALID.getCode().equals(code)
                || CommonErrorCode.SIGN_REQUEST_EXPIRED.getCode().equals(code)
                || CommonErrorCode.SIGN_SECRET_NOT_FOUND.getCode().equals(code)
                || CommonErrorCode.SIGN_INVALID.getCode().equals(code);
    }

    /**
     * 统一处理请求体和查询参数的 Bean Validation 校验异常。
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(BindException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError == null
                ? CommonErrorCode.PARAM_VALID_FAILED.getMessage()
                : fieldError.getDefaultMessage();
        return ResponseEntity.badRequest().body(
                ApiResponse.fail(CommonErrorCode.PARAM_VALID_FAILED, message));
    }


    /*
     * 处理系统异常, 捕获的是系统异常信息.
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception e){
        log.error("系统异常捕获 , message = {}:",e.getMessage(), e);
        return ApiResponse.fail(CommonErrorCode.FAILED.getCode(),CommonErrorCode.FAILED.getMessage());
    }





}
