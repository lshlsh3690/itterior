package com.itterior.itterior.controller;

import com.itterior.itterior.aspect.annotation.MeasureExecutionTime;
import com.itterior.itterior.dto.UserDTO;
import com.itterior.itterior.service.UserService;
import com.itterior.itterior.util.CustomFileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Log4j2
@Tag(name = "User", description = "User API")
public class UserController {
    private final CustomFileUtil fileUtil;
    private final UserService userService;
    @MeasureExecutionTime
    @PostMapping("/register")
    public ResponseEntity register(UserDTO userDTO) throws IOException, ExecutionException, InterruptedException {
        log.info("register..." + userDTO);
        String result = userService.register(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/details/{username}")
//    @PreAuthorize("hasRole('ROLE_USER')")
    public UserDTO getUserByUserId(@PathVariable("username") String username) {
        log.info("details... " + username);
        UserDTO oneByUserId = this.userService.getOneByUserName(username);

        return oneByUserId;
    }

    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName) throws IOException {
        log.info(fileName);
        return fileUtil.getFile(fileName);
    }

    @PostMapping("/checkUsername")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestBody Map<String, String> requestData) {
        String usernameToCheck = requestData.get("username");
        log.info("checkusername: "+ usernameToCheck);
        boolean isUsernameValid = userService.isUsernameAvailable(usernameToCheck);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isValid", isUsernameValid);
        return ResponseEntity.ok(response);
    }
}
