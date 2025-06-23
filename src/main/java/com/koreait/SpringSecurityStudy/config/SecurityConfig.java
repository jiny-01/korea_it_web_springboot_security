package com.koreait.SpringSecurityStudy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration   //설정파일에 달아야하는 어노테이션
public class SecurityConfig {

    //
    /*
    * corsConfigurationSource() 설정은 spring security 에서
    * CORS (Cross-Origin Resource Sharing)를 처리하기 위한 설정
    * CORS - 브라우저가 보안상 다른 도메인의 리소스 요청을 제한하는 정책 => 보안정책
    * 기본적으로 브라우저는 같은 출처(Same-Origin) 만 허용 (포트 다르면 다른 출처)
    *
    *
    *
    * */

    //CORS 설정
    @Bean     //객체 생성 시 필요
    public CorsConfigurationSource configurationSource() {

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //1) 요청을 보내는 쪽의 ALL(모든)도메인(사이트 주소) 를 허용하겠다는 의미
        corsConfiguration.addAllowedOriginPattern(CorsConfiguration.ALL);
        //2) 요청을 보내는 쪽에서 Request, Response Header 정보에 대한 제약을 허용
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL);
        //3) 요청을 보내는 쪽의 메소드 (GET, POST, PUT, DELETE, OPTION 등)
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);

        // 요청 URL (/user/get)에 대한 CORS 설정 적용을 위해 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 URL(/**) 에 대해 위에서 설정한 CORS 정책을 적용하겠단 의미
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    // SecurityFilterChain 설정 - 들어오는 요청은 이 필터를 거칠 것임
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());    //위에서 만든 CORS 설정을 security 에 적용

    }












}
