# API 문서

기준 코드: Spring MVC Controller, DTO, Service, `exception/`, `GlobalExceptionHandler`, `AuthInterceptor`

## 공통

### Base URL

Base URL은 [application.properties](../src/main/resources/application.properties)의 `app.api.base-url` 값을 기준으로 한다.

```properties
app.api.base-url=http://localhost:8080
```

### 응답 래퍼

`/test`, `/check`, 테스트용 `/login`을 제외한 주요 컨트롤러 응답은 `ApiResponse<T>`로 감싼다.

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

에러 응답은 `GlobalExceptionHandler`가 아래 형태로 반환한다.

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "에러 메시지",
    "status": 400,
    "detail": {
      "resource": "대상",
      "action": "동작",
      "reason": "상세 사유"
    }
  }
}
```

`BusinessException(ErrorCode)`처럼 detail 없이 던진 경우 `detail`은 `null`이다.

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "POST_NOT_FOUND",
    "message": "게시글을 찾을 수 없습니다.",
    "status": 404,
    "detail": null
  }
}
```

### 인증 방식

로그인 성공 시 서버가 `Set-Cookie` 헤더로 JWT 쿠키를 내려준다.

```http
Set-Cookie: token={jwt}; HttpOnly; Path=/; Max-Age=3600; SameSite={app.cookie.same-site}; Secure={app.cookie.secure}
```

`AuthInterceptor.passing`에 포함된 API와 `OPTIONS` 요청은 인증 없이 통과한다. 그 외 API는 `token` 쿠키 검증을 통과해야 한다.

### 인증 없이 접근 가능한 API

```text
POST /member/login
POST /member/register
GET  /member/check-id
GET  /post/max-page
GET  /post/{post_pk}
GET  /post/all
GET  /test
OPTIONS *
```

### 항상 차단되는 API

```text
GET /member/{pk}
```

### 인증이 필요한 API

```text
GET    /member
POST   /member/logout
GET    /member/me
POST   /post
PATCH  /post/{post_pk}
DELETE /post/{post_pk}
GET    /check
POST   /login
```

### 에러 코드 기준

| HTTP Status | code | 처리 위치 | 주요 발생 조건 |
| --- | --- | --- | --- |
| 400 | `BAD_REQUEST_PARAM` | `BusinessException` | 서비스 검증 실패 |
| 400 | `BAD_REQUEST` | `MethodArgumentTypeMismatchException` | path/query 파라미터 타입 변환 실패 |
| 400 | `MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION` | `MissingServletRequestParameterException` | 필수 query parameter 누락 |
| 401 | `AUTHORIZATION_REQUIRED` | `AuthInterceptor` | `token` 쿠키 없음 |
| 401 | `LOGIN_FAILED` | `MemberService.login` | 로그인 ID/PW 불일치 |
| 403 | `FORBIDDEN_REQUEST` | `AuthInterceptor`, `PostService` | 차단 API 접근, 잘못된 JWT, 작성자 불일치 |
| 404 | `POST_NOT_FOUND` | `PostService` | 게시글 없음 또는 삭제됨 |
| 409 | `ID_CONFLICT` | `MemberService` | 회원 ID 중복 또는 DB에 동일 ID가 2개 이상 존재 |
| 500 | `INTERNAL_SERVER_ERROR` | `Exception.class` fallback | 별도 핸들러가 없는 서버 예외 |

---

## Member API

<details>
<summary><code>GET /member</code> - 전체 회원 조회 / 인증 필요</summary>

### Request

```http
GET /member
Cookie: token={jwt}
```

### Response 200

```json
{
  "success": true,
  "data": [
    {
      "memberPk": 1,
      "memberId": "user01",
      "memberName": "홍길동",
      "memberEmail": "user01@example.com",
      "memberPasswordHash": "$2a$10$..."
    }
  ],
  "error": null
}
```

현재 `Member` 도메인을 그대로 반환하므로 `memberPasswordHash`가 응답에 포함된다.

<details>
<summary>Errors</summary>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "AUTHORIZATION_REQUIRED",
    "message": "로그인이 필요한 작업입니다.",
    "status": 401,
    "detail": {
      "resource": "HTTP_REQUEST",
      "action": "auth",
      "reason": "인증이 필요한 작업입니다."
    }
  }
}
```

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 파싱, 서명 검증, 만료 검증, `memberPk` 변환 중 예외 발생

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "FORBIDDEN_REQUEST",
    "message": "권한을 벗어난 요청입니다.",
    "status": 403,
    "detail": {
      "resource": "JwtToken",
      "action": "token",
      "reason": "유효하지 않은 토큰입니다."
    }
  }
}
```

