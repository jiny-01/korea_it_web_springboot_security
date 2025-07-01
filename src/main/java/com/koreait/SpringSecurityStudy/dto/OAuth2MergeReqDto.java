package com.koreait.SpringSecurityStudy.dto;

import com.koreait.SpringSecurityStudy.entity.OAuth2User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor

public class OAuth2MergeReqDto {
    private String username;
    private String password;
    private String provider;
    private String providerUserId;

    public OAuth2User toOAuth2User(Integer userId) {
        return OAuth2User.builder()
                .userId(userId)
                .providerUserId(this.providerUserId)
                .provider(this.provider)
                .build();

    }
}
