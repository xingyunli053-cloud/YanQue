package com.yanque.commons.context;

/**
 * 保存当前请求对应的登录用户信息。
 *
 * <p>Web 容器会复用工作线程，因此请求结束后必须调用 {@link #clear()}，
 * 防止上一次请求的用户信息泄漏到下一次请求。</p>
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> LOGIN_USER_HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(Long userId, String signSecret, String sessionId) {
        LOGIN_USER_HOLDER.set(new LoginUser(userId, signSecret, sessionId));
    }

    public static Long getUserId() {
        LoginUser loginUser = LOGIN_USER_HOLDER.get();
        return loginUser == null ? null : loginUser.userId();
    }

    public static String getSignSecret() {
        LoginUser loginUser = LOGIN_USER_HOLDER.get();
        return loginUser == null ? null : loginUser.signSecret();
    }

    /** 获取 JWT 的 jti，用于区分同一用户的不同登录会话。 */
    public static String getSessionId() {
        LoginUser loginUser = LOGIN_USER_HOLDER.get();
        return loginUser == null ? null : loginUser.sessionId();
    }

    public static void clear() {
        LOGIN_USER_HOLDER.remove();
    }

    private record LoginUser(Long userId, String signSecret, String sessionId) {
    }
}
