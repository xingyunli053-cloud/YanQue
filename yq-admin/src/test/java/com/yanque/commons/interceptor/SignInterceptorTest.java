package com.yanque.commons.interceptor;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.digest.HmacAlgorithm;
import com.yanque.commons.apires.CommonErrorCode;
import com.yanque.commons.constant.JwtConstants;
import com.yanque.commons.context.UserContext;
import com.yanque.commons.exception.BusinessException;
import com.yanque.commons.utils.RedisUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignInterceptorTest {

    private static final Long USER_ID = 10L;
    private static final String SIGN_SECRET = "request-sign-secret";
    private static final String NONCE = "nonce-001";

    private RedisUtils redisUtils;
    private SignInterceptor interceptor;

    @BeforeEach
    void setUp() {
        redisUtils = mock(RedisUtils.class);
        interceptor = new SignInterceptor(redisUtils);
        UserContext.set(USER_ID, SIGN_SECRET);
    }

    @AfterEach
    void tearDown() {
        UserContext.clear();
    }

    @Test
    void rejectsRequestWhenSignHeaderIsMissing() {
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(
                        new MockHttpServletRequest(),
                        new MockHttpServletResponse(),
                        new Object()));

        assertEquals(CommonErrorCode.SIGN_HEADER_MISSING.getCode(), exception.getCode());
    }

    @Test
    void rejectsInvalidTimestamp() {
        MockHttpServletRequest request = baseRequest("not-a-timestamp", NONCE);
        request.addHeader(JwtConstants.SIGN_HEADER, "00");

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        assertEquals(CommonErrorCode.SIGN_TIMESTAMP_INVALID.getCode(), exception.getCode());
    }

    @Test
    void rejectsRepeatedNonce() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        MockHttpServletRequest request = signedRequest(timestamp, NONCE);
        when(redisUtils.setIfAbsent(
                JwtConstants.SIGN_NONCE_KEY_PREFIX + USER_ID + ":" + NONCE,
                "1",
                JwtConstants.SIGN_VALID_DURATION)).thenReturn(false);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        assertEquals(CommonErrorCode.SIGN_NONCE_REPEATED.getCode(), exception.getCode());
    }

    @Test
    void rejectsExpiredRequest() {
        String timestamp = String.valueOf(
                System.currentTimeMillis() - JwtConstants.SIGN_VALID_DURATION.toMillis() - 1);
        MockHttpServletRequest request = signedRequest(timestamp, NONCE);
        allowNonce(NONCE);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        assertEquals(CommonErrorCode.SIGN_REQUEST_EXPIRED.getCode(), exception.getCode());
    }

    @Test
    void rejectsIncorrectSignature() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        MockHttpServletRequest request = baseRequest(timestamp, NONCE);
        request.addHeader(JwtConstants.SIGN_HEADER, "00");
        allowNonce(NONCE);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));

        assertEquals(CommonErrorCode.SIGN_INVALID.getCode(), exception.getCode());
    }

    @Test
    void allowsRequestWithValidSignature() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        MockHttpServletRequest request = signedRequest(timestamp, NONCE);
        allowNonce(NONCE);

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
        verify(redisUtils).setIfAbsent(
                JwtConstants.SIGN_NONCE_KEY_PREFIX + USER_ID + ":" + NONCE,
                "1",
                JwtConstants.SIGN_VALID_DURATION);
    }

    private void allowNonce(String nonce) {
        when(redisUtils.setIfAbsent(
                JwtConstants.SIGN_NONCE_KEY_PREFIX + USER_ID + ":" + nonce,
                "1",
                JwtConstants.SIGN_VALID_DURATION)).thenReturn(true);
    }

    private MockHttpServletRequest signedRequest(String timestamp, String nonce) {
        MockHttpServletRequest request = baseRequest(timestamp, nonce);
        String source = String.join("\n",
                "GET",
                "/yq-admin/api/sysUser",
                "pageNum=1&pageSize=10",
                timestamp,
                nonce);
        String sign = SecureUtil.hmac(
                        HmacAlgorithm.HmacSHA256,
                        SIGN_SECRET.getBytes(CharsetUtil.CHARSET_UTF_8))
                .digestHex(source, CharsetUtil.CHARSET_UTF_8);
        request.addHeader(JwtConstants.SIGN_HEADER, sign);
        return request;
    }

    private MockHttpServletRequest baseRequest(String timestamp, String nonce) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/yq-admin/api/sysUser");
        request.setQueryString("pageNum=1&pageSize=10");
        request.addHeader(JwtConstants.SIGN_TIMESTAMP_HEADER, timestamp);
        request.addHeader(JwtConstants.SIGN_NONCE_HEADER, nonce);
        return request;
    }
}
