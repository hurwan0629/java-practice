package com.test.security.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers(HttpMethod.GET, "/WEB-INF/views/**.jsp").permitAll()
                    .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                    .requestMatchers(
                            HttpMethod.GET,"/", "/public", "/private",
                            "/login-success", "/login-custom", "/login-fail").permitAll() // 루트(index)와 /public는 모든 사용자에 대해서 접근 가능하게 설정
                    .anyRequest().authenticated() // 나머지 요청들은 모두 "인증된" 사용자 이여야함.
            )
            .formLogin(form -> form
                    .loginPage("/login-custom")
                    .loginProcessingUrl("/login-execute")
                    .defaultSuccessUrl("/login-success")
                    .failureUrl("/login-fail")
                    .usernameParameter("id")
                    .passwordParameter("pw")
                    .permitAll() // 폼 로그인에 대해서는 모든 여청 허가
            )
            .logout(logout -> logout
                    .logoutUrl("/logout-custom")
                    .logoutSuccessUrl("/logout-success")
                    .permitAll()
            );

        return http.build();
    }
}
