package com.itterior.itterior.exception;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(CustomUserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleException(CustomUserNotFoundException ex, WebRequest request ) {
        // 에러 메시지를 맵으로 생성
        Map<String, String> errorResponse = new HashMap<>();
        String msg = ex.getMessage();
        errorResponse.put("error", msg);

        // HTTP 상태 코드와 함께 맵 반환
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
    }
}
