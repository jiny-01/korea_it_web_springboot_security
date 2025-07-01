package com.koreait.SpringSecurityStudy.controller;

import com.koreait.SpringSecurityStudy.dto.OAuth2MergeReqDto;
import com.koreait.SpringSecurityStudy.dto.OAuth2SignupReqDto;
import com.koreait.SpringSecurityStudy.service.OAuth2AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @Autowired
    private OAuth2AuthService oAuth2AuthService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody OAuth2SignupReqDto oAuth2SignupReqDto) {
        return ResponseEntity.ok(oAuth2AuthService.signup(oAuth2SignupReqDto));
    }

    //DB 에 추가할 때
    //user_tb -> user_role tb -> oauth2_user_tb 로 들어감
    //이때 3개 다하려면 transaction 걸어줌

    @PostMapping("/merge")
    public ResponseEntity<?> merge(@RequestBody OAuth2MergeReqDto oAuth2MergeReqDto) {
        return ResponseEntity.ok(oAuth2AuthService.merge(oAuth2MergeReqDto));
    }


}
