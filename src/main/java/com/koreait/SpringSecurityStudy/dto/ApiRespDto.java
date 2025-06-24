package com.koreait.SpringSecurityStudy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

//공통 Response Dto
@Data
@AllArgsConstructor
public class ApiRespDto<T> {
    private String status;
    private String message;
    private T data;
}
