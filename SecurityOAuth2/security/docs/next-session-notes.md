# Next Session Notes
session code:
`codex resume 019e3983-7b37-74f3-9952-2b4000870074`
## How to continue

This project is being used as a Spring Security practice session.

Important workflow rule:

- The user writes the code directly.
- Codex should not edit application code unless the user explicitly asks for it.
- Codex should read the code, verify compile/structure/flow, explain concepts, and suggest the next small step.
- When the user says "했어", inspect the relevant files and run `.\mvnw.cmd compile`.
- Do not treat `.\mvnw.cmd test` failure as a blocker unless the user asks about tests. Current test failure is from missing test dependencies such as JUnit/SpringBootTest, not from the security code.

## Current project state

Main file:

- `src/main/java/com/test/security/config/SecurityConfig.java`

Related files:

- `src/main/java/com/test/security/controller/SecurityController.java`
- `src/main/java/com/test/security/controller/TestController.java`
- `src/main/webapp/WEB-INF/views/security/*.jsp`
- `src/main/java/com/test/security/entity/Member.java`
- `src/main/java/com/test/security/repository/MemberRepository.java`

Current practice style:

- Security-related beans are intentionally being kept in `SecurityConfig` for learning.
- Later they can be split into separate packages/classes.

## Completed so far

1. `UserDetailsService` bean

- Current active style uses a lambda.
- It loads a `Member` by username and builds Spring Security's default `UserDetails`.
- It uses `User.withUsername(...).password(...).roles(member.getRole()).build()`.

2. `PasswordEncoder` bean

- `BCryptPasswordEncoder(12)` is registered.
- Test users are stored with encoded passwords.

3. Test member data

- A `CommandLineRunner` creates these accounts if missing:

| username | password | role |
| --- | --- | --- |
| `basic` | `basic1234` | `Basic` |
| `advanced` | `advanced1234` | `Advanced` |
| `pro` | `pro1234` | `Pro` |
| `ultimate` | `ultimate1234` | `Ultimate` |

4. Login success/failure handlers

- `AuthenticationSuccessHandler` redirects to `/login-success`.
- `AuthenticationFailureHandler` redirects to `/login-fail`.
- `formLogin` now uses:

```java
.successHandler(authenticationSuccessHandler())
.failureHandler(authenticationFailureHandler())
```

instead of:

```java
.defaultSuccessUrl("/login-success")
.failureUrl("/login-fail")
```

Concept:

- `defaultSuccessUrl` / `failureUrl`: mostly URL movement.
- success/failure handlers: custom logic can be added before redirect.

5. `AuthenticationEntryPoint`

- Added for unauthenticated access.
- Current behavior: redirects unauthenticated users to `/login-custom`.

Concept:

```text
Not logged in + protected URL
-> AuthenticationEntryPoint
-> /login-custom
```

6. `AccessDeniedHandler`

- Added for authenticated users who do not have enough authority.
- Current behavior: redirects to `/access-denied`.
- `SecurityController` has `/access-denied`.
- JSP exists at `src/main/webapp/WEB-INF/views/security/access-denied.jsp`.

Concept:

```text
Not logged in
-> AuthenticationEntryPoint

Logged in but role is not enough
-> AccessDeniedHandler
```

Important note:

- The handler is wired, but there may not yet be enough URL role restrictions to trigger it clearly.
- The next useful step is to add role-protected URLs.

## Next tasks

### 1. Add URL role restrictions

Goal:

- Create URLs that require different roles so `AccessDeniedHandler` can be tested.

Suggested URLs:

```text
/basic
/advanced
/pro
/ultimate
```

Suggested security rules:

```java
.requestMatchers("/basic").hasRole("Basic")
.requestMatchers("/advanced").hasRole("Advanced")
.requestMatchers("/pro").hasRole("Pro")
.requestMatchers("/ultimate").hasRole("Ultimate")
```

Important:

- `roles(member.getRole())` creates authorities like `ROLE_Basic`, `ROLE_Advanced`, etc.
- Therefore `hasRole("Basic")` is correct, not `hasRole("ROLE_Basic")`.
- Order matters. Put specific role rules before `.anyRequest().authenticated()`.

Suggested controller methods can go in `TestController`:

```java
@GetMapping("/basic")
public String basicPage() {
    return "basic";
}
```

For a quick practice step, returning plain text with `@ResponseBody` is also acceptable if the user wants to avoid JSP pages.

After the user writes it:

- Check `SecurityConfig`.
- Check controller mappings.
- Run `.\mvnw.cmd compile`.
- Explain how to test:
  - Login as `basic`, visit `/basic`: should pass.
  - Login as `basic`, visit `/advanced`: should redirect to `/access-denied`.
  - Logout, visit `/basic`: should redirect to `/login-custom`.

### 2. Add `logoutSuccessHandler`

Goal:

- Replace `.logoutSuccessUrl("/logout-success")` with a custom `LogoutSuccessHandler`.

Suggested import:

```java
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
```

Suggested bean:

```java
@Bean
public LogoutSuccessHandler logoutSuccessHandler() {
    return (request, response, authentication) -> {
        response.sendRedirect("/logout-success");
    };
}
```

Suggested logout config:

```java
.logout(logout -> logout
        .logoutUrl("/logout-custom")
        .logoutSuccessHandler(logoutSuccessHandler())
        .permitAll()
)
```

Concept:

- `logoutSuccessUrl`: only redirects after logout.
- `logoutSuccessHandler`: can inspect the previous authentication, log data, clear extra cookies, then redirect.

### 3. Split handlers into classes later

Do not do this immediately unless the user asks.

Later structure:

```text
src/main/java/com/test/security/security/handler
- LoginSuccessHandler
- LoginFailureHandler
- CustomAuthenticationEntryPoint
- CustomAccessDeniedHandler
- CustomLogoutSuccessHandler
```

Purpose:

- Keep `SecurityConfig` focused on security wiring.
- Move behavior into handler classes.

### 4. Create `CustomUserDetails` later

Do not do this immediately unless the user asks.

Current code uses Spring Security's built-in `User`.

Later goal:

- Create `CustomUserDetails implements UserDetails`.
- Store `Member` inside it.
- Access domain-specific fields from `Authentication.getPrincipal()`.

### 5. Convert roles to enum later

Do not do this immediately unless the user asks.

Current role values are strings:

```text
Basic
Advanced
Pro
Ultimate
```

Later goal:

- Use enum to avoid typos.
- Make role hierarchy or comparison easier.

## Verification commands

Use this after user code changes:

```powershell
.\mvnw.cmd compile
```

Known issue:

```powershell
.\mvnw.cmd test
```

currently fails because test dependencies are missing. This is separate from the Spring Security practice code.

