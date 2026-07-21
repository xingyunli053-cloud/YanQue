package com.yanque.commons.exception;

import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.apires.IErrorCode;
import lombok.Data;
import lombok.Getter;

@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;


    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());

        this.code = errorCode.getCode();
    }

    public BusinessException(IErrorCode errorCode,String message) {
        super(message);

        this.code = errorCode.getCode();
    }
    public BusinessException(Integer errorCode ,String message) {
        super(message);

        this.code = errorCode;
    }

    public static  BusinessException of(IErrorCode errorCode){
        return new BusinessException(errorCode);
    }
    public static  BusinessException of(IErrorCode errorCode,String message){
        return new BusinessException(errorCode,message);
    }

    public static  BusinessException of(Integer errorCode,String message){
        return new BusinessException(errorCode,message);
    }
}