</details>

</details>

</details>

<details>
<summary><code>GET /member/{pk}</code> - 회원 단건 조회 / 강제 차단</summary>

### Request

```http
GET /member/1
```

### Response 403

`GET /member/{var}` 패턴은 `AuthInterceptor.forbidden` 목록에 포함되어 컨트롤러 진입 전에 차단된다.

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "FORBIDDEN_REQUEST",
    "message": "권한을 벗어난 요청입니다.",
    "status": 403,
    "detail": {
      "resource": "HTTP_REQUEST",
      "action": "auth",
      "reason": "인증이 필요한 작업입니다."
    }
  }
}
```

<details>
<summary>Errors</summary>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: 요청이 `GET /member/{var}` 패턴과 일치함

</details>

</details>

</details>

<details>
<summary><code>GET /member/check-id</code> - ID 중복 확인 / 공개</summary>

### Request

```http
GET /member/check-id?memberId=user01
```

### Query Parameters

| Name | Type | Required | Description |
| --- | --- | --- | --- |
| memberId | String | yes | 중복 확인할 회원 ID |

### Response 200

```json
{
  "success": true,
  "data": {
    "duplicated": true
  },
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>400 <code>MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION</code></summary>

- 발생 위치: `GlobalExceptionHandler.handleMissingParam`
- 조건: `memberId` query parameter 누락

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION",
    "message": "필수 요청 값이 누락되었습니다.",
    "status": 400,
    "detail": null
  }
}
```

</details>

</details>

</details>

<details>
<summary><code>POST /member/login</code> - 로그인 / 공개</summary>

### Request

```http
POST /member/login
Content-Type: application/json
```

```json
{
  "memberId": "user01",
  "memberPassword": "password123"
}
```

### Response 200

```http
Set-Cookie: token={jwt}; HttpOnly; Path=/; Max-Age=3600
```

```json
{
  "success": true,
  "data": {
    "memberName": "홍길동",
    "memberPk": 1
  },
  "error": null
}
```

JWT는 response body에 포함하지 않고 `token` 쿠키로만 내려간다.

<details>
<summary>Errors</summary>

<details>
<summary>401 <code>LOGIN_FAILED</code> - ID 없음</summary>

- 발생 위치: `MemberService.login`
- 조건: `memberMapper.checkMemberUniqueWithId(memberId)` 결과가 `0`

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "LOGIN_FAILED",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
    "status": 401,
    "detail": {
      "resource": "memberId",
      "action": "select",
      "reason": "아이디가 잘못되었습니다."
    }
  }
}
```

</details>

<details>
<summary>401 <code>LOGIN_FAILED</code> - 비밀번호 불일치</summary>

- 발생 위치: `MemberService.login`
- 조건: `passwordEncoder.matches(...)` 결과가 `false`

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "LOGIN_FAILED",
    "message": "아이디 또는 비밀번호가 올바르지 않습니다.",
    "status": 401,
    "detail": {
      "resource": "memberPassword",
      "action": "select",
      "reason": "비밀번호가 잘못되었습니다."
    }
  }
}
```

</details>

<details>
<summary>409 <code>ID_CONFLICT</code></summary>

- 발생 위치: `MemberService.login`
- 조건: 동일 `memberId` 회원 수가 `2` 이상

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ID_CONFLICT",
    "message": "이미 사용 중인 아이디입니다.",
    "status": 409,
    "detail": {
      "resource": "memberId",
      "action": "select",
      "reason": "아이디가 중복 되었습니다."
    }
  }
}
```

</details>

</details>

</details>

<details>
<summary><code>POST /member/logout</code> - 로그아웃 / 인증 필요</summary>

### Request

```http
POST /member/logout
Cookie: token={jwt}
```

### Response 200

```http
Set-Cookie: token=; HttpOnly; Path=/; Max-Age=0
```

