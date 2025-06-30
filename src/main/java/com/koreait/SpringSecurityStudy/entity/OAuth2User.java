package com.koreait.SpringSecurityStudy.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
public class OAuth2User {
    //DB 에 있는 칼럼 그대로
    private Integer oauth2Id;
    private Integer userId;
    private String provider;
    private String providerUserId;
    private LocalDateTime createDt;
    private LocalDateTime updateDt;




}
