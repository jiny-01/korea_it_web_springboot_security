package com.koreait.SpringSecurityStudy.security.Handler;

import com.koreait.SpringSecurityStudy.entity.OAuth2User;
import com.koreait.SpringSecurityStudy.entity.User;
import com.koreait.SpringSecurityStudy.repository.OAuth2UserRepositoy;
import com.koreait.SpringSecurityStudy.repository.UserRepository;
import com.koreait.SpringSecurityStudy.security.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;


//사용자 정보까지 파싱완료했을 떄 그 정보를 처리하는 부분
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private OAuth2UserRepositoy oAuth2UserRepositoy;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //OAuth2User 정보 가져오기

        DefaultOAuth2User defaultOAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        //OAuth2User 가 context holder 안에 들어가는 인증객체가 될 것 -> getPrincipal 로 받아옴

        String provider = defaultOAuth2User.getAttribute("provider");
        String providerUserId = defaultOAuth2User.getAttribute("id");
        String email = defaultOAuth2User.getAttribute("email");

        //provider, providerUserId 이미 연동된 사용자 정보가 있는지 DB 조회
        //OAuthUser - Entity 거 가져오기
        OAuth2User oAuth2User = oAuth2UserRepositoy.getOAuth2UserByProviderAndProviderUserId(provider, providerUserId);

        //OAuth2 로그인을 통해 회원가입이 되어있지 않거나 아직 연동되지 않은 상태
        if(oAuth2User == null) {
            //프론트(Web)로 provider 와 providerUserId 전달
            response.sendRedirect
                    ("http://localhost:3000/auth/oauth2?provider=" + provider + "&providerUserId=" + providerUserId + "&email=" + email);  //리액트에서 보냄(3000)
            //연동된 적이 없는데 어떻게 provider 가 id, email 정보를 가지고 있는지??
            return;
        }

        //연동된 사용자가 있다면? => userId를 통해 회원 정보 조회
        Optional<User> optionalUser = userRepository.getUserByUserId(oAuth2User.getUserId());

        //OAuth2 로그인을 통해 회원가입이나 연동을 진행한 경우
        String accessToken = null;
        if(optionalUser.isPresent()) {
            accessToken = jwtUtil.generateAccessToken(Integer.toString(optionalUser.get().getUserId()));
        }

        //최종적으로 accessToken 을 쿼리 파라미터로 프론트에 전달
        response.sendRedirect("htt://localhost:3000/auth/oauth2/signin?accessToken=" + accessToken);
        //프론트에서 이 토큰을 웹 local storage 에 넣음

        //SecurityConfig 로 감

    }
}
