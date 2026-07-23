package com.yanque.commons.constant;

import java.time.Duration;

/**
 * JWT 登录认证相关常量。
 */
public final class JwtConstants {

    /** 客户端传递 token 使用的请求头。 */
    public static final String AUTHORIZATION_HEADER = "Authorization";

    /** Authorization 请求头支持的标准 Bearer 前缀。 */
    public static final String BEARER_PREFIX = "Bearer ";

    /** 登录接口不参与 token 拦截。 */
    public static final String LOGIN_PATH = "/api/sysUser/login";

    public static final String SIGN_TIMESTAMP_HEADER = "X-Timestamp";
    public static final String SIGN_NONCE_HEADER = "X-Nonce";
    public static final String SIGN_HEADER = "X-Sign";
    public static final String SIGN_NONCE_KEY_PREFIX = "yanque:sign:nonce:";
    /** 用于归档一个登录会话产生的 nonce 键，退出时可精确清理该会话的 nonce。 */
    public static final String SIGN_NONCE_SESSION_KEY_PREFIX = "yanque:sign:nonce:session:";
    public static final String SIGN_SECRET_KEY_PREFIX = "yanque:sign:secret:";

    /** JWT 中保存用户 ID 的载荷名称。 */
    public static final String JWT_CLAIM_USER_ID = "uid";

    /** JWT 中保存过期时间戳的载荷名称，单位为毫秒。 */
    public static final String JWT_CLAIM_EXPIRE_TIME = "expire_time";

    /** JWT 唯一标识载荷名称。 */
    public static final String JWT_CLAIM_ID = "jti";

    public static final Duration SIGN_VALID_DURATION = Duration.ofMinutes(5);

    private JwtConstants() {
    }
}
