package com.koreait.SpringSecurityStudy.security.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component   //AutoWired 하기 위함 - Bean 객체 등록해야 ioc 컨테이너에 등록
public class JwtAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;   //캐스팅
        //request 안에 메소드가 있을 것
        List<String> methods = List.of("POST", "PUT", "Get", "PATCH", "DELETE");
       //해당 메소드가 아니면 그냥 다음 필터로 넘김
        if (!methods.contains(request.getMethod())) {
            filterChain.doFilter(servletRequest, servletResponse);    //filterChain.doFilter 다음 필터로 넘김
        }

        System.out.println("전처리");
        filterChain.doFilter(servletRequest, servletResponse);
        //filterchain.dofilter 기준 -> 전에 있으면 전처리 / 후에 있으면 후처리
        //request : 요청 보낸값, response 전처리 완료된 요청(다음 필터로 갈 요청)
        System.out.println("후처리");
    }
}
