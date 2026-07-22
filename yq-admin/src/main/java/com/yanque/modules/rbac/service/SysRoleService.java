package com.yanque.modules.rbac.service;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.reqvo.RoleCreateReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RolePageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.RoleUpdateReq;
import com.yanque.modules.rbac.pojo.vo.resvo.RoleRes;

import java.util.List;

public interface SysRoleService {
    PageResult<RoleRes> page(RolePageReq req);
    RoleRes detail(Long id);
    Long create(RoleCreateReq req);
    void update(Long id, RoleUpdateReq req);
    void delete(Long id);
    void assignPermissions(Long id, List<Long> permissionIds);
}
