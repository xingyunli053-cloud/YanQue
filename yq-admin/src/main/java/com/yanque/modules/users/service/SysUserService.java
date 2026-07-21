package com.yanque.modules.users.service;

import com.yanque.modules.users.pojo.vo.reqvo.LoginReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserCreateReq;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;

public interface SysUserService {

    Long create(UserCreateReq req);


    LoginRes login(LoginReq req);
}
