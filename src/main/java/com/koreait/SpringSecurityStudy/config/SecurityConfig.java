package com.koreait.SpringSecurityStudy.config;

import com.koreait.SpringSecurityStudy.security.Handler.OAuth2SuccessHandler;
import com.koreait.SpringSecurityStudy.security.filter.JwtAuthenticationFilter;
import com.koreait.SpringSecurityStudy.service.OAuth2PrincipalUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration   //Spring 설정 클래스임을 나타냄 → 빈으로 등록되어 동작
public class SecurityConfig {

    @Autowired         //의존성 주입 - JWT 필터
    private JwtAuthenticationFilter jwtAuthenticationFilter;   //만든 필터 가져옴

    @Autowired
    private OAuth2PrincipalUserService oAuth2PrincipalUserService;

    @Autowired
    private OAuth2SuccessHandler oAuth2ScuccessHandler;

    //비밀번호 암호화용 Bean 생성 - BC 인코더
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*BCrypt 인코더
    * 비밀번호를 안전하게 암호화(해싱)하고, 검증하는 역할
    * 단방향 해시, 복호화 불가능
    *
    *
    * */


    /*
    * corsConfigurationSource() 설정은 spring security 에서
    * CORS (Cross-Origin Resource Sharing)를 처리하기 위한 설정
    * CORS - 브라우저가 보안상 다른 도메인의 리소스 요청을 제한하는 정책 => 보안정책
    * 다른 도메인에서 백엔드 API를 호출할 때 보안 제약에 걸리는 것을 방지하는 설정.
    * 기본적으로 브라우저는 같은 출처(Same-Origin) 만 허용 (포트 다르면 다른 출처)
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
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL);  //모든 HTTP메서드 허용

        // 요청 URL (/user/get)에 대한 CORS 설정 적용을 위해 객체 생성
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 URL(/**) 에 대해 위에서 설정한 CORS 정책을 적용하겠단 의미
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    // SecurityFilterChain 설정 커스터마이징- 들어오는 요청은 이 필터를 거칠 것임
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());    //위에서 만든 CORS 설정을 security 에 적용
        http.csrf(csrf -> csrf.disable());  //CSRF 보호 비활성화 (JWT는 필요 없음)
        //CSRF 란
        //사용자가 의도하지 않은 요청을 공격자가 유도해서 서버에 전달하도록 하는 공격
        //JWT 방식 또는 무상태(Stateless) 인증이기 떄문에
        //세션이 없고, 쿠키도 안 쓰고, 토큰 기반이기 때문에 CSRF 공격 자체가 성립되지 않음


        //-----CORS, CSRF, 로그인, 세션 설정---------
        //서버 사이드 렌더링 로그인 방식 비활성화 - 기본 로그인 페이지 비활성화
        http.formLogin(formLogin -> formLogin.disable());
        //HTTP 프로토콜 기본 로그인 방식 비활성화 - 브라우저 팝업 로그인 비활성화
        http.httpBasic(httpBasic -> httpBasic.disable());
        //서버 사이드 렌더링 로그아웃 비활성화 - 로그아웃 기능 비활성화
        http.logout(logout -> logout.disable());

        // session creation 꺼둔다 => 세션 저장 안 함 (JWT는 Stateless 인증 방식)
        http.sessionManagement
                (Session -> Session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        //UsernamePasswordAuthenticationFilter 보다 먼저 우리가 만든 JwtAuthenticationFilter를 실행
        //username~~ (작동 안함- formlogin 에서 비활성화) 필터로 가기 전에 jwt필터를 끼운 것


        //특정 요청 URL 에 대한 권한 설정 - (로그인, 회원가입은 막으면 안됨)
        http.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/auth/test").hasRole("ADMIN"); //test 요청 보내려면 ADMIN 권한을 가진 사람만
            //권한을 ROLE_ADMIN ROLE_USER 처럼 ROLE_ 형식으로 저장했다면 -> hasRole("ADMIN')
            //권한을 그냥 ADMIN, USER 이렇게 저장했다면 -> hasAuthority("ADMIN") 을 사용
            auth.requestMatchers(
                    "/auth/signup",
                    "/auth/signin",
                    "/oauth2/**",
                    "/login/oauth2/**").permitAll();   //인증없이 접근허용할 요청 URL
            auth.anyRequest().authenticated();  //	그 외 모든 URL- 인증 필요(토큰 필요)
            //principal 해당함 - Bearer 토큰 있어야함
        });

        //요청이 들어오면 Spring Security 의 filterChain 을 탄다
        //여기서 여러 필터 중 하나가 OAuth2 요청을 감지
        //감지되면 해당 provider 의 로그인 페이지로 리디렉션함
        http.oauth2Login(oauth2 -> oauth2.
                //사용자 정보 요청이 완료가 되면 이 커스텀 서비스로 OAuth2User 를 처리하겠다고 설정
                userInfoEndpoint(userInfo ->
                //Oauth2 인증이 최종적으로 성공한 후 (사용자 정보 파싱 완료 후) 실행할 핸들러 설정
                        userInfo.userService(oAuth2PrincipalUserService))
                //OAuth2 로그인 요청이 성공하고 사용자 정보를 가져오는 과정 설정
                .successHandler(oAuth2ScuccessHandler)
        );



        return http.build();
    }













}
