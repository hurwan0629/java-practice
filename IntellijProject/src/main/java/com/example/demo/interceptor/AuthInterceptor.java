package com.example.demo.interceptor;

import com.example.demo.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Arrays;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final String[] passing ={
            "/post/max-page"
    };

    @Autowired
    private JwtService jwtService;

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if(uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }

        for (String passUrl: passing) {
            if(request.getRequestURL().equals(passUrl)) {
                return true;
            }
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            unauthorized(response, "로그인이 필요합니다");
            return false;
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if(token == null) {
            unauthorized(response, "토큰이 존재하지 않습니다");
            return false;
        }

        try {
            String memberPk = jwtService.getMemberPk(token);
            request.setAttribute("memberPk", Long.parseLong(memberPk));
            return true;
        } catch (Exception e) {
            unauthorized(response, "유효하지 않은 토큰입니다.");
            return false;
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
