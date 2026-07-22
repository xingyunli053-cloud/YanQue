package com.yanque.modules.rbac.service;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionPageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePermissionSaveReq;
import com.yanque.modules.rbac.pojo.vo.resvo.RolePermissionRes;

public interface SysRolePermissionService {
    PageResult<RolePermissionRes> page(RolePermissionPageReq req);
    RolePermissionRes detail(Long id);
    Long create(RolePermissionSaveReq req);
    void update(Long id, RolePermissionSaveReq req);
    void delete(Long id);
}
