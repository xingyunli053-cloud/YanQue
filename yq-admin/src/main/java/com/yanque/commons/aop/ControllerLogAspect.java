package com.yanque.commons.aop;

import com.alibaba.fastjson2.JSON;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Around("execution(* com.yanque..controller..*.*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getDeclaringType().getSimpleName() + "#" + signature.getName();
        HttpServletRequest request = getCurrentRequest();

        log.info("接口开始: uri={}, httpMethod={}, controller={}, args={}",
                request == null ? "-" : request.getRequestURI(),
                request == null ? "-" : request.getMethod(),
                methodName,
                JSON.toJSONString(joinPoint.getArgs()));

        Object result = joinPoint.proceed();
        long cost = System.currentTimeMillis() - start;
        log.info("接口结束: uri={}, controller={}, cost={}ms, result={}",
                request == null ? "-" : request.getRequestURI(),
                methodName,
                cost,
                JSON.toJSONString(result));
        return result;
    }

    private HttpServletRequest getCurrentRequest() {
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();
        if (!(attributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        return servletRequestAttributes.getRequest();
    }

    private boolean shouldIgnore(Object arg) {
        return arg == null
                || arg instanceof ServletRequest
                || arg instanceof ServletResponse
                || arg instanceof MultipartFile;
    }
}