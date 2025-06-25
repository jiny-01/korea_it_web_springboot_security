package com.koreait.SpringSecurityStudy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//로그인하기 위한 Dto
@Data
@AllArgsConstructor
public class SigninReqDto {
    private String username;
    private String password;


}
