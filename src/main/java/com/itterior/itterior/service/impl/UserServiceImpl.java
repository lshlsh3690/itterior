package com.itterior.itterior.service.impl;

import com.itterior.itterior.domain.UserRole;
import com.itterior.itterior.dto.UserDTO;
import com.itterior.itterior.entity.Product;
import com.itterior.itterior.entity.User;
import com.itterior.itterior.exception.CustomUserFoundException;
import com.itterior.itterior.exception.CustomUserNotFoundException;
import com.itterior.itterior.repository.ProductRepository;
import com.itterior.itterior.repository.UserRepository;
import com.itterior.itterior.service.UserService;
import com.itterior.itterior.util.CustomFileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Log4j2
public class UserServiceImpl implements UserService {
    private final CustomFileUtil fileUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    public UserDTO getOneByUserName(String username) {
        Optional<User> result = userRepository.selectOneByUserName(username);
        if(result.isEmpty()){
            throw new CustomUserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        User user = result.orElseThrow();
        UserDTO userDTO = entityToDTO(user);
        return userDTO;
    }

    @Override
    public UserDTO getOneByUserId(Long userId) {
        Optional<User> result = userRepository.findById(userId);
        if(result.isEmpty()){
            throw new CustomUserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        User user = result.get();
        UserDTO userDTO = entityToDTO(user);
        return userDTO;
    }

    @Override
    public String register(UserDTO userDTO) throws IOException, ExecutionException, InterruptedException {
        String userName = userDTO.getUsername();
        if(!isUsernameAvailable(userName)){
            throw new CustomUserFoundException("이미 존재하는 사용자 이름입니다.");
        }
        MultipartFile profile = userDTO.getFile();
        long startTime = System.currentTimeMillis();
        String uploadProfileFileName = fileUtil.saveProFile(profile);
        long endTime = System.currentTimeMillis();
        log.info("S3 업로드 걸린 시간 : {}",endTime-startTime);
        userDTO.setProfileImage(uploadProfileFileName);
        User user = dtoToEntity(userDTO);
        user.addRole(UserRole.USER);
        User result = userRepository.save(user);

        log.info(result);
        return result.getUserName();
    }

    @Override
    public boolean isUsernameAvailable(String username) {
        // 사용자 이름(ID) 중복 여부를 확인하는 로직
        Optional<User> existingUser = userRepository.selectOneByUserName(username);
        // username이 존재하지 않으면 사용 가능(true), 존재하면 중복(false)
        return existingUser.isEmpty();
    }

    private UserDTO entityToDTO(User user) {
        UserDTO userDTO = new UserDTO(
                user.getUserName(),
                user.getEmail(),
                user.getPw(),
                user.getNickname(),
                user.isSocial(),
                user.getProfileImage(),
                user.getRoleList().stream().map(userRole -> userRole.name()).collect(Collectors.toList())
        );
        return userDTO;
    }

    private User dtoToEntity(UserDTO userDTO){
        User user = User.builder()
                .userName(userDTO.getUsername())
                .pw(passwordEncoder.encode(userDTO.getPassword()))
                .email(userDTO.getEmail())
                .nickname(userDTO.getNickname())
                .social(userDTO.isSocial())
                .build();
        user.setProfileImageString(userDTO.getProfileImage());
        return user;
    }
}
