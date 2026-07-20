package com.yanque.modules.users.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yanque.common.enums.CommonStatusEnum;
import com.yanque.common.exception.BusinessException;
import com.yanque.common.response.CommonErrorCode;
import com.yanque.modules.users.mapper.SysUserMapper;
import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.req.UserAddReq;
import com.yanque.modules.users.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;

    @Override
    public Long create(UserAddReq req) {
        if (sysUserMapper.selectByUsername(req.getUsername()) != null) {
            throw BusinessException.of(CommonErrorCode.USERNAME_ALREADY_EXISTS);
        }

        SysUserEntity user = new SysUserEntity();
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
        user.setNickname(req.getNickname());
        user.setRealName(req.getRealName());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setUnionId(req.getUnionId());
        user.setStatus(StrUtil.blankToDefault(req.getStatus(), CommonStatusEnum.ACTIVE.name()));
        if (sysUserMapper.insert(user) != 1) {
            throw BusinessException.of(CommonErrorCode.USER_OPERATION_FAILED);
        }
        return user.getId();
    }
}
