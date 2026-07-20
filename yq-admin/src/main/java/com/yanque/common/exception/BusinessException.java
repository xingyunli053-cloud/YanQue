package com.yanque.common.exception;

import com.yanque.common.response.IErrorCode;
import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final Integer code;

    public static final BusinessException UserExist = new BusinessException(10001, "用户已存在");
    public static final BusinessException UserNotExist = new BusinessException(10002, "用户不存在");
    public static final BusinessException PermissionExist = new BusinessException(11001, "权限已存在");
    public static final BusinessException PermissionNotExist = new BusinessException(11002, "权限不存在");
    public static final BusinessException ParamsError = new BusinessException(11005, "参数异常");
    public static final BusinessException PasswordError = new BusinessException(11003, "密码错误");
    public static final BusinessException DateExist = new BusinessException(11006, "数据已存在");
    public static final BusinessException DateError = new BusinessException(11004, "数据错误");
    public static final BusinessException RoleExist = new BusinessException(12001, "角色已存在");
    public static final BusinessException RoleNotExist = new BusinessException(12002, "角色不存在");
    public static final BusinessException ConfigExist = new BusinessException(13001, "配置已存在");
    public static final BusinessException ConfigNotExist = new BusinessException(13002, "配置不存在");
    public static final BusinessException CampusNotExist = new BusinessException(14001, "校区不存在");
    public static final BusinessException CourseNotExist = new BusinessException(15001, "课程不存在");
    public static final BusinessException CourseDetailNotExist = new BusinessException(15002, "课程详情不存在");
    public static final BusinessException ClazzNotExist = new BusinessException(16001, "班级不存在");
    public static final BusinessException ProductNotExist = new BusinessException(17001, "产品不存在");
    public static final BusinessException PrepayOrderNotExist = new BusinessException(17002, "预支付订单不存在");
    public static final BusinessException RemoteError = new BusinessException(17003, "远程调用异常");

    public BusinessException(String message) {
        super(message);
        this.code = 400;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(IErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException newInstance(String message) {
        return new BusinessException(this.getCode(), message);
    }

    /**
     * 提供工具类方法, 便于直接快速的获取BusinessException
     * @param errorCode
     *
     */
    public static BusinessException of(IErrorCode errorCode){
        return new BusinessException(errorCode);
    }

    public static BusinessException of(Integer code,String message){
        return new BusinessException(code,message);
    }

    public static BusinessException of(IErrorCode errorCode, String message){
        return new BusinessException(errorCode,message);
    }
}
