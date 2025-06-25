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

@Component   //AutoWired 하기 위함 - Bean 객체 등록해야 ioc 컨테이너에 등록
public class JwtAuthenticationFilter implements Filter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;   //캐스팅
        //request 안에 메소드가 있을 것
        List<String> methods = List.of("POST", "PUT", "GET", "PATCH", "DELETE");
        //해당 메소드가 아니면 그냥 다음 필터로 넘김
        if (!methods.contains(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);    //filterChain.doFilter 다음 필터로 넘김
            return;
        }
        String authorization = request.getHeader("Authorization");
        System.out.println("Bearer 토큰 : " + authorization);     //Bearer abweftwgg 이런 형식으로 옴
        if (jwtUtil.isBearer(authorization)) {
            String accessToken = jwtUtil.removeBearer(authorization);   //Bearer접두사 제거하고 토큰만 남김
            try {
                Claims claims = jwtUtil.getClaims(accessToken);
                //토큰에서 Claims 를 추출 - 이때 서명검증도 같이 진행
                //서명 위조나 만료 시 예외 발생
                String id = claims.getId();      //아이디를 Claims 에서 빼옴

                //여기서부터 UserDetailsService 역할 시작
                Integer userId = Integer.parseInt(id);       //String id 를 int userId 로 변환
                Optional<User> optionalUser = userRepository.getUserByUserId(userId);
                optionalUser.ifPresentOrElse((user) -> {
                    //DB 에서 조회된 User 객체를 Spring Security 인증 객체 (PrincipalUser)로
                    //UserDetails
                    PrincipalUser principalUser = PrincipalUser.builder()
                            .userId(user.getUserId())
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .email(user.getEmail())
                            .build();
                    //UsernamePasswordAuthenticationToken 직접 생성
                    Authentication authentication = new UsernamePasswordAuthenticationToken(principalUser, "", principalUser.getAuthorities());
                    //이미 인증 완료된 것이므로 비밀번호 부분 비워둠
                    //spring security 의 인증 컨텍스트에 인증 객체 저장 => 이후 요청은 인증된 사용자로 간주됨
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


        System.out.println("전처리완료");
        filterChain.doFilter(servletRequest, servletResponse);
        //filterChain.dofilter 기준 -> 전에 있으면 전처리 / 후에 있으면 후처리
        //request : 요청 보낸값, response 전처리 완료된 요청(다음 필터로 갈 요청)
        System.out.println("후처리");
    }
}
