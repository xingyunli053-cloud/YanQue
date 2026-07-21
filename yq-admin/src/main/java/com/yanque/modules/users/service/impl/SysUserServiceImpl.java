package com.yanque.modules.users.service.impl;

import cn.hutool.core.util.StrUtil;
import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.exception.BusinessException;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.modules.users.mapper.SysUserMapper;
import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.vo.reqvo.LoginReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserCreateReq;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
import com.yanque.modules.users.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private final SysUserMapper sysUserMapper;
    private  final StringRedisTemplate redisTemplate;

    @Override
    public Long create(UserCreateReq req) {
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

    @Override
    public LoginRes login(LoginReq req) {

        // (1) 校验用户名称和密码.( 学习了springboot 校验框架后, 可以省略不写)
        String username = req.getUsername();
        String password = req.getPassword();
        if (StrUtil.hasBlank(username, password)) {
            throw BusinessException.of(CommonErrorCode.USERNAME_OR_PASSWORD_NOT_NULL);
        }
        // (2) 调用Mapper层, 根据用户名称获取用户信息.
        SysUserEntity sysUserEntity = sysUserMapper.selectByUsername(username);
        // (3) 判断用户信息是否为空.
        if (sysUserEntity == null) {
            throw BusinessException.of(CommonErrorCode.USER_NOT_EXIST);
        }

        // (4) 密码校验.
        if (!password.equals(sysUserEntity.getPassword())) {
            throw BusinessException.of(CommonErrorCode.USER_NOT_EXIST);
        }

        // (5) 判断用户是否是 启用  | 禁用
        if (!CommonStatusEnum.ACTIVE.name().equals(sysUserEntity.getStatus())) {
            throw BusinessException.of(CommonErrorCode.USER_NOT_ACTIVE);
        }

        //  (6) 生成token, 存redis
        String token = "";
        String signSecret = "";

        // (7) 存入redis 中.
        redisTemplate.opsForValue().set("yanque:jwt:token", token);

        // (8) 拼接返回结果.
        LoginRes loginRes = new LoginRes();
        loginRes.setToken(token);
        loginRes.setSignSecret("signSecret");

        return loginRes;
    }
}
