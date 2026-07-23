package com.yanque.modules.rbac.service;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionCreateReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionTreeReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.PermissionUpdateReq;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionRes;
import com.yanque.modules.rbac.pojo.vo.resvo.PermissionTreeRes;
import com.yanque.modules.rbac.model.PermissionMetadata;

import java.util.List;

public interface SysPermissionService {
    PageResult<PermissionRes> page(PermissionPageReq req);
    List<PermissionTreeRes> tree(PermissionTreeReq req);
    PermissionRes detail(Long id);
    Long create(PermissionCreateReq req);
    void update(Long id, PermissionUpdateReq req);
    void delete(Long id);

    /** 启动扫描时仅插入缺失权限，不更新或删除人工维护的权限数据。 */
    void syncMissingPermissions(List<PermissionMetadata> metadataList);
}
