package com.yanque.modules.users.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.jwt.JWTUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yanque.commons.apires.PageResult;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.config.AuthProperties;
import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.enums.CommonStatusEnum;
import com.yanque.commons.exception.BusinessException;
import com.yanque.commons.utils.RedisUtils;
import com.yanque.modules.users.mapper.SysUserMapper;
import com.yanque.modules.rbac.service.RbacPermissionService;
import com.yanque.modules.users.pojo.entity.SysUserEntity;
import com.yanque.modules.users.pojo.vo.reqvo.LoginReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserCreateReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserPageReq;
import com.yanque.modules.users.pojo.vo.reqvo.UserUpdateReq;
import com.yanque.modules.users.pojo.vo.resvo.LoginRes;
import com.yanque.modules.users.pojo.vo.resvo.UserRes;
import com.yanque.modules.users.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private static final int SIGN_SECRET_BYTE_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final RedisUtils redisUtils;

    /** 用户数据访问对象。 */
    private final SysUserMapper sysUserMapper;

    /** JWT 和 Redis 会话相关配置。 */
    private final AuthProperties authProperties;

    /** 用户登录后权限写入 Redis Set，并用于注销或用户删除时清理缓存。 */
    private final RbacPermissionService rbacPermissionService;

    @Override
    @Transactional
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
    public PageResult<UserRes> page(UserPageReq req) {
        // PageHelper 必须紧邻需要分页的 Mapper 查询调用。
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<SysUserEntity> users = sysUserMapper.selectList(req);
        PageInfo<SysUserEntity> pageInfo = new PageInfo<>(users);
        List<UserRes> records = users.stream().map(this::toUserRes).toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    @Override
    public UserRes detail(Long id) {
        SysUserEntity user = getUserOrThrow(id);
        return toUserRes(user);
    }

    @Override
    @Transactional
    public void update(Long id, UserUpdateReq req) {
        getUserOrThrow(id);
        if (!hasUpdateField(req)) {
            throw BusinessException.of(CommonErrorCode.PARAM_VALID_FAILED, "至少需要传入一个待修改字段");
        }

        // 修改用户名时需要排除当前用户，避免与其他用户重名。
        if (req.getUsername() != null) {
            SysUserEntity sameUsernameUser = sysUserMapper.selectByUsername(req.getUsername());
            if (sameUsernameUser != null && !id.equals(sameUsernameUser.getId())) {
                throw BusinessException.of(CommonErrorCode.USERNAME_ALREADY_EXISTS);
            }
        }

        SysUserEntity user = new SysUserEntity();
        user.setId(id);
        user.setUsername(req.getUsername());
        user.setPassword(req.getPassword());
            user.setNickname(req.getNickname());
            user.setRealName(req.getRealName());
            user.setPhone(req.getPhone());
            user.setEmail(req.getEmail());
            user.setUnionId(req.getUnionId());
            user.setStatus(req.getStatus());
            if (sysUserMapper.updateById(user) != 1) {
            throw BusinessException.of(CommonErrorCode.USER_OPERATION_FAILED);
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        getUserOrThrow(id);
        if (sysUserMapper.deleteById(id) != 1) {
            throw BusinessException.of(CommonErrorCode.USER_OPERATION_FAILED);
        }

        // 删除用户后立即清除其登录状态，使已签发 token 失效。
        redisUtils.delete(authProperties.getRedisKeyPrefix() + id);
        redisUtils.delete(JwtConstants.SIGN_SECRET_KEY_PREFIX + id);
        rbacPermissionService.evictUserPermissions(id);
    }

    @Override
    public LoginRes login(LoginReq req) {
        // 1. 对用户名和密码做基础非空校验。
        String username = req.getUsername();
        String password = req.getPassword();
        if (StrUtil.hasBlank(username, password)) {
            throw BusinessException.of(CommonErrorCode.USERNAME_OR_PASSWORD_NOT_NULL);
        }

        // 2. 查询用户并校验密码。用户不存在和密码错误返回相同提示，避免暴露账号是否存在。
        SysUserEntity user = sysUserMapper.selectByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            throw BusinessException.of(CommonErrorCode.USER_NOT_EXIST);
        }

        // 3. 只有启用状态的用户可以登录。
        if (!CommonStatusEnum.ACTIVE.name().equals(user.getStatus())) {
            throw BusinessException.of(CommonErrorCode.USER_NOT_ACTIVE);
        }

        // 4. 读取认证配置。JWT 有效期和 Redis 会话 TTL 使用同一个秒数。
        long expireSeconds = authProperties.getTokenExpireSeconds();
        String jwtSecret = authProperties.getJwtSecret();
        if (StrUtil.isBlank(jwtSecret) || expireSeconds <= 0) {
            throw new IllegalStateException("Authentication configuration is invalid");
        }

        // 5. 生成 token 和后续请求签名使用的随机密钥。
        String signSecret = this.createSign();
        String token = this.createToken(user, jwtSecret, expireSeconds);

        // 6. token 和签名密钥使用相同 TTL 存入 Redis。
        Duration loginTtl = Duration.ofSeconds(expireSeconds);
        redisUtils.set(authProperties.getRedisKeyPrefix() + user.getId(), token, loginTtl);
        redisUtils.set(JwtConstants.SIGN_SECRET_KEY_PREFIX + user.getId(), signSecret, loginTtl);
        // 权限编码缓存为 Redis Set，后续 RBAC 拦截器通过 SISMEMBER 校验。
        rbacPermissionService.cacheUserPermissions(user.getId(), loginTtl);
        return new LoginRes(token, signSecret);
    }

    @Override
    public void logout(Long userId, String sessionId) {
        if (userId == null || StrUtil.isBlank(sessionId)) {
            throw BusinessException.of(CommonErrorCode.TOKEN_INVALID);
        }

        // 当前会话的所有 nonce 键都记录在 Set 中，退出时一次性清理。
        String sessionNonceKey = JwtConstants.SIGN_NONCE_SESSION_KEY_PREFIX + userId + ":" + sessionId;
        Set<String> nonceKeys = redisUtils.getSetMembers(sessionNonceKey);
        if (nonceKeys != null && !nonceKeys.isEmpty()) {
            redisUtils.delete(nonceKeys);
        }
        redisUtils.delete(sessionNonceKey);

        // 同步删除登录状态和请求签名密钥，使当前 token 立即失效。
        redisUtils.delete(authProperties.getRedisKeyPrefix() + userId);
        redisUtils.delete(JwtConstants.SIGN_SECRET_KEY_PREFIX + userId);
        rbacPermissionService.evictUserPermissions(userId);
    }

    /**
     * 使用 Hutool 生成登录 JWT。
     */
    private String createToken(SysUserEntity sysUserEntity, String jwtSecret, long expireSeconds) {
        String jti = IdUtil.simpleUUID();
        Map<String, Object> map = new HashMap<>();
        map.put(JwtConstants.JWT_CLAIM_USER_ID, sysUserEntity.getId());
        map.put(JwtConstants.JWT_CLAIM_EXPIRE_TIME,
                System.currentTimeMillis() + Duration.ofSeconds(expireSeconds).toMillis());
        // jti 是 token 的唯一标识，保证同一用户每次登录生成不同 token。
        map.put(JwtConstants.JWT_CLAIM_ID, jti);

        return JWTUtil.createToken(map, jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    // 生成一个签名方法. 目的.
    private String createSign() {
        // 1:生成一个数组
        byte[] bytes = new byte[SIGN_SECRET_BYTE_LENGTH];
        // 2: 使用随机数,对bytes进行填充. Random,可预测.
        SECURE_RANDOM.nextBytes(bytes);
        // 3: 使用Base64进行编码.
        // Base64案例: 标准Base64：9r03Vn9-2kQ_zrO5cG7fL1s9jBx4dT8hQw==
        // getUrlEncoder();方法的作用:  /  +   --->  -  _
        // withoutPadding();  是把最后的==去掉.
        // encodeToString(); 将bytes进行翻译成String,返回.
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private SysUserEntity getUserOrThrow(Long id) {
        if (id == null || id <= 0) {
            throw BusinessException.of(CommonErrorCode.USER_DETAIL_NOT_FOUND);
        }
        SysUserEntity user = sysUserMapper.selectById(id);
        if (user == null) {
            throw BusinessException.of(CommonErrorCode.USER_DETAIL_NOT_FOUND);
        }
        return user;
    }

    private boolean hasUpdateField(UserUpdateReq req) {
        return req.getUsername() != null
                || req.getPassword() != null
                || req.getNickname() != null
                || req.getRealName() != null
                || req.getPhone() != null
                || req.getEmail() != null
                || req.getUnionId() != null
                || req.getStatus() != null;
    }

    private UserRes toUserRes(SysUserEntity user) {
        UserRes response = new UserRes();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRealName(user.getRealName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setUnionId(user.getUnionId());
        response.setStatus(user.getStatus());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
