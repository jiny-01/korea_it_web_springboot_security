package com.koreait.SpringSecurityStudy.security.filter;

import jakarta.servlet.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component   //AutoWired 하기 위함 - Bean 객체 등록해야 ioc 컨테이너에 등록
public class JwtAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("전처리");
        filterChain.doFilter(servletRequest, servletResponse);
        //filterchain.dofilter 기준 -> 전에 있으면 전처리 / 후에 있으면 후처리
        //request : 요청 보낸값, response 전처리 완료된 요청(다음 필터로 갈 요청)
        System.out.println("후처리");
    }
}
