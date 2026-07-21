package com.yanque.commons.exception;


import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.CommonErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ApiResponse<Void> handleBusinessException(BusinessException e){
        log.error("业务异常捕获 , message = {}:",e.getMessage(), e);
        return ApiResponse.fail(e.getCode(),e.getMessage());
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
