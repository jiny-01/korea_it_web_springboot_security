package com.koreait.SpringSecurityStudy.service;

import com.koreait.SpringSecurityStudy.dto.*;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.entity.UserRole;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.repository.UserRoleRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRoleRepository userRoleRepository;

    public ApiRespDto<?> addUser(SignupReqDto signupReqDto) {
        Optional<User> optionalUser = userRepository.addUser(signupReqDto.toEntity(bCryptPasswordEncoder));
        UserRole userRole = UserRole.builder()
                .userId(optionalUser.get().getUserId())
                .roleId(3)     //일단 임시사용자("3") 으로 설정
                .build();
        userRoleRepository.addUserRole(userRole);

        //암호화해서 repo의 adduser 에 넘김
        //새로 생성된 계정에 해당하는 권한(Userrole)도 넣어줘야함

        return new ApiRespDto<>("success", "회원가입 성공", optionalUser);
        //존재하는 username 인지 검증
        //이메일 중복확인 (DB에 이미 있는지)

    }

    //로그인 메소드
    public ApiRespDto<?> signin(SigninReqDto signinReqDto) {
        //회원정보 있는지 확인
        Optional<User> optionalUser = userRepository.getUserByUsername(signinReqDto.getUsername());
        if (optionalUser.isEmpty()) {
            return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요", null);
            //"아이디가 잘못됨" 등 맞다 틀리다 알려주면 안됨 - 보안상 문제
        }
        User user = optionalUser.get();
        //DB 에 있는 거랑 입력한 게 일치하는지
        if (!bCryptPasswordEncoder.matches(signinReqDto.getPassword(), user.getPassword())) {
            return new ApiRespDto<>("failed", "사용자 정보를 확인해주세요", null);
        }
        //Dto - 사용자 입력  / user.get DB 에 저장된 암호문일 것
        System.out.println("로그인 성공");
        //ApiResDto 에 토큰을 넘겨줘야함
        String token = jwtUtil.generateAccessToken(user.getUserId().toString());
        //Token 만들 때 String 으로 id 를 받았ㅅ기 때문
        return new ApiRespDto<>("success", "로그인 성공", token);
        //토큰 => 브라우저 F12 눌러서 local storage 안에 들어갈 것
    }

    //이메일 수정
    public ApiRespDto<?> modifyEmail(Integer userId, ModifyEmailReqDto modifyEmailReqDto) {
        User user = modifyEmailReqDto.toEntity(userId);  //유저 객체 생성
        int result = userRepository.updateEmail(user);
        return new ApiRespDto<>("success", "이메일 수정 성공", result);
    }

    //비밀번호 수정
    public ApiRespDto<?> modifyPassword(ModifyPasswordReqDto modifyPasswordReqDto, PrincipalUser principalUser) {

        if (!bCryptPasswordEncoder.matches(modifyPasswordReqDto.getOldPassword(), principalUser.getPassword())) {
            return new ApiRespDto("failed", "사용자 정보를 확인하세요", null);
        }
        if (!modifyPasswordReqDto.getNewPassword().equals(modifyPasswordReqDto.getNewPassword())) {
            return new ApiRespDto<>("failed", "새 비밀번호가 일치하지 않습니다.", null);
        }
        //ContextHolder 안에 principaluser 객체가 있을 것 - 원래 비밀번호
        //원래 비밀번호가 맞는지 확인 (modifypw dto 에 입력한 것과 같은지)

        String password = bCryptPasswordEncoder.encode(modifyPasswordReqDto.getNewPassword());
        int result = userRepository.updatePassword(principalUser.getUserId(), password);
        return new ApiRespDto<>("success", "비밀번호 수정 성공", result);

    }

    //비밀번호 찾기?
}













