package com.yanque.commons.exception;

import com.yanque.commons.apires.ApiResponse;
import com.yanque.commons.apires.CommonErrorCode;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void tokenExceptionReturnsHttp401() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(
                BusinessException.of(CommonErrorCode.TOKEN_INVALID));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(CommonErrorCode.TOKEN_INVALID.getCode(), response.getBody().getCode());
    }

    @Test
    void repeatedNonceReturnsHttp409() {
        ResponseEntity<ApiResponse<Void>> response = handler.handleBusinessException(
                BusinessException.of(CommonErrorCode.SIGN_NONCE_REPEATED));

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(CommonErrorCode.SIGN_NONCE_REPEATED.getCode(), response.getBody().getCode());
    }
}
