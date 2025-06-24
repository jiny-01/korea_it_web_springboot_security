package com.koreait.SpringSecurityStudy.security.jwt;

//JWT 관련 기능 구현 - JWT 필터에서 가져다씀

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component     //Bean 객체로 등록
public class JwtUtil {

    private final Key KEY;

    public JwtUtil(@Value("${jwt.secret}") String secret) {     //jwt의 secret 값을 secret 에 넣어둠
        KEY = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateAccessToken(String id) {
        return Jwts.builder()
                .subject("AccessToken")    //토큰 용도 명시하는 식별자 역할
                .id(id)   //토큰에 고유한 식별자를 부여(사용자 ID, 이메일) => 나중에 토큰 무효화나 사용자 조회 시 사용
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 60L * 24L * 30L)))
                //토큰의 만료기간 설정 => 현재 시간 기준 30일 뒤까지 유효함
                //1000L = 1초를 밀리초로 표현
                //60 * 60 * 24 * 30 => 30일
                .signWith(KEY)  //토큰에 서명을 적용
                .compact();  // 설정한 JWT 내용을 바탕으로 최종적으로 문자열 형태의 JWT 생성
    }
}
