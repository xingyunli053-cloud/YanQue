package com.yanque.modules.users.service;

import com.yanque.commons.apires.PageResult;
import com.yanque.modules.users.pojo.vo.reqvo.LoginReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserCreateReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserPageReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserUpdateReq;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
import com.yanque.modules.users.pojo.vo.resvo.UserRes;

public interface SysUserService {

    Long create(UserCreateReq req);

    LoginRes login(LoginReq req);

    void logout(Long userId, String sessionId);

    PageResult<UserRes> page(UserPageReq req);

    UserRes detail(Long id);

    void update(Long id, UserUpdateReq req);

    void delete(Long id);
}
