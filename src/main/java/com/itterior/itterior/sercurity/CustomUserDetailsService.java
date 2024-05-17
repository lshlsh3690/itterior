package com.itterior.itterior.sercurity;

import com.itterior.itterior.aspect.annotation.MeasureExecutionTime;
import com.itterior.itterior.dto.UserDTO;
import com.itterior.itterior.entity.Product;
import com.itterior.itterior.entity.User;
import com.itterior.itterior.exception.CustomUserNotFoundException;
import com.itterior.itterior.repository.ProductRepository;
import com.itterior.itterior.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;


@Log4j2
@RequiredArgsConstructor
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        long startTime = System.currentTimeMillis();
        log.info("----------------loadUserByUsername-----------------------------");
        User user = userRepository.getWithRoles(username);
        if (user == null) {
            throw new CustomUserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        UserDTO userDTO = new UserDTO(
                user.getUserName(),
                user.getEmail(),
                user.getPw(),
                user.getNickname(),
                user.isSocial(),
                user.getProfileImage(),
                user.getRoleList().stream().map(userRole -> userRole.name()).collect(Collectors.toList()));

        log.info(userDTO);
        long endTime = System.currentTimeMillis();
        log.info("Method execution time: {} milliseconds", endTime-startTime);

        return userDTO;
    }
}
