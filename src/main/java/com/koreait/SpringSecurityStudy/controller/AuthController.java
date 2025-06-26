package com.koreait.SpringSecurityStudy.controller;

import com.koreait.SpringSecurityStudy.dto.ModifyEmailReqDto;
import com.koreait.SpringSecurityStudy.dto.ModifyPasswordReqDto;
import com.koreait.SpringSecurityStudy.dto.SigninReqDto;
import com.koreait.SpringSecurityStudy.dto.SignupReqDto;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import com.koreait.SpringSecurityStudy.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {


    @Autowired
    private AuthService authService;


    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok("test");
    }

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupReqDto signupReqDto) {
        return ResponseEntity.ok(authService.addUser(signupReqDto));
    }


    //로그인
    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SigninReqDto signinReqDto) {
        return ResponseEntity.ok(authService.signin(signinReqDto));
    }

    //Principal User 와 비교
    @GetMapping("/principal")
    public ResponseEntity<?> getPrincipal() {
        return ResponseEntity.ok(SecurityContextHolder.getContext().getAuthentication());
        //Context Holder 에 넣어뒀던 정보 가져옴
        //Config 에는 추가 안해줘도 됨 - 토큰을 가지고 먼저 filter 로 가서 인증완료 절차를 거친 것이기 때문

    }

    //이메일 수정  - config 에 요청 url 설정 안해둠 -> 어차피 토큰 필요할 것
    @PostMapping("/{userId}")
    public ResponseEntity<?> modifyEmail(@PathVariable Integer userId, @RequestBody ModifyEmailReqDto modifyEmailReqDto) {
        return ResponseEntity.ok(authService.modifyEmail(userId, modifyEmailReqDto));
    }

    //비밀번호 수정
    @PostMapping("/password/{userId}")
    public ResponseEntity<?> modifyPassword(
            @PathVariable Integer userId,
            @RequestBody ModifyPasswordReqDto modifyPasswordReqDto,
            @AuthenticationPrincipal PrincipalUser principalUser
            ) {
        if(!userId.equals(principalUser.getUserId())) {
            return ResponseEntity.badRequest().body("본인의 계정만 변경 가능함");   //잘못된 요청 띄움
        }
        return ResponseEntity.ok(authService.modifyPassword(modifyPasswordReqDto, principalUser));
    }





}
