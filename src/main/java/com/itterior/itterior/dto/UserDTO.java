package com.itterior.itterior.dto;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@ToString(callSuper = true)
@Getter
@Setter
@Log4j2
public class UserDTO extends User {
    private String username;
    private String pw;
    private String email;
    private String nickname;
    private boolean social;
    private String profileImage;
    private MultipartFile file;
    public UserDTO(String username, String email, String pw, String nickname, boolean social, String profileImage, List<String> roleNames) {
        super(
                username,
                pw,
                roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_"+str)).collect(Collectors.toList()));

        this.username=username;
        this.pw = pw;
        this.email = email;
        this.nickname = nickname;
        this.social = social;
        this.profileImage = profileImage;
    }

    public Map<String, Object> getClaims() {
        Collection<GrantedAuthority> authorities = getAuthorities();
        List<String> roleNames = authorities.stream().map(auth -> auth.getAuthority().substring(5)).collect(Collectors.toList());

        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("username", username);
        dataMap.put("email", email);
        dataMap.put("pw", pw);
        dataMap.put("nickname", nickname);
        dataMap.put("social", social);
        dataMap.put("roleNames", roleNames);
        dataMap.put("profileImage", profileImage);

        return dataMap;
    }
}