```json
{
  "success": true,
  "data": {
    "message": "로그아웃 되었습니다."
  },
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 검증 실패

</details>

</details>

</details>

<details>
<summary><code>POST /member/register</code> - 회원가입 / 공개</summary>

### Request

```http
POST /member/register
Content-Type: application/json
```

```json
{
  "memberName": "홍길동",
  "memberId": "user01",
  "memberPassword": "password123",
  "memberEmail": "user01@example.com"
}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "memberPk": 1
  },
  "error": null
}
```

현재 코드는 `memberService.register`의 반환값을 `memberPk`로 내려준다. MyBatis `insert`는 보통 영향받은 row 수를 반환하므로, 생성된 PK가 아니라 `1`일 수 있다.

<details>
<summary>Errors</summary>

<details>
<summary>409 <code>ID_CONFLICT</code></summary>

- 발생 위치: `MemberService.register`
- 조건: 동일 `memberId` 회원 수가 `1` 이상

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ID_CONFLICT",
    "message": "이미 사용 중인 아이디입니다.",
    "status": 409,
    "detail": {
      "resource": "memberId",
      "action": "select",
      "reason": "이미 존재하는 아이디입니다. 다른 아이디를 시도해주세요"
    }
  }
}
```

</details>

</details>

</details>

<details>
<summary><code>GET /member/me</code> - 내 정보 조회 / 인증 필요</summary>

### Request

```http
GET /member/me
Cookie: token={jwt}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "memberPk": 1,
    "memberName": "홍길동",
    "memberId": "user01",
    "memberEmail": "user01@example.com"
  },
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 검증 실패

</details>

</details>

</details>

---

## Post API

<details>
<summary><code>GET /post/max-page</code> - 최대 페이지 수 조회 / 공개</summary>

### Request

```http
GET /post/max-page?maxPostCount=10
```

### Query Parameters

| Name | Type | Required | Default | Description |
| --- | --- | --- | --- | --- |
| maxPostCount | Integer | no | 10 | 한 페이지에 표시할 게시글 수 |

### Response 200

```json
{
  "success": true,
  "data": {
    "maxPageCount": 5
  },
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>400 <code>BAD_REQUEST_PARAM</code></summary>

- 발생 위치: `PostService.getMaxPageCount`
- 조건: `maxPostCount <= 0`

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "BAD_REQUEST_PARAM",
    "message": "요청 값이 올바르지 않습니다.",
    "status": 400,
    "detail": {
      "resource": "maxPostCount",
      "action": "select",
      "reason": "해당 파라미터는 1 이상이어야 합니다."
    }
  }
}
```

</details>

<details>
<summary>400 <code>BAD_REQUEST</code></summary>

- 발생 위치: `GlobalExceptionHandler.handleTypeMismatch`
- 조건: `maxPostCount`가 `Integer`로 변환되지 않음

</details>

</details>

</details>

<details>
<summary><code>GET /post/{post_pk}</code> - 게시글 상세 조회 / 공개</summary>

### Request

```http
GET /post/1
```

### Response 200

```json
{
  "success": true,
  "data": {
    "postPk": 1,
    "postTitle": "게시글 제목",
    "postContent": "게시글 내용",
    "postCreatedAt": "2026-05-10T12:00:00",
    "postUpdatedAt": "2026-05-10T12:30:00",
    "writerPk": 1,
    "writerId": "user01"
  },
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>400 <code>BAD_REQUEST</code></summary>

- 발생 위치: `GlobalExceptionHandler.handleTypeMismatch`
- 조건: `post_pk`가 `Long`으로 변환되지 않음

</details>

<details>
<summary>404 <code>POST_NOT_FOUND</code></summary>

- 발생 위치: `PostService.getPost`
- 조건: `postMapper.getPostByPk(postPk)` 결과가 `null`

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "POST_NOT_FOUND",
    "message": "게시글을 찾을 수 없습니다.",
    "status": 404,
    "detail": {
      "resource": "post",
      "action": "select",
      "reason": "해당 포스트가 존재하지 않습니다."
    }
  }
}
```

</details>

</details>

</details>

<details>
<summary><code>GET /post/all</code> - 게시글 목록 조회 / 공개</summary>

### Request

```http
GET /post/all?page=1&maxPostCount=10
```

### Query Parameters

| Name | Type | Required | Default | Description |
| --- | --- | --- | --- | --- |
| page | int | no | 1 | 조회할 페이지 번호 |
| maxPostCount | int | no | 10 | 한 페이지에 표시할 게시글 수 |

### Response 200

```json
{
  "success": true,
  "data": [
    {
      "postPk": 1,
      "postTitle": "게시글 제목",
      "writerId": "user01",
      "postCreatedAt": "2026-05-10T12:00:00",
      "postViewCount": 0
    }
  ],
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>400 <code>BAD_REQUEST_PARAM</code></summary>

- 발생 위치: `PostService.getPosts`
- 조건: `page <= 0` 또는 `maxPostCount <= 0`

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "BAD_REQUEST_PARAM",
    "message": "요청 값이 올바르지 않습니다.",
    "status": 400,
    "detail": {
      "resource": "[page, maxPostCount]",
      "action": "update",
      "reason": "두 인자는 모두 1 이상이어야 합니다."
    }
  }
}
```

</details>

<details>
<summary>400 <code>BAD_REQUEST</code></summary>

- 발생 위치: `GlobalExceptionHandler.handleTypeMismatch`
- 조건: `page` 또는 `maxPostCount`가 `int`로 변환되지 않음

</details>

</details>

</details>

<details>
<summary><code>POST /post</code> - 게시글 생성 / 인증 필요</summary>

### Request

```http
POST /post
Content-Type: application/json
Cookie: token={jwt}
```

```json
{
  "postTitle": "게시글 제목",
  "postContent": "게시글 내용"
}
```

### Response 201

```json
{
  "success": true,
  "data": {
    "message": "게시글이 생성되었습니다.",
    "postPk": 1
  },
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>400 <code>BAD_REQUEST_PARAM</code> - 제목 검증 실패</summary>

- 발생 위치: `PostService.createPost`
- 조건: `postTitle == null`, 빈 문자열, 또는 금칙어 문자열 포함

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "BAD_REQUEST_PARAM",
    "message": "요청 값이 올바르지 않습니다.",
    "status": 400,
    "detail": {
      "resource": "postTitle",
      "action": "create",
      "reason": "제목이 없거나 부적절합니다."
    }
  }
}
```

</details>

<details>
<summary>400 <code>BAD_REQUEST_PARAM</code> - 내용 검증 실패</summary>

- 발생 위치: `PostService.createPost`
- 조건: `postContent == null`, 빈 문자열, 또는 금칙어 문자열 포함

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "BAD_REQUEST_PARAM",
    "message": "요청 값이 올바르지 않습니다.",
    "status": 400,
    "detail": {
      "resource": "postContent",
      "action": "create",
      "reason": "내용이 없거나 부적절합니다."
    }
  }
}
```

