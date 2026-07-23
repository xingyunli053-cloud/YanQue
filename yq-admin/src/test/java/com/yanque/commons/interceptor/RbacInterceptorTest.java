package com.yanque.commons.interceptor;

import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.context.UserContext;
import com.yanque.commons.exception.BusinessException;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.rbac.service.RbacPermissionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RbacInterceptorTest {

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void allowsAnnotatedApiWhenPermissionExistsInRedisSet() throws Exception {
        RbacPermissionService permissionService = mock(RbacPermissionService.class);
        RbacInterceptor interceptor = new RbacInterceptor(permissionService);
        UserContext.set(10L, "sign-secret", "session-001");
        when(permissionService.hasPermission(10L, "api:demo:read")).thenReturn(true);

        HandlerMethod handler = new HandlerMethod(new DemoController(),
                DemoController.class.getMethod("read"));

        assertTrue(interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handler));
        verify(permissionService).hasPermission(10L, "api:demo:read");
    }

    @Test
    void rejectsAnnotatedApiWhenPermissionIsAbsentFromRedisSet() throws Exception {
        RbacPermissionService permissionService = mock(RbacPermissionService.class);
        RbacInterceptor interceptor = new RbacInterceptor(permissionService);
        UserContext.set(10L, "sign-secret", "session-001");
        when(permissionService.hasPermission(10L, "api:demo:read")).thenReturn(false);

        HandlerMethod handler = new HandlerMethod(new DemoController(),
                DemoController.class.getMethod("read"));

        BusinessException exception = assertThrows(BusinessException.class,
                () -> interceptor.preHandle(new MockHttpServletRequest(), new MockHttpServletResponse(), handler));

        assertEquals(CommonErrorCode.PERMISSION_DENIED.getCode(), exception.getCode());
    }

    static class DemoController {
        @PermissionMeta(value = "api:demo:read", name = "读取演示数据", type = PermissionTypeEnum.API)
        public void read() {
        }
    }
}
