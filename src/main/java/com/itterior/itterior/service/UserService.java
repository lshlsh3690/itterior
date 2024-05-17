package com.itterior.itterior.service;

import com.itterior.itterior.dto.ProductDTO;
import com.itterior.itterior.dto.UserDTO;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface UserService {
    UserDTO getOneByUserName(String username);

    UserDTO getOneByUserId(Long userId);

    String register(UserDTO userDTO) throws IOException, ExecutionException, InterruptedException;

    boolean isUsernameAvailable(String usernameToCheck);
}
