package com.yanque.common.config;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.UUID;

@Slf4j
@Aspect
@Component
public class RequestLogAspect {

    private static final String TRACE_ID = "traceId";

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerPointcut() {
    }

    @Before("controllerPointcut()")
    public void beforeRequest(JoinPoint joinPoint) {
        // 设置 traceId
        MDC.put(TRACE_ID, UUID.randomUUID().toString().replace("-", ""));

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        log.info("===== 请求开始 =====");
        log.info("请求路径: {} {}", request.getMethod(), request.getRequestURI());
        log.info("请求方法: {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        String queryString = request.getQueryString();
        if (StrUtil.isNotBlank(queryString)) {
            log.info("查询参数: {}", queryString);
        }
        Object[] args = joinPoint.getArgs();
        if (args.length > 0) {
            log.info("请求参数: {}", Arrays.toString(args));
        }
    }

    @AfterReturning(pointcut = "controllerPointcut()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        log.info("响应结果: {}", result);
        log.info("===== 请求结束 =====");
        MDC.clear();
    }
}
