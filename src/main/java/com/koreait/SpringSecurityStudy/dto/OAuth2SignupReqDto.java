package com.koreait.SpringSecurityStudy.dto;

import com.koreait.SpringSecurityStudy.entity.OAuth2User;
import com.koreait.SpringSecurityStudy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Data
@AllArgsConstructor
public class OAuth2SignupReqDto {
    private String email;
    private String username;
    private String password;
    private String provider;
    private String providerUserId;

    public User toEntity(BCryptPasswordEncoder bCryptPasswordEncoder) {
        return User.builder()
                .username(this.username)
                .password(bCryptPasswordEncoder.encode(this.password))
                .email(this.email)
                .build();
    }

    public OAuth2User toOAuth2User(int userId) {
        return OAuth2User.builder()
                .userId(userId)
                .provider(this.provider)
                .providerUserId(this.providerUserId)
                .build();
    }
}
