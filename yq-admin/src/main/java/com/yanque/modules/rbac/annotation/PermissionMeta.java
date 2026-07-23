package com.yanque.modules.rbac.annotation;

import com.yanque.modules.rbac.enums.PermissionTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 声明权限元数据。
 *
 * <p>MENU 与 BUTTON 用于权限树和前端显示控制；API 用于接口权限同步及 RBAC 拦截。
 * 一个 Controller 方法最多只能声明一个 API 类型的权限，但可声明多个 BUTTON 权限。</p>
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(PermissionMetas.class)
public @interface PermissionMeta {

    /** 权限编码，对应 sys_permission.permission_code，系统内必须唯一。 */
    String value();

    /** 权限名称，对应 sys_permission.permission_name。 */
    String name();

    /** 权限类型：API、MENU、BUTTON。 */
    PermissionTypeEnum type();

    /** 父权限编码；启动同步时解析为 sys_permission.parent_id。 */
    String parentCode() default "";

    /** 权限树排序值，越小越靠前。 */
    int sort() default 0;

    /** 权限说明。 */
    String description() default "";
}
