package com.itterior.itterior.sercurity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itterior.itterior.aspect.annotation.MeasureExecutionTime;
import com.itterior.itterior.dto.UserDTO;
import com.itterior.itterior.util.CustomJWTException;
import com.itterior.itterior.util.JWTUtil;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class JWTCheckFilter extends OncePerRequestFilter {
    private final JWTUtil jwtUtil;
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

        // Preflight요청은 체크하지 않음
        log.info(request.getMethod());
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }
        String path = request.getRequestURI();
        log.info("check uri.............." + path);
        //api/member/ 경로의 호출은 체크하지 않음
        if(path.equals("/api/user/login")) {
            return true;
        }
        if (path.startsWith("/api/products/list")) {
            return true;
        }
        //이미지 조회 경로는 체크하지 않는다면
        if(path.startsWith("/api/products/view/")) {
            return true;
        }
        //이미지 조회 경로는 체크하지 않는다면
        if(path.startsWith("/api/user/view/")) {
            return true;
        }
        if (path.startsWith("/api/products/productions")) {
            return true;
        }
        if (path.startsWith("/api/products/search")) {
            return true;
        }
        if (path.startsWith("/api/products/popular")){
            return true;
        }
        if (path.startsWith("/api/user/refresh")){
            return true;
        }
        if (path.startsWith("/api/user/checkUsername")) {
            return true;
        }
        if(path.startsWith("/api/user/details")) {
            return true;
        }
        if(path.equals("/api/user/register")) {
            return true;
        }
        if(path.startsWith("/swagger"))
        {
            return true;
        }
        if(path.startsWith("/v3"))
        {
            return true;
        }
        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException, IOException {
        long startTime = System.currentTimeMillis();
        log.info("------------------------JWTCheckFilter.......................");

        String authHeaderStr = request.getHeader("Authorization");

        try {
            //Bearer accestoken
            String accessToken = authHeaderStr.substring(7);

            Map<String, Object> claims = jwtUtil.validateToken(accessToken);


            log.info("JWT claims: " + claims);

            String username = (String) claims.get("username");
            String pw = (String) claims.get("pw");
            String email = (String) claims.get("email");
            String nickname = (String) claims.get("nickname");
            Boolean social = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");
            String profileImage = (String) claims.get("profileImage");



            UserDTO userDTO = new UserDTO(username, email, pw, nickname, social, profileImage,  roleNames);

            log.info("-----------------------------------doFilterInternal");
            log.info(userDTO);
            log.info(userDTO.getAuthorities());

            UsernamePasswordAuthenticationToken authenticationToken
                    = new UsernamePasswordAuthenticationToken(userDTO, pw, userDTO.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authenticationToken);


            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            log.info("Spring Security filter execution time: {} ms", duration);

            filterChain.doFilter(request, response);

        }catch(CustomJWTException e){
            log.error("JWT Check Error..............");
            log.error(e.getMessage());

            Map<String, String> map = Map.of("error", "ERROR_ACCESS_TOKEN");
            String msg = new ObjectMapper().writeValueAsString(map);

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }
}