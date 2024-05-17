package com.itterior.itterior.controller;

import com.itterior.itterior.util.CustomJWTException;
import com.itterior.itterior.util.JWTUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
@Tag(name = "JWT", description = "JWT API")
public class APIRefreshController {
    private final JWTUtil jwtUtil;

    @RequestMapping("/api/user/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken){
        long startTime = System.currentTimeMillis();

        log.info("refresh()" + authHeader + " : " + refreshToken);
        if(refreshToken == null) {
            throw new CustomJWTException("NULL_REFRASH");
        }

        if(authHeader == null || authHeader.length() < 7) {
            throw new CustomJWTException("INVALID_STRING");
        }
        String accessToken = authHeader.substring(7);
        //Access 토큰이 만료되지 않았다면
//        jwtUtil.revokeTokenByJWT(accessToken);
        if(checkExpiredToken(accessToken) == false ) {
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }
        //Refresh토큰 검증
        Map<String, Object> claims = jwtUtil.validateToken(refreshToken);

        String newAccessToken = jwtUtil.generateToken(claims, 10);
        String newRefreshToken = jwtUtil.generateToken(claims, 60*24);
//        if(checkTime((Long) claims.get("exp")) == true){
//            newRefreshToken =  jwtUtil.generateToken(claims, 60 * 24);
//            jwtUtil.revokeTokenByJWT(refreshToken);
//        }else {
//            newRefreshToken = refreshToken;
//        }

//        jwtUtil.saveToken((String) claims.get("username"), newAccessToken);
//        jwtUtil.saveToken((String) claims.get("username"), newRefreshToken);
        long endTime = System.currentTimeMillis();

        log.info("토큰 저장 시간 {}", endTime-startTime);


        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);
    }

    //시간이 1시간 미만으로 남았다면
    private boolean checkTime(long exp) {

        //JWT exp를 날짜로 변환
        java.util.Date expDate = new java.util.Date( (long)exp * (1000 ));

        //현재 시간과의 차이 계산 - 밀리세컨즈
        long gap   = expDate.getTime() - System.currentTimeMillis();

        //분단위 계산
        long leftMin = gap / (1000 * 60);

        //1시간도 안남았는지..
        return leftMin < 60;
    }

    private boolean checkExpiredToken(String token) {

        try{
            jwtUtil.validateToken(token);
        }catch(CustomJWTException ex) {
            return true;
        }

//        Tokens token1 = jwtUtil.findToken(token);
//        if (token1.isRevoked() || token1.isExpired()) {
//            return true;
//        }

        return false;
    }

}