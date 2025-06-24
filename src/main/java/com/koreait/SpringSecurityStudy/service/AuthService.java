package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.ApiRespDto;
import com.koreait.SpringSecurityStudy.dto.SignupReqDto;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    public ApiRespDto<?> addUser(SignupReqDto signupReqDto) {
        int result = userRepository.addUser(signupReqDto.toEntity(bCryptPasswordEncoder));
        //암호화해서 repo의 adduser 에 넘김
        return new ApiRespDto<>("success", "회원가입 성공", result);
        //존재하는 username 인지 검증
        //이메일 중복확인 (DB에 이미 있는지)

    }











}
