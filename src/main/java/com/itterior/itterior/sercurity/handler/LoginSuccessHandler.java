package com.itterior.itterior.sercurity.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itterior.itterior.aspect.annotation.MeasureExecutionTime;
import com.itterior.itterior.dto.UserDTO;
import com.itterior.itterior.util.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    @Override
    @MeasureExecutionTime
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info(authentication);

        UserDTO userDTO = (UserDTO) authentication.getPrincipal();

        Map<String, Object> claims = userDTO.getClaims();

        String accessToken = jwtUtil.generateToken(claims, 1);
        String refreshToken = jwtUtil.generateToken(claims, 60 * 24);

        claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        response.setStatus(HttpStatus.CREATED.value());
        response.setContentType("application/json");
        response.getWriter().write(new ObjectMapper().writeValueAsString(claims));
        response.getWriter().close();
    }
}
