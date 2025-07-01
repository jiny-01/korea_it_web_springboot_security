package com.koreait.SpringSecurityStudy.security.jwt;

//JWT 관련 기능 구현 - JWT 필터에서 가져다씀

import io.jsonwebtoken.*;
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

    //이메일 인증용 토큰 발급
    public String generateMailVerifyToken(String id) {
        return Jwts.builder()
                .subject("VerifyToken")
                .id(id)
                .expiration(new Date(new Date().getTime() + (1000L * 60L * 3)))    //3분
                .signWith(KEY)
                .compact();
    }


    //토큰의 형식이 맞는지 확인하는 메소드
    public boolean isBearer(String token) {
        if (token == null) {
            return false;
        }
        if (!token.startsWith("Bearer ")) {   //토큰이 Bearer 로 시작안할 경우
            return false;
        }
        return true;    //유효한 토큰임
    }


    public String removeBearer(String bearerToken) {
        return bearerToken.replaceFirst("Bearer ", "");     //여러 개 중 가장 첫 번째 것만 가져옴

    }


    /*
    * Claims 만 뽑는 메소드
    *Claims : JWT 의 Payload 영역 -> 사용자 정보, 만료일자 등 담겨있음
    * JwtException : 토큰 잘못 되었을 경우 (위변조, 만료 등) 발생하는 예외
    * */
    public Claims getClaims(String token) throws JwtException {
        JwtParserBuilder jwtParserBuilder = Jwts.parser();
        //Jwts.parser() 는 JwtParserBuilder 객체를 반환 - 정보를 가져오는 기능
        //JWT 파서를 구성할 수 있는 빌더 (parser 설정 작업을 체이닝으로 가능하게 함)
        jwtParserBuilder.setSigningKey(KEY);
        //generateToken 시 KEY 를 그대로 넣음 - 토큰의 서명을 검증하기 위해 비밀키 설정
        JwtParser jwtParser = jwtParserBuilder.build();
        //설정이 완료된 파서를 빌드해서 최종 JWTparser 객체 생성
        return jwtParser.parseClaimsJws(token).getBody();   //순수 Claims JWT 를 파싱
    }


}
