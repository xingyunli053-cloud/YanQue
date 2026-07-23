package com.yanque.commons.interceptor;

import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.context.UserContext;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.rbac.service.RbacPermissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

/**
 * RBAC 权限拦截器。
 *
 * <p>仅校验 Controller 方法上的 API 类型 {@link PermissionMeta}。MENU、BUTTON 只用于前端
 * 权限树展示，不会阻断接口请求。JWT 和请求签名已在本拦截器之前完成校验。</p>
 */
@Component
@RequiredArgsConstructor
public class RbacInterceptor implements HandlerInterceptor {

    private final RbacPermissionService rbacPermissionService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 非 Controller 方法（如静态资源）不参与注解权限校验。
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        List<PermissionMeta> apiPermissions = Arrays.stream(
                        handlerMethod.getMethod().getAnnotationsByType(PermissionMeta.class))
                .filter(permission -> permission.type() == PermissionTypeEnum.API)
                .toList();

        // 未声明 API 权限的接口暂时放行，便于现有接口逐步接入 RBAC。
        if (apiPermissions.isEmpty()) {
            return true;
        }
        // 一个接口对应一个 API 权限，多个 API 元数据属于代码配置错误。
        if (apiPermissions.size() > 1) {
            throw new IllegalStateException("一个 Controller 方法只能声明一个 API 类型的 PermissionMeta");
        }

        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }

        String permissionCode = apiPermissions.get(0).value();
        // Redis 内部使用 SISMEMBER 判断 permission_code 是否属于当前用户。
        if (!rbacPermissionService.hasPermission(userId, permissionCode)) {
            throw BusinessException.of(CommonErrorCode.PERMISSION_DENIED);
        }
        return true;
    }
}
