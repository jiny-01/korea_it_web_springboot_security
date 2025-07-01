package com.koreait.SpringSecurityStudy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//SMTP 를 위한 이메일
@Data
@AllArgsConstructor
public class SendMailReqDto {
    private String email;

}
