package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.ApiRespDto;
import com.koreait.SpringSecurityStudy.dto.OAuth2SignupReqDto;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.entity.UserRole;
import com.koreait.SpringSecurityStudy.repository.OAuth2UserRepository;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OAuth2AuthService {

    @Autowired
    private UserRepository userRepository;    //유저 이메일 있는지 확인하기 위함

    @Autowired
    private UserRoleRepository userRoleRepository;    //등록할 때 쓰기 위함

    @Autowired
    private OAuth2UserRepository oAuth2UserRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    //유저 이메일 있는지 확인하는 부분 -> UserRepo 에 구현
    public ApiRespDto<?> signup(OAuth2SignupReqDto oAuth2SignupReqDto) {
        Optional<User> optionalUser = userRepository.getUserByEmail(oAuth2SignupReqDto.getEmail());
        if (optionalUser.isPresent()) {
            return new ApiRespDto<>("failed", "이미 존재하는 이메일입니다", null);
        }

        Optional<User> user = userRepository.addUser(oAuth2SignupReqDto.toEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(user.get().getUserId())     //xml 에 generate 옵션이 있으므로 userId를 userrole 객체에 등록
                .roleId(3)
                .build();
        userRoleRepository.addUserRole(userRole);

        //OAuth2 table 에 추가  -> oauth2_user_mapper 에 insert문 구현
        oAuth2UserRepository.insertOAuth2User(oAuth2SignupReqDto.toOAuth2User(user.get().getUserId()));
        //Dto 에 있는 정보랑 oauth 유저 tb에도 넣음

        return new ApiRespDto<>("success", "OAuth2 회원가입 완료", null);






    }
}
