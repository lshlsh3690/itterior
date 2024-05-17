package com.itterior.itterior.config;

import com.itterior.itterior.sercurity.JWTCheckFilter;
import com.itterior.itterior.sercurity.handler.LoginFailHandler;
import com.itterior.itterior.sercurity.handler.LoginSuccessHandler;
import com.itterior.itterior.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    //"/api/user/details/**"
    private static final String[] securityList = {"/api/products/register", "/api/products/modify/**","/api/products/remove/**",  "/api/cart/**"};

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .cors(cors -> {
                    cors.configurationSource(corsConfigurationSource());
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrf -> csrf.disable())
                .formLogin(formLogin -> {
                    formLogin.loginPage("/api/user/login")
                            .successHandler(new LoginSuccessHandler(jwtUtil))
                            .failureHandler(new LoginFailHandler());
                })
                .authorizeHttpRequests(request ->  request.requestMatchers(securityList).authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(new JWTCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class); //JWT체크
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
//        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedOrigins(List.of("https://it-terior.netlify.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}