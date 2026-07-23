package com.yanque.modules.rbac.bootstrap;

import cn.hutool.core.util.StrUtil;
import com.yanque.modules.rbac.annotation.PermissionMeta;
import com.yanque.modules.rbac.enums.PermissionTypeEnum;
import com.yanque.modules.rbac.model.PermissionMetadata;
import com.yanque.modules.rbac.service.SysPermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.reflect.AnnotatedElement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 应用启动后读取 Controller 的 {@link PermissionMeta}，幂等补齐 sys_permission。
 *
 * <p>该任务只新增缺失权限，不删除权限，不覆盖 status、parent_id，也不会修改角色授权关系。</p>
 */
@Component
@RequiredArgsConstructor
public class PermissionMetadataBootstrap implements ApplicationRunner {

    private final RequestMappingHandlerMapping handlerMapping;
    private final SysPermissionService permissionService;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Override
    public void run(ApplicationArguments args) {
        Map<String, PermissionMetadata> metadataByCode = new LinkedHashMap<>();
        handlerMapping.getHandlerMethods().forEach((mapping, handlerMethod) -> {
            collectMetadata(handlerMethod.getBeanType(), null, metadataByCode);
            collectMetadata(handlerMethod.getMethod(), mapping, metadataByCode);
        });
        permissionService.syncMissingPermissions(metadataByCode.values().stream().toList());
    }

    private void collectMetadata(AnnotatedElement element,
                                 RequestMappingInfo mapping,
                                 Map<String, PermissionMetadata> metadataByCode) {
        for (PermissionMeta meta : element.getAnnotationsByType(PermissionMeta.class)) {
            String apiPath = meta.type() == PermissionTypeEnum.API ? resolveApiPath(mapping) : null;
            PermissionMetadata metadata = new PermissionMetadata(
                    meta.value(), meta.name(), meta.type(), meta.parentCode(), apiPath,
                    meta.sort(), meta.description());
            PermissionMetadata previous = metadataByCode.putIfAbsent(meta.value(), metadata);
            if (previous != null && !previous.equals(metadata)) {
                throw new IllegalStateException("发现重复且内容不一致的权限编码：" + meta.value());
            }
        }
    }

    private String resolveApiPath(RequestMappingInfo mapping) {
        if (mapping == null) {
            throw new IllegalStateException("API 类型 PermissionMeta 只能标记在 Controller 方法上");
        }
        Set<org.springframework.web.bind.annotation.RequestMethod> methods = mapping.getMethodsCondition().getMethods();
        Set<PathPattern> patterns = mapping.getPathPatternsCondition().getPatterns();
        if (methods.size() != 1 || patterns.size() != 1) {
            throw new IllegalStateException("一个 API 权限必须且只能对应一个请求方法和一个路径");
        }
        String normalizedContextPath = StrUtil.removeSuffix(StrUtil.blankToDefault(contextPath, ""), "/");
        String path = patterns.iterator().next().getPatternString();
        return methods.iterator().next().name() + " " + normalizedContextPath + path;
    }
}