</details>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 검증 실패

</details>

</details>

</details>

<details>
<summary><code>DELETE /post/{post_pk}</code> - 게시글 삭제 처리 / 인증 필요</summary>

### Request

```http
DELETE /post/1
Cookie: token={jwt}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "postPk": 1,
    "postDeleted": true
  },
  "error": null
}
```

<details>
<summary>Errors</summary>

<details>
<summary>400 <code>BAD_REQUEST</code></summary>

- 발생 위치: `GlobalExceptionHandler.handleTypeMismatch`
- 조건: `post_pk`가 `Long`으로 변환되지 않음

</details>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code> - JWT 검증 실패</summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 검증 실패

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code> - 작성자 불일치</summary>

- 발생 위치: `PostService.setPostDeletedTrueByUserDeleteRequest`
- 조건: `checkMemberPkOwnsPost(postPk, memberPk)` 결과가 `false`

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "FORBIDDEN_REQUEST",
    "message": "권한을 벗어난 요청입니다.",
    "status": 403,
    "detail": {
      "resource": "post",
      "action": "update",
      "reason": "글의 소유자가 아닙니다."
    }
  }
}
```

</details>

<details>
<summary>404 <code>POST_NOT_FOUND</code></summary>

- 발생 위치: `PostService.setPostDeletedTrueByUserDeleteRequest`
- 조건: `postMapper.checkPostByPk(postPk) <= 0`
- 특이사항: 이 케이스는 `BusinessException(ErrorCode.POST_NOT_FOUND)`로 던져져 `detail`이 `null`

</details>

</details>

</details>

<details>
<summary><code>PATCH /post/{post_pk}</code> - 게시글 수정 / 인증 필요</summary>

### Request

```http
PATCH /post/1
Content-Type: application/json
Cookie: token={jwt}
```

```json
{
  "postPk": 1,
  "postTitle": "수정된 제목",
  "postContent": "수정된 내용"
}
```

### Response 200

```json
{
  "success": true,
  "data": {
    "postPk": 1,
    "postTitle": "수정된 제목",
    "postContent": "수정된 내용",
    "memberPk": null
  },
  "error": null
}
```

현재 서비스는 소유자 확인 시 path variable `post_pk`가 아니라 request body의 `postPk`를 먼저 사용한다. 따라서 body에도 `postPk`가 있어야 정상적으로 권한 확인이 가능하다. DB 업데이트 직전에는 `request.setPostPk(postPk)`로 path variable 값을 다시 넣는다.

<details>
<summary>Errors</summary>

<details>
<summary>400 <code>BAD_REQUEST</code></summary>

- 발생 위치: `GlobalExceptionHandler.handleTypeMismatch`
- 조건: path variable `post_pk`가 `Long`으로 변환되지 않음

</details>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code> - JWT 검증 실패</summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 검증 실패

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code> - 작성자 불일치</summary>

- 발생 위치: `PostService.updatePost`
- 조건: `checkMemberPkOwnsPost(request.getPostPk(), memberPk)` 결과가 `false`
- 특이사항: `action` 값은 현재 코드상 `"delete"`로 내려간다.

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "FORBIDDEN_REQUEST",
    "message": "권한을 벗어난 요청입니다.",
    "status": 403,
    "detail": {
      "resource": "post",
      "action": "delete",
      "reason": "글의 소유자가 아닙니다."
    }
  }
}
```

