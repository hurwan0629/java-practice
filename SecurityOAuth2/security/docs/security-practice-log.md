# Spring Security Practice Log

## 목적

이 문서는 현재 프로젝트에서 Spring Security 인증/인가 기능을 단계별로 연습하면서 기록을 이어가기 위한 작업 로그다.
각 단계에서 무엇을 했는지, 어떤 개념을 질문했고 어떻게 이해했는지, 다음에 무엇을 해야 하는지 계속 누적한다.

## 프로젝트 기준

- 프로젝트 경로: `C:\HUR\Documents\java-practice\SecurityOAuth2\security`
- 주요 설정 파일: `src/main/java/com/test/security/config/SecurityConfig.java`
- 회원 엔티티: `src/main/java/com/test/security/entity/Member.java`
- 회원 저장소: `src/main/java/com/test/security/repository/MemberRepository.java`
- 로그인 화면: `src/main/webapp/WEB-INF/views/security/login-custom.jsp`
- 현재 방식: 연습 목적상 `@Bean` 중심으로 Security 관련 객체를 등록

## 연습 목표

- [x] 2. `UserDetailsService` 빈 추가
- [x] 3. `PasswordEncoder` 빈 추가
- [x] 4. 테스트용 `Member` 데이터 준비
- [ ] 5. `successHandler` / `failureHandler` 추가
- [ ] 6. `authenticationEntryPoint` 추가
- [ ] 7. `accessDeniedHandler` 추가
- [ ] 8. `logoutSuccessHandler` 추가
- [ ] 9. URL별 role 제한 추가

## 현재 완료된 작업

### 2. UserDetailsService 빈 추가

현재 `SecurityConfig`에는 `UserDetailsService` 구현 방식이 2개 작성되어 있다.

- `userDetailsServiceFunctional`: 활성화됨
- `userDetailsService`: `@Bean`이 주석 처리되어 비활성화됨

활성화된 방식은 람다를 이용한 함수형 구현이다.

```java
@Bean
public UserDetailsService userDetailsServiceFunctional(MemberRepository memberRepository) {
    return username -> memberRepository.findByUsername(username)
            .map(member -> User.withUsername(member.getUsername())
                    .password(member.getPassword())
                    .roles(member.getRole())
                    .build())
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다: " + username));
}
```

핵심 이해:

- `UserDetailsService`는 메서드가 하나뿐인 함수형 인터페이스다.
- 실제 메서드는 `loadUserByUsername(String username)`이다.
- 그래서 람다로 구현할 수 있다.
- `User.withUsername(...)`은 Spring Security가 제공하는 기본 `UserDetails` 구현체를 만드는 방식이다.
- 나중에 도메인 정보를 더 많이 쓰고 싶으면 직접 `CustomUserDetails implements UserDetails`를 만들 수 있다.

### 3. PasswordEncoder 빈 추가

현재 `BCryptPasswordEncoder`가 빈으로 등록되어 있다.

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

핵심 이해:

- 로그인 시 사용자가 입력한 비밀번호와 DB에 저장된 암호화 비밀번호를 비교할 때 사용된다.
- 테스트 데이터를 저장할 때도 `passwordEncoder.encode(...)`로 암호화해서 저장해야 한다.

### 4. 테스트용 Member 데이터 준비

`CommandLineRunner`를 이용해서 앱 실행 직후 테스트 회원을 자동 생성하도록 작성했다.

현재 테스트 계정:

| username | password | role |
| --- | --- | --- |
| `basic` | `basic1234` | `Basic` |
| `advanced` | `advanced1234` | `Advanced` |
| `pro` | `pro1234` | `Pro` |
| `ultimate` | `ultimate1234` | `Ultimate` |

현재 방식:

```java
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
        // advanced, pro, ultimate도 같은 방식
    };
}
```

`Member` 생성자 버그도 수정됨:

```java
@Builder
public Member(String username, String password, String role) {
    this.username = username;
    this.password = password;
    this.role = role;
}
```

## 질문과 답변 요약

### Q1. `@Bean` 2개 중 하나를 비활성화하려면?

답변:

- 가장 단순한 방법은 `@Bean`만 주석 처리하는 것이다.
- 메서드 전체를 주석 처리해도 된다.
- 나중에는 `@Profile`을 써서 환경별로 빈을 다르게 활성화할 수 있다.

예시:

```java
// @Bean
public UserDetailsService userDetailsService(MemberRepository memberRepository) {
    ...
}
```

주의:

- 같은 타입의 빈이 2개 활성화되면 Spring이 어떤 빈을 써야 할지 모호해질 수 있다.
- 현재는 `UserDetailsService` 빈을 하나만 활성화하는 상태가 적절하다.

