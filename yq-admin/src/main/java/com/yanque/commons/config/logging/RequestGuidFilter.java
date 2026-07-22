package com.yanque.commons.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestGuidFilter extends OncePerRequestFilter {

    public static final String REQUEST_GUID_KEY = "guid";
    public static final String REQUEST_GUID_ATTR = "requestGuid";
    public static final String REQUEST_GUID_HEADER = "X-Request-Guid";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String guid = resolveGuid(request);
        // 当前线程,子线程失效
        MDC.put(REQUEST_GUID_KEY, guid);
        // 适用于多线程,异步请求中
        request.setAttribute(REQUEST_GUID_ATTR, guid);
        // 设置到响应头
        response.setHeader(REQUEST_GUID_HEADER, guid);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_GUID_KEY);
        }
    }

    private String resolveGuid(HttpServletRequest request) {
        // 如果前端或网关已经传了链路 ID，就继续沿用
        String guid = request.getHeader(REQUEST_GUID_HEADER);
        if (StringUtils.hasText(guid)) {
            return guid.trim();
        }
        // 没传就生成 UUID保证每次请求都有唯一标识
        return UUID.randomUUID().toString().replace("-", "");
    }
}