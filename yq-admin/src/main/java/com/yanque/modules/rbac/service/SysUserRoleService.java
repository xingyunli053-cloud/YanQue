package com.yanque.modules.rbac.service;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRolePageReq;
import com.yanque.modules.rbac.pojo.vo.reqvo.UserRoleSaveReq;
import com.yanque.modules.rbac.pojo.vo.resvo.UserRoleRes;

import java.util.List;

public interface SysUserRoleService {
    PageResult<UserRoleRes> page(UserRolePageReq req);
    UserRoleRes detail(Long id);
    Long create(UserRoleSaveReq req);
    void update(Long id, UserRoleSaveReq req);
    void delete(Long id);
    List<Long> getRoleIdsByUserId(Long userId);
    void assignRoles(Long userId, List<Long> roleIds);
}
