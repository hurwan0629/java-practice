package com.example.demo.interceptor;

import com.example.demo.exception.BusinessException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.service.JwtService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Map;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final String[] passing ={
            "POST /member/login",
            "POST /member/register",
            "GET /member/check-id",
            "GET /post/max-page",
            "GET /post/{var}",
            "GET /post/all",
            "GET /test"
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

        if ("OPTIONS".equals(request.getMethod())) {
            return true;
        }

        if (isRequestIncludedIn(this.passing, request.getMethod(), uri)) {
            return true;
        }
        if (isRequestIncludedIn(this.forbidden, request.getMethod(), uri)) {
            throw new BusinessException(ErrorCode.FORBIDDEN_REQUEST, Map.of(
                    "resource", "HTTP_REQUEST",
                    "action", "auth",
                    "reason", "인증이 필요한 작업입니다."
            ));
        }

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_REQUIRED, Map.of(
                    "resource", "HTTP_REQUEST",
                    "action", "auth",
                    "reason", "인증이 필요한 작업입니다."
            ));
        }

        String token = Arrays.stream(cookies)
                .filter(cookie -> "token".equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        if(token == null) {
            throw new BusinessException(ErrorCode.AUTHORIZATION_REQUIRED, Map.of(
                    "resource", "HTTP_REQUEST",
                    "action", "auth",
                    "reason", "인증이 필요한 작업입니다."
            ));
        }

        try {
            String memberPk = jwtService.getMemberPk(token);
            request.setAttribute("memberPk", Long.parseLong(memberPk));
            return true;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FORBIDDEN_REQUEST, Map.of(
                    "resource", "JwtToken",
                    "action", "token",
                    "reason", "유효하지 않은 토큰입니다."
            ));
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
