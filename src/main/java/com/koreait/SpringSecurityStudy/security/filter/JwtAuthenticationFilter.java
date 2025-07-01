package com.koreait.SpringSecurityStudy.security.filter;

import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import com.koreait.SpringSecurityStudy.security.model.PrincipalUser;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

//빈 등록되어 Spring Security 필터 체인에 추가 가능.
@Component   //AutoWired 하기 위함 - Bean 객체 등록해야 ioc 컨테이너에 등록
public class JwtAuthenticationFilter implements Filter {    //Filter 인터페이스 구현

    @Autowired
    private JwtUtil jwtUtil;   //JwtUtil: 토큰 파싱, 검증, Bearer 제거 등의 유틸리티

    @Autowired
    private UserRepository userRepository;    //UserRepository: 토큰에서 추출한 userId로 사용자 조회


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        //ServletRequest를 HttpServletRequest로 다운캐스팅하여 HTTP 메서드, 헤더 사용 가능
        //request 안에 메소드가 있을 것
        List<String> methods = List.of("POST", "PUT", "GET", "PATCH", "DELETE");
        //해당 메소드가 아니면 그냥 다음 필터로 넘김 -> 위에 포함된 메서드만 JWT 인증 처리
        //ex) OPTIONS 요청 등은 필터 적용 안 하고 바로 통과
        if (!methods.contains(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);    //filterChain.doFilter 다음 필터로 넘김
            return;
        }

        // "Authorization" 헤더에서 "Bearer " 접두사 확인 후, 순수 토큰만 추출하는 로직
        String authorization = request.getHeader("Authorization");
        System.out.println("Bearer 토큰 : " + authorization);     //Bearer abweftwgg 이런 형식으로 옴
        if (jwtUtil.isBearer(authorization)) {
            String accessToken = jwtUtil.removeBearer(authorization);   //Bearer접두사 제거하고 토큰만 남김

            //Claimms 추출 및 검증
            try {
                Claims claims = jwtUtil.getClaims(accessToken);
                //토큰에서 Claims 를 추출 - 이때 서명검증 + 만료 시간 확인도 같이 진행
                //서명 위조나 만료 시 -> GetClaims() 실패 시 예외 발생시키는 예외처리
                String id = claims.getId();      //
                //Claims 객체는 토큰 안의 sub, id, exp 등 정보 포함  -> 아이디를 Claims 에서 빼옴

                /*
                * 여기서부터 UserDetailsService 역할 시작
                *사용자 DB 조회 및 인증 객체 생성
                *토큰에서 꺼낸 id를 이용해 DB에서 사용자 조회
                *사용자 있으면 → PrincipalUser로 인증 객체 생성
                *없으면 → 인증 실패 예외 발생
                *
                *
                * */

                Integer userId = Integer.parseInt(id);       //String id 를 int userId 로 변환
                Optional<User> optionalUser = userRepository.getUserByUserId(userId);
                optionalUser.ifPresentOrElse((user) -> {
                    //DB 에서 조회된 User 객체를 Spring Security 인증 객체 (PrincipalUser)로
                    // 인증 객체 만들기


                    //UserDetails
                    //인증 객체 생성 및 저장
                    //PrincipalUser는 UserDetails 구현 클래스 (Spring Security에서 사용자 정보를 담는 객체)
                    PrincipalUser principalUser = PrincipalUser.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .userRoles(user.getUserRoles())
                            .build();
                    //UsernamePasswordAuthenticationToken 직접 생성
                    //UsernamePasswordAuthenticationToken: 인증된 사용자로 설정
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principalUser, "", principalUser.getAuthorities());
                    //이미 인증 완료된 것이므로 비밀번호 부분 비워둠
                    //spring security 의 인증 컨텍스트에 인증 객체 저장 => 이후 요청은 인증된 사용자로 간주됨
                    //SecurityContextHolder: 현재 요청 쓰레드에 인증 정보 저장 → 이후 인증된 사용자로 인식됨
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("인증 완료");
                    System.out.println(authentication.getName());
                }, () -> {
                    //사용자 없으면 인증실패 예외 발생
                    throw new AuthenticationServiceException("인증 실패 : 사용자 없음");
                });

            } catch (RuntimeException e) {
                e.printStackTrace();
            }

        }


        System.out.println("전처리완료");   //전처리 => g인증 작업은 filterChain.doFilter() 이전
        filterChain.doFilter(servletRequest, servletResponse);
        //filterChain.dofilter 기준 -> 전에 있으면 전처리 / 후에 있으면 후처리
        //필터 체인 계속 실행 -인증이든 실패든 항상 실행
        //인증에 성공하면 이후 컨트롤러에서 @AuthenticationPrincipal, SecurityContextHolder 사용 가능
        //request : 요청 보낸값, response 전처리 완료된 요청(다음 필터로 갈 요청)
        System.out.println("후처리");
    }
}
