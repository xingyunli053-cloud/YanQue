package com.yanque.commons.interceptor;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.context.UserContext;
import com.yanque.commons.exception.BusinessException;
import com.yanque.commons.utils.RedisUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.MessageDigest;
import java.util.Locale;

/**
 * 请求签名拦截器。
 *
 * <p>前后端使用完全相同的五段原文计算 HMAC-SHA256：</p>
 * <pre>
 * METHOD\n
 * URI\n
 * QUERY\n
 * TIMESTAMP\n
 * NONCE
 * </pre>
 *
 * <p>其中 URI 使用 {@link HttpServletRequest#getRequestURI()}，包含项目 context-path；
 * QUERY 使用原始查询字符串，没有查询参数时使用空字符串。</p>
 */
@Component
@RequiredArgsConstructor
public class SignInterceptor implements HandlerInterceptor {

    private static final String NONCE_REDIS_VALUE = "1";

    private final RedisUtils redisUtils;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // 1. 从请求头中获取时间戳、随机数和前端计算的签名。
        String timestampValue = request.getHeader(JwtConstants.SIGN_TIMESTAMP_HEADER);
        String nonce = request.getHeader(JwtConstants.SIGN_NONCE_HEADER);
        String clientSign = request.getHeader(JwtConstants.SIGN_HEADER);

        // 2. 三个签名请求头缺少任意一个，都无法完成签名验证。
        if (StrUtil.hasBlank(timestampValue, nonce, clientSign)) {
            throw BusinessException.of(CommonErrorCode.SIGN_HEADER_MISSING);
        }

        // 前端应传递 Date.now() 生成的毫秒时间戳。
        if (!NumberUtil.isLong(timestampValue)) {
            throw BusinessException.of(CommonErrorCode.SIGN_TIMESTAMP_INVALID);
        }
        long timestamp = NumberUtil.parseLong(timestampValue);

        Long userId = UserContext.getUserId();
        String signSecret = UserContext.getSignSecret();
        String sessionId = UserContext.getSessionId();
        if (userId == null || StrUtil.isBlank(signSecret) || StrUtil.isBlank(sessionId)) {
            throw BusinessException.of(CommonErrorCode.SIGN_SECRET_NOT_FOUND);
        }

        // 3. 通过 Redis SETNX 记录 nonce，防止同一个请求被重复执行。
        // Redis 键中加入 uid 和会话 jti，避免不同用户或不同登录会话互相影响。
        String nonceKey = JwtConstants.SIGN_NONCE_KEY_PREFIX + userId + ":" + sessionId + ":" + nonce;
        String sessionNonceKey = JwtConstants.SIGN_NONCE_SESSION_KEY_PREFIX + userId + ":" + sessionId;
        Boolean stored = redisUtils.setIfAbsent(
                nonceKey,
                NONCE_REDIS_VALUE,
                JwtConstants.SIGN_VALID_DURATION);
        if (!Boolean.TRUE.equals(stored)) {
            throw BusinessException.of(CommonErrorCode.SIGN_NONCE_REPEATED);
        }
        // 将 nonce 键归档到当前会话。退出登录时可以删除集合内所有 nonce 键。
        redisUtils.addToSet(sessionNonceKey, nonceKey);
        redisUtils.expire(sessionNonceKey, JwtConstants.SIGN_VALID_DURATION);

        // 4. 签名只允许在五分钟时间窗口内使用，同时拒绝超前五分钟以上的时间戳。
        long now = System.currentTimeMillis();
        long validMillis = JwtConstants.SIGN_VALID_DURATION.toMillis();
        if (timestamp < now - validMillis || timestamp > now + validMillis) {
            throw BusinessException.of(CommonErrorCode.SIGN_REQUEST_EXPIRED);
        }

        // 5. 按前端约定的顺序拼接签名原文。query 为空时仍保留这一行。
        String source = buildSource(request, timestampValue, nonce);
        byte[] serverSign = SecureUtil.hmac(
                        HmacAlgorithm.HmacSHA256,
                        signSecret.getBytes(CharsetUtil.CHARSET_UTF_8))
                .digest(source, CharsetUtil.CHARSET_UTF_8);

        // 6. 前端传递十六进制签名。先解码再进行常量时间比较，降低时序攻击风险。
        byte[] clientSignBytes;
        try {
            clientSignBytes = HexUtil.decodeHex(clientSign);
        } catch (RuntimeException exception) {
            throw BusinessException.of(CommonErrorCode.SIGN_INVALID);
        }
        if (!MessageDigest.isEqual(serverSign, clientSignBytes)) {
            throw BusinessException.of(CommonErrorCode.SIGN_INVALID);
        }

        // 7. nonce、时间戳和签名全部通过校验后放行请求。
        return true;
    }

    private String buildSource(HttpServletRequest request, String timestamp, String nonce) {
        String method = request.getMethod().toUpperCase(Locale.ROOT);
        String uri = request.getRequestURI();
        String query = StrUtil.blankToDefault(request.getQueryString(), "");
        return String.join("\n", method, uri, query, timestamp, nonce);
    }
}
