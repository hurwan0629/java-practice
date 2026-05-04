package com.example.demo.config;

import com.example.demo.interceptor.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// 이 클래스는 Spring의 설정 클래스이다 라는 뜻하는 어노테이션
@Configuration
public class WebConfig {

    @Autowired
    private AuthInterceptor authInterceptor;

    // 해당 메서드 (corsConfigurer)이 반환하는 객체를 스프링이 받아서 관리한다는 뜻
    // WebMvcConfigurer 타입의 객체를 사용하게 됨
    // WebMvcConfigurer은 Spring MVC의 웹 설정을 추가로 바꿀 때 쓰는 인터페이스
    @Bean
    public WebMvcConfigurer corsConfigurer() {
            // 자바의 익명크래스 anonymous class 문법으로
            // WebConfigurer은 원래 인터페이스이기 때문에 바로 생성이 불가능 하지만 이 내부의 메서드를
            // 구현함으로 써 인터페이스 구현체를 반환하는 방식
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/**")
                        .allowedOrigins("https://hoppscotch.io",
                                        "http://localhost:3000")
                        .allowCredentials(true)
                        .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
                        .allowedHeaders("*");
            }

            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(authInterceptor)
                        .addPathPatterns("/**")
                        .excludePathPatterns(
                                "/member/login",
                                "/member/register",
                                "/member/check-id",
                                "/test"
                        );
            }
        };
    }
}