</details>

<details>
<summary>404 <code>POST_NOT_FOUND</code></summary>

- 발생 위치: `PostService.updatePost`
- 조건: `postMapper.checkPostByPk(postPk) <= 0`

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "POST_NOT_FOUND",
    "message": "게시글을 찾을 수 없습니다.",
    "status": 404,
    "detail": {
      "resource": "post",
      "action": "select",
      "reason": "글이 존재하지 않거나 삭제되었습니다."
    }
  }
}
```

</details>

<details>
<summary>500 <code>INTERNAL_SERVER_ERROR</code> - body <code>postPk</code> 누락 가능</summary>

- 발생 위치: `GlobalExceptionHandler.handleException`
- 조건: body의 `postPk`가 `null`인 상태에서 `checkMemberPkOwnsPost(request.getPostPk(), memberPk)` 호출 후 매퍼/DB 레이어에서 처리되지 않은 예외가 발생하는 경우
- 특이사항: 현재 코드는 이 값을 서비스에서 직접 400으로 검증하지 않는다.

</details>

</details>

</details>

---

## Test API

<details>
<summary><code>GET /test</code> - 테스트 API / 공개</summary>

### Request

```http
GET /test
```

### Response 200

`ApiResponse`로 감싸지 않는다.

```json
{
  "message": "hello",
  "dbConnection": true
}
```

`testMapper.checkConnection()`에서 예외가 발생해도 컨트롤러 내부에서 catch하고 `dbConnection: false`로 응답한다.

</details>

<details>
<summary><code>POST /login</code> - 테스트용 로그인 API / 인증 필요</summary>

### Request

```http
POST /login
Content-Type: application/json
Cookie: token={jwt}
```

```json
{
  "username": "user01"
}
```

### Response

현재 컨트롤러가 `null`을 반환한다.

<details>
<summary>Errors</summary>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 검증 실패

</details>

</details>

</details>

<details>
<summary><code>GET /check</code> - Authorization 헤더 JWT 확인 / 인증 필요</summary>

### Request

```http
GET /check
Authorization: Bearer {jwt}
Cookie: token={jwt}
```

### Response 200

`ApiResponse`로 감싸지 않는다.

```json
{
  "username": "1"
}
```

`username` 필드에는 실제 username이 아니라 `jwtService.getMemberPk(token)` 결과가 들어간다.

<details>
<summary>Errors</summary>

<details>
<summary>401 <code>AUTHORIZATION_REQUIRED</code></summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `Cookie`가 없거나 `token` 쿠키가 없음

</details>

<details>
<summary>403 <code>FORBIDDEN_REQUEST</code> - 쿠키 JWT 검증 실패</summary>

- 발생 위치: `AuthInterceptor.preHandle`
- 조건: `token` 쿠키는 있으나 JWT 검증 실패

</details>

<details>
<summary>500 <code>INTERNAL_SERVER_ERROR</code> - Authorization 헤더 누락</summary>

- 발생 위치: `GlobalExceptionHandler.handleException`
- 조건: `Authorization` request header 누락
- 이유: `TestController.check`의 `@RequestHeader("Authorization")` 누락 예외를 별도 핸들러가 처리하지 않으므로 fallback으로 잡힌다.

</details>

<details>
<summary>500 <code>INTERNAL_SERVER_ERROR</code> - Authorization JWT 파싱 실패</summary>

- 발생 위치: `GlobalExceptionHandler.handleException`
- 조건: `Authorization` 헤더의 `Bearer {jwt}` 토큰이 `jwtService.getMemberPk(token)`에서 파싱 또는 검증 실패
- 이유: 컨트롤러 내부에서 직접 호출하며 `BusinessException`으로 변환하지 않는다.

</details>

</details>

</details>