### Q2. `UserDetailsService`가 람다라서 이전에 봤던 방식과 다르게 느껴진다

답변:

람다 방식은 아래 익명 클래스 방식과 같은 의미다.

```java
return new UserDetailsService() {
    @Override
    public UserDetails loadUserByUsername(String username) {
        ...
    }
};
```

차이는 표현 방식뿐이다.

- 람다 방식: 짧고 간결함
- 익명 클래스 방식: 메서드 구조가 눈에 보여서 학습 초기에 이해하기 쉬움
- 직접 만든 `CustomUserDetails` 방식: 내 도메인 객체인 `Member`를 더 풍부하게 다룰 수 있음

정리:

```text
Member = 내 DB 테이블 엔티티
UserDetails = Spring Security가 이해하는 인증 사용자 규격
UserDetailsService = username으로 UserDetails를 찾아주는 서비스
```

### Q3. `CommandLineRunner`는 그냥 람다 실행해주는 것인가? `psvm` 같은 것인가?

답변:

`CommandLineRunner`는 Spring Boot 애플리케이션이 뜬 직후 실행되는 콜백 인터페이스다.

```java
@FunctionalInterface
public interface CommandLineRunner {
    void run(String... args) throws Exception;
}
```

`main()`과 비교:

- `main()`: JVM이 제일 먼저 실행하는 진짜 시작점
- `CommandLineRunner`: Spring 컨테이너가 준비된 뒤 실행되는 초기화 코드

그래서 `MemberRepository`, `PasswordEncoder` 같은 Spring Bean을 주입받아 사용할 수 있다.

주의:

- 앱이 실행될 때마다 실행된다.
- 테스트 데이터 생성 시 중복 체크가 필요하다.

## 나중에 패키지와 클래스를 나누는 기준

현재는 연습 목적상 `SecurityConfig` 안에 여러 빈을 모아두고 있다.
나중에는 다음처럼 나누는 것이 좋다.

```text
config
- SecurityConfig
- PasswordConfig 또는 AppConfig
- TestDataConfig

security
- CustomUserDetails
- CustomUserDetailsService
- handler
  - LoginSuccessHandler
  - LoginFailureHandler
  - CustomAccessDeniedHandler
  - CustomAuthenticationEntryPoint

entity
- Member

repository
- MemberRepository
```

분리 기준:

- `SecurityConfig`: 보안 정책 조립
- `PasswordEncoder`, 테스트 데이터: 별도 설정 빈으로 분리 가능
- `UserDetailsService`: 인증 사용자 조회 로직이 커지면 별도 클래스로 분리
- handler류: 성공, 실패, 접근 거부, 인증 필요 응답처럼 실제 동작 로직이므로 별도 클래스로 분리

## 다음에 이어서 할 작업

### 5. successHandler / failureHandler 추가

다음 세션에서 진행할 내용:

- 기존 `defaultSuccessUrl("/login-success")` 대신 `successHandler(...)` 추가
- 기존 `failureUrl("/login-fail")` 대신 `failureHandler(...)` 추가
- 처음에는 `SecurityConfig` 안에 `@Bean`으로 등록해서 연습
- 핸들러 내부에서 로그 출력 또는 redirect 처리 연습

예상 진행 방향:

```java
@Bean
public AuthenticationSuccessHandler authenticationSuccessHandler() {
    return (request, response, authentication) -> {
        response.sendRedirect("/login-success");
    };
}
```

```java
@Bean
public AuthenticationFailureHandler authenticationFailureHandler() {
    return (request, response, exception) -> {
        response.sendRedirect("/login-fail");
    };
}
```

이후 `formLogin` 설정에서 연결한다.

```java
.formLogin(form -> form
        .loginPage("/login-custom")
        .loginProcessingUrl("/login-execute")
        .successHandler(authenticationSuccessHandler())
        .failureHandler(authenticationFailureHandler())
        .usernameParameter("id")
        .passwordParameter("pw")
        .permitAll()
)
```

## 확인할 점

- 현재 `SecurityConfig.java`의 일부 한글 주석이 인코딩 문제로 깨져 있다.
- 기능 연습에는 큰 문제는 없지만, 나중에 정리할 때 주석을 다시 작성하는 것이 좋다.
- `Member.changeRole()`은 현재 문자열 비교를 직접 하고 있다. 나중에는 enum으로 바꾸는 것이 더 안전하다.
- role 값이 `Basic`, `Advanced`, `Pro`, `Ultimate`처럼 저장되어 있고, `roles(member.getRole())`를 쓰면 실제 권한은 `ROLE_Basic` 형태가 된다.
- URL 제한에서는 `hasRole("Basic")`처럼 사용하면 된다.
