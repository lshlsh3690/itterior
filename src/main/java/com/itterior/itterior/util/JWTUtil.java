package com.itterior.itterior.util;


import com.itterior.itterior.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Component
@RequiredArgsConstructor
public class JWTUtil {


    @Value("${application.security.jwt.secret-key}")
    private String key;


    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    public String generateToken(Map<String, Object> valueMap, int min) {

        log.info(getkey() + "key");
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(this.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(valueMap)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant()))
                .signWith(key)
                .compact();

        return jwtStr;
    }

    private String getkey() {
        return this.key;
    }

    public Map<String, Object> validateToken(String token) {

        Map<String, Object> claim = null;
        try {
            SecretKey key = Keys.hmacShaKeyFor(this.key.getBytes("UTF-8"));
            claim = Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token) // 파싱 및 검증, 실패 시 에러
                    .getBody();
            log.info(claim);
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJWTException("MalFormed");
        } catch (ExpiredJwtException expiredJwtException) {
            throw new CustomJWTException("Expired");
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("Invalid");
        } catch (JwtException jwtException) {
            throw new CustomJWTException("JWTError");
        } catch (Exception e) {
            throw new CustomJWTException("Error");
        }
        return claim;
    }
}