package com.itterior.itterior.repository;

import com.itterior.itterior.domain.UserRole;
import com.itterior.itterior.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserRepositoryTest {

    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Test
    void getWithRoles() {
    }

    @Test
    void selectOneByUserName() {
    }

    @Test
    void selectOneByUserId() {
    }

    @Test
    public void testInsertUser(){

//        for (int i = 0; i < 10 ; i++) {
//
//            User user = User.builder()
//                    .email("user"+i+"@aaa.com")
//                    .pw(passwordEncoder.encode("1111"))
//                    .nickname("USER"+i)
//                    .userName("user"+i)
//                    .social(false)
//                    .profileImage("")
//                    .build();
//
//            user.addRole(UserRole.USER);
//
//            userRepository.save(user);
//        }
    }
}