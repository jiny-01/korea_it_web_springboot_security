package com.koreait.SpringSecurityStudy.security.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

// UserDetails 서비스에서 이걸 가져옴, 유저 정보를 Principal 객체로 만듦
@Data
@Builder
public class PrincipalUser implements UserDetails {
    private Integer userId;
    private String username;
    private String password;
    private String email;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
