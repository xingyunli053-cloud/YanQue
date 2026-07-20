package com.yanque.modules.users.service;

import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.req.UserAddReq;

public interface SysUserService {

    Long create(UserAddReq req);
}
