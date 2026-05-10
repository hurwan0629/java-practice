package com.example.demo.interceptor;

import com.example.demo.exception.ForbiddenRequestException;
import com.example.demo.exception.MemberUnathorizationException;
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
            "GET /post/max-page",
            "GET /post/{var}",
            "GET /post/all",
            "OPTIONS /post/{var}"
    };

    private final String[] forbidden = {
            "GET /member/{var}"
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

        System.out.println(request.getMethod() + uri);

        if (isRequestIncludedIn(this.passing, request.getMethod(), uri)) {
            return true;
        }
        if (isRequestIncludedIn(this.forbidden, request.getMethod(), uri)) {
            throw new ForbiddenRequestException();
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new MemberUnathorizationException("로그인이 필요합니다");
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if(token == null) {
            throw new MemberUnathorizationException("토큰이 존재하지 않습니다");
        }

        try {
            String memberPk = jwtService.getMemberPk(token);
            request.setAttribute("memberPk", Long.parseLong(memberPk));
            return true;
        } catch (Exception e) {
            throw new MemberUnathorizationException("유효하지 않은 토큰입니다.");
        }
    }

    private boolean isRequestIncludedIn(
            String[] targets,
            String requestMethod,
            String requestUri) {
        for (String targetUrl: targets) {
            String[] parts = targetUrl.split(" ", 2);

            if (parts.length != 2) {
                continue;
            }

            String passMethod = parts[0];
            String passUri = parts[1];

            if (passMethod.equals(requestMethod) && matchesUri(passUri, requestUri)) {
                return true;
            }
        }

        return false;
    }

    private boolean matchesUri(String passUri, String requestUri) {
        String regex = passUri.replaceAll("\\{[^/]+}", "[^/]+");
        return requestUri.matches("^" + regex + "$");
    }
}
