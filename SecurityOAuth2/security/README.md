해당 프로젝트는 Ornably 작업을 했을 때 사용하였던 Spring Security + OAuth2 의 구조를 복습하기 위해서 만들었습니다.
프론트 또는 cors 공부를 계획하진 않았기 때문에 우선 .jsp 를 통해서 작업을 할 생각입니다.

# 2026-05-18
현재 시큐리티 기능과 함께 로그인 과정에 DB와 연동하여 회원을 검사하기 위해 H2 Database와 JPA 의존성을 받았습니다.

현재 프로젝트는 Maven 프로젝트이며
받은 의존성은
### org.springframework.boot
- spring-boot-starter-security
- spring-boot-starter-web
- spring-boot-starter-oauth2-client: oauth2 사용
- spring-boot-starter-data-jpa
- spring-boot-h2console
### com.h2databases
- h2: runtime
### org.apache.tomcat.embed
- tomcat-embed-jasper: jsp 사용
### jakarta.servlet.jsp.jstl
- jakarta.servlet.jsp.jstl-api
### org.glassfish.web
- jakarta.servlet.jsp.jstl
와 같습니다.

일단 SecurityFilterChain에서 HttpSecurity의 설정으로 csrf에 `h2/console/**`에 대한 요청을 무시하게 하였습니다.
Spring Security는 기본적으로 GET을 제외한 대부분의 요청에 CSRF토큰 (우리 서버가 허락한 클라이언트가 맞니)을 요구합니다.
이때 임시로 사용하는 h2콘솔에 대해서 임시로 CSRF 검사를 제외하기 위해서 작성하였습니다.

두번째로 `headers.frameOptions(frame -> frame.sameOrigin()`의 경우에는 
스프링이 기본적으로 클릭재킹 공격을 막기 위해서 `<iframe>`안에서 열리는 것을 제한하지만 H2 콘솔은 내부적으로 iframe을 사용하기 때문에 SameOrigin의 경우에는 iframe을 제한하지 않겟다는 의미입니다.

`controller/TestController`을 확인해보면 `GET /`에 대해서 `isUserLoggedIn`을 반환하는 것을 확인할 수 있습니다.
처음에는 `SecurityContextHolder.getContext().getAuthentication() != null`을 통해 `isUserLoggedIn`을 체크하려 하였지만 스프링 시큐리티가 기본적으로 게스트에게도 `AnonymousAuthenticationToken`을 주어 항상 `true`가 나오는 문제가 있었습니다.
따라서 이후 결과 값에서 `.isAuthenticated()`와 `not instanceof AnonymousAuthenticationToken`인지를 확인하여 값을 반환하게 하였습니다.

로그인 과정 문제 중에는 `/login-custom`을 Anonymous 회원이 가서 로그인을 시도할 시 `defaultSuccessUrl`이 `/login-success`임에도 불구하고 fallback-error이 나오는 문제가 있었습니다.
이것은 스프링 시큐리티가 id/pw를 통한 로그인을 가로체어 사용자를 조회하며 비밀번호를 검증 후 이전에 이동하려했던 경로로 이동시키기 때문이다. 
예를 들어 `defaultSuccessUrl("/login-success")`일 때 `/private`로 이동하려 할 때 로그인 요구에 의해 로그인을 하면 `/login-success`가 아닌 `/private`로 이동한다.
이때 내 `/private`는 **ViewResolver**에 의해 `private - 로그인 필요.jsp`같은 url을 요청하여 에러가 났었었다.

# Spring Security의 핸들러들
Spring Security의 핸들러는 보통 특정 보안 이벤트가 발생했을 때 후속 동작을 결정하는 객체입니디ㅏ.
보통
- 로그인 성공
- 로그인 실패
- 인증필요
- 권한 없음
- 로그아웃 성공
같은 지점마다의 핸들러가 있습니다.

순서대로 보면
1. Security Filter Chain: 모든 요청에 대해서 Controller 전에 받아서 처리
2. authorizeHttpRequest 설정을 확인함
    - 인증이 필요없는 경우: Controller로 이동
    - 필요한 경우 로그인 상태를 확인
      - 로그인 안되었을 때: AuthenticationENtryPoint 동작 (보통 `/login`으로 redirect)
      - 로그인 되어있을 때: 권한 확인(authorities)후 403 또는 Controller

이제 위와같은 설정들을 직접 구현해보며 체험을 해보려 합니다.

# Spring Security 핸들러 구현 실습
`15:21`
이것저것 (작업하다 운동이나 등등 하고 왔습니다.)하다 기초 지식을 보충하고 싶다는 생각이 들었습니다.
### 지금까지 한 것
`@Bean`을 만들었습니다.
- UserDetailsService
- PasswordEncoder
- CommandLineRunner (샘플데이터)
- AuthenticationSuccessHandler (req, res, auth를 인자로 받는 람다)
- AuthenticationFailureHandler (동일한 로그인 실패 람다)
와 궁금한것들 등을 알아보았습니다.

이때 나중에 공부하고자 하는 것은 순수 Servlet(리스너와 필터도)를 안한지 좀 시간이 된 것 같기도 하고
Maven/Gradle의 빌드 과정에 대해서 조금 더 알아보기,
의존성 group와 artifact, scope, 테스트 등에 대해서 이것저것 알아보아야 할 것 같습니다.
말 그대로 framework에 대해서 알아보려 합니다.

추가로 순수 자바의 Optional, 함수형 프로그래밍, Exception 및 오류, 인터페이스/추상클래스, 익명함수, 비동기 등에 대해서도 실습할 예정입니다.


