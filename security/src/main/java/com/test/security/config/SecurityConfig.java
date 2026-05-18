package com.test.security.config;

import com.test.security.entity.Member;
import com.test.security.repository.MemberRepository;
import jakarta.servlet.DispatcherType;
import lombok.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf ->  csrf
                    .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                    .frameOptions(frame -> frame.sameOrigin())
            )
            .authorizeHttpRequests(auth -> auth
//                    .requestMatchers(HttpMethod.GET, "/WEB-INF/views/**.jsp").permitAll()
                    .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                    .requestMatchers(
                            HttpMethod.GET,"/", "/public", "/login-dashboard",
                            "/login-success", "/login-custom", "/login-fail").permitAll() // 루트(index)와 /public는 모든 사용자에 대해서 접근 가능하게 설정
                        .requestMatchers(HttpMethod.GET, "/basic").hasRole("Basic")
                        .requestMatchers(HttpMethod.GET, "/advanced").hasRole("Advanced")
                        .requestMatchers(HttpMethod.GET, "/pro").hasRole("Pro")
                        .requestMatchers(HttpMethod.GET, "/ultimate").hasRole("Ultimate")
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated() // 나머지 요청들은 모두 "인증된" 사용자 이여야함.
            )
            .formLogin(form -> form
                    .loginPage("/login-custom")
                    .loginProcessingUrl("/login-execute")
//                  .defaultSuccessUrl("/login-success")    // 성공 후 이동할 url 지정 가능. 2번째 인자로 true 할당 시 무조건 리다이렉트 가능
                    .successHandler(authenticationSuccessHandler()) // 성공시 무조건 실행할 로직 지정
//                  .failureUrl("/login-fail")          // 실패시 이동할 url 지정
                    .failureHandler(authenticationFailureHandler()) // 실패시 동작할 로직 지정
                    .usernameParameter("id")
                    .passwordParameter("pw")
                    .permitAll() // 폼 로그인에 대해서는 모든 여청 허가
            )
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(authenticationEntryPoint())
                    .accessDeniedHandler(accessDeniedHandler()))
            .logout(logout -> logout
                    .logoutUrl("/logout-custom")
                    .logoutSuccessUrl("/logout-success")
                    .permitAll()
            )
            .sessionManagement(session -> session
                    .maximumSessions(-1)
                    .sessionRegistry(sessionRegistry()));

        return http.build();
    }

    // 사용자 인증, 권한 체크 로직 객체 빈.
    // 함수형을 통해 작성하는 방법
    @Bean
    public UserDetailsService userDetailsServiceFunctional(MemberRepository memberRepository) {
                    // 1.  UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
                    //     을 구현하기 위한 람다를 만들어준다.
                    //     username를 받아서 repo로부터 해당 username를 찾는다.
        return username -> memberRepository.findByUsername(username)
                // 나온 결과 (0 또는 1개)에 대해서 UserDetails객체를 빌드 시작
                .map(member -> User.withUsername(member.getUsername())
                        // password 넣고
                        .password(member.getPassword())
                        // db에서 뽑은 role까지 뽑아서 UserDetails에 넣기.
                        .roles(member.getRole())
                        .build())
                // 만약에 .map()에서 empty() == EMPTY가 나온다면 throw 하기.
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));
    }
    
    // 위와 동일한 작업을 명령형으로 작성한 결과.
    // @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
        // 익명 클래스 생성하는 방식
        return new UserDetailsService() {
            @Override
            // 부모 메서드의 인자/결과가 NonNull이기 때문에 lombok.NonNull 넣어주기
            @NonNull
            public UserDetails loadUserByUsername(@NonNull String username) {
                return memberRepository.findByUsername(username)
                        .map(member -> User.withUsername(member.getUsername())
                                .password(member.getPassword())
                                .roles(member.getRole())
                                .build())
                        .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));

            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // CommandLineRunner 빈을 통해 시작과 동시에 중복되지 않는 basic, advanced, pro, ultimate 계정 생성 비번은 ([id] + [1234])
    @Bean
    public CommandLineRunner testMemberData(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (memberRepository.findByUsername("basic").isEmpty()) {
                memberRepository.save(Member.builder()
                        .username("basic")
                        .password(passwordEncoder.encode("basic1234"))
                        .role("Basic")
                        .build());
            }
            if (memberRepository.findByUsername("advanced").isEmpty()) {
                memberRepository.save(Member.builder()
                        .username("advanced")
                        .password(passwordEncoder.encode("advanced1234"))
                        .role("Advanced")
                        .build());
            }
            if (memberRepository.findByUsername("pro").isEmpty()) {
                memberRepository.save(Member.builder()
                        .username("pro")
                        .password(passwordEncoder.encode("pro1234"))
                        .role("Pro")
                        .build());
            }
            if (memberRepository.findByUsername("ultimate").isEmpty()) {
                memberRepository.save(Member.builder()
                        .username("ultimate")
                        .password(passwordEncoder.encode("ultimate1234"))
                        .role("Ultimate")
                        .build());
            }
            if (memberRepository.findByUsername("admin").isEmpty()) {
                memberRepository.save(Member.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin1234"))
                        .role("admin")
                        .build());
            }
        };
    }

    // 로그인 성공 했을 때 동작할 작업
    // defaultSuccessUrl과 다른점:
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (req, res, auth) -> {
            res.sendRedirect("/login-success");
        };
    }

    // 로그인 실패 했을 때 동작할 작업
    // failureUrl과 다른점:
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (req, res, exception) -> {
            res.sendRedirect("/login-fail");
        };
    }

    // anyRequest().authenticated() 에서 /private, /principal 같은 url에 비회원이 접근하려 할 때
    // authenticationEntryPoint(authenticationEntryPoint())가 실행되게 됨.
    // 유사한 핸들러로 AccessDeniedHandler: 인증은 되어있지만 인가되지 않은 권한의 경우 403이 나온다고 볼 수 있습니다.
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (req, res, authException) -> {
           res.sendRedirect("/login-custom");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (req, res, accessDeniedException) -> {
            res.sendRedirect("/access-denied");
        };
    }

    // 로그인 사용자를 확인하기 위한 객체
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
