package com.koreait.SpringSecurityStudy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ModifyPasswordReqDto {
    private String oldPassword;
    private String newPassword;        //변경할 PW
    private String newPasswordCheck;   //재입력해서 확인할 PW
}
