# API 문서

기준 코드: Spring MVC Controller, DTO, Service, `GlobalExceptionHandler`, `AuthInterceptor`

## 공통

### Base URL

Base URL은 [application.properties](../src/main/resources/application.properties)의 `app.api.base-url` 값을 기준으로 한다.

```properties
app.api.base-url=http://localhost:8080
```

### 인증 방식

로그인 성공 시 서버가 `Set-Cookie` 헤더로 JWT 쿠키를 내려준다.

```http
Set-Cookie: token={jwt}; HttpOnly; Path=/; Max-Age=3600; SameSite={app.cookie.same-site}; Secure={app.cookie.secure}
```

`AuthInterceptor.passing`에 포함된 API와 `OPTIONS` 요청은 인증 없이 통과한다.  
`AuthInterceptor.forbidden`에 포함된 API는 403으로 차단한다.  
그 외 모든 API는 `token` 쿠키 검사를 통과해야 한다.

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

### 강제 차단 API

```text
GET /member/{pk}
```

### 인증 필요한 API

`passing`, `forbidden`, `OPTIONS`에 해당하지 않는 모든 API.

대표적으로:

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

### 성공 응답

현재 컨트롤러 성공 응답은 아직 `ApiResponse.success(...)`로 감싸지 않고, 각 컨트롤러가 반환하는 DTO 또는 `Map`을 그대로 반환한다.

예시:

```json
{
  "memberName": "홍길동",
  "memberPk": 1
}
```

### 공통 에러 응답

비즈니스 예외와 전역 예외 핸들러에서 처리하는 예외는 아래 형태로 반환한다.

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

`detail`이 없는 경우 `null`이다.

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

### 에러 코드

| HTTP Status | code | message | 주요 발생 조건 |
| --- | --- | --- | --- |
| 400 | `BAD_REQUEST_PARAM` | 요청 값이 올바르지 않습니다. | 서비스 레벨 요청값 검증 실패 |
| 400 | `BAD_REQUEST` | 파라미터의 형태가 올바르지 않습니다 | path/query 파라미터 타입 불일치 |
| 400 | `MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION` | 필수 요청 값이 누락되었습니다. | 필수 query parameter 누락 |
| 401 | `AUTHORIZATION_REQUIRED` | 로그인이 필요한 작업입니다. | 토큰 쿠키 없음 |
| 401 | `LOGIN_FAILED` | 아이디 또는 비밀번호가 올바르지 않습니다. | 로그인 ID/PW 불일치 |
| 403 | `FORBIDDEN_REQUEST` | 권한을 벗어난 요청입니다. | 강제 차단 API, 권한 없는 게시글 수정/삭제, 유효하지 않은 토큰 |
| 404 | `POST_NOT_FOUND` | 게시글을 찾을 수 없습니다. | 게시글 없음 또는 삭제됨 |
| 409 | `ID_CONFLICT` | 이미 사용 중인 아이디입니다. | 회원 ID 중복 |
| 500 | `INTERNAL_SERVER_ERROR` | 서버 에러입니다. | 처리되지 않은 서버 오류 |

---

## Member API

<details>
<summary><code>GET /member</code> - 전체 회원 조회 / 인증 필요 / Response: <code>Member[]</code></summary>

### Request

```http
GET /member
Cookie: token={jwt}
```

### Response 200

```json
[
  {
    "memberPk": 1,
    "memberId": "user01",
    "memberName": "홍길동",
    "memberEmail": "user01@example.com",
    "memberPasswordHash": "$2a$10$..."
  }
]
```

### Error

- 401 `AUTHORIZATION_REQUIRED`
- 403 `FORBIDDEN_REQUEST`

### 동작

- `MemberMapper.findAll()` 결과를 그대로 반환한다.
- 현재 응답에 `memberPasswordHash`가 포함될 수 있다.

</details>

<details>
<summary><code>GET /member/{pk}</code> - 회원 단건 조회 / 차단 / Response: <code>403</code></summary>

### Request

```http
GET /member/1
```

### Response 403

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

### 동작

- `GET /member/{var}` 패턴은 `AuthInterceptor.forbidden` 목록에 포함되어 있다.
- 컨트롤러에는 `memberMapper.findByPk(pk)` 호출 코드가 있지만, 인터셉터에서 먼저 차단된다.

</details>

<details>
<summary><code>GET /member/check-id</code> - ID 중복 확인 / 공개 / Query: <code>memberId</code> / Response: <code>{ duplicated }</code></summary>

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
  "duplicated": true
}
```

### Error

- 400 `MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION`

</details>

<details>
<summary><code>POST /member/login</code> - 로그인 / 공개 / Body: <code>memberId, memberPassword</code> / Response: <code>memberName, memberPk</code> + Cookie</summary>

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
  "memberName": "홍길동",
  "memberPk": 1
}
```

### Error

회원 ID가 없는 경우:

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

비밀번호가 틀린 경우:

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

회원 ID가 중복 데이터 상태인 경우:

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

### 동작

- `memberId`로 회원 수를 확인한다.
- 회원이 1명 있으면 비밀번호를 `BCryptPasswordEncoder.matches()`로 검증한다.
- 성공 시 JWT를 생성하고 `token` 쿠키로 내려준다.
- 응답 body에는 JWT가 포함되지 않는다.

</details>

<details>
<summary><code>POST /member/logout</code> - 로그아웃 / 인증 필요 / Response: <code>{ message }</code> + Cookie 삭제</summary>

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
  "message": "로그아웃 되었습니다"
}
```

### Error

- 401 `AUTHORIZATION_REQUIRED`
- 403 `FORBIDDEN_REQUEST`

### 동작

- `token` 쿠키를 빈 값으로 만들고 `Max-Age=0`으로 내려서 삭제시킨다.

</details>

<details>
<summary><code>POST /member/register</code> - 회원가입 / 공개 / Body: <code>memberName, memberId, memberPassword, memberEmail</code> / Response: <code>memberPk</code></summary>

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
  "memberPk": 1
}
```

### Error 409

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

### 동작

- `memberId` 중복 여부를 확인한다.
- 중복 회원이 없으면 비밀번호를 BCrypt로 해시한 뒤 회원을 생성한다.
- 현재 컨트롤러 응답 타입은 `Map<String, Integer>`이고, 서비스는 `memberMapper.insert(member)` 결과값을 반환한다.

</details>

<details>
<summary><code>GET /member/me</code> - 내 정보 조회 / 인증 필요 / Response: <code>MemberInfoResponse</code></summary>

### Request

```http
GET /member/me
Cookie: token={jwt}
```

### Response 200

```json
{
  "memberPk": 1,
  "memberName": "홍길동",
  "memberId": "user01",
  "memberEmail": "user01@example.com"
}
```

### Error

- 401 `AUTHORIZATION_REQUIRED`
- 403 `FORBIDDEN_REQUEST`

### 동작

- `AuthInterceptor`가 JWT에서 `memberPk`를 꺼내 request attribute에 저장한다.
- 컨트롤러는 `memberPk`로 회원 정보를 조회한다.

</details>

---

## Post API

<details>
<summary><code>GET /post/max-page</code> - 최대 페이지 수 조회 / 공개 / Query: <code>maxPostCount</code> / Response: <code>maxPageCount</code></summary>

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
  "maxPageCount": 5
}
```

### Error

`maxPostCount <= 0`인 경우:

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
      "reason": "해당 파라미터는 1 이상이여야 합니다."
    }
  }
}
```

`maxPostCount`가 숫자가 아니면 400 `BAD_REQUEST`가 발생한다.

</details>

<details>
<summary><code>GET /post/{post_pk}</code> - 게시글 상세 조회 / 공개 / Path: <code>post_pk</code> / Response: <code>PostViewResponse</code></summary>

### Request

```http
GET /post/1
```

### Response 200

```json
{
  "postPk": 1,
  "postTitle": "게시글 제목",
  "postContent": "게시글 내용",
  "postCreatedAt": "2026-05-10T12:00:00",
  "postUpdatedAt": "2026-05-10T12:30:00",
  "writerPk": 1,
  "writerId": "user01"
}
```

### Error 404

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

`post_pk`가 숫자가 아니면 400 `BAD_REQUEST`가 발생한다.

</details>

<details>
<summary><code>GET /post/all</code> - 게시글 목록 조회 / 공개 / Query: <code>page, maxPostCount</code> / Response: <code>PostBoardResponse[]</code></summary>

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
[
  {
    "postPk": 1,
    "postTitle": "게시글 제목",
    "writerId": "user01",
    "postCreatedAt": "2026-05-10T12:00:00",
    "postViewCount": 0
  }
]
```

### Error

`page <= 0` 또는 `maxPostCount <= 0`인 경우:

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
      "reason": "두 인자는 모두 1 이상이여야 합니다."
    }
  }
}
```

`page`, `maxPostCount`가 숫자가 아니면 400 `BAD_REQUEST`가 발생한다.

</details>

<details>
<summary><code>POST /post</code> - 게시글 생성 / 인증 필요 / Body: <code>postTitle, postContent</code> / Response: <code>message, postPk</code></summary>

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
  "message": "게시글이 생성되었습니다",
  "postPk": 1
}
```

### Error

제목 검증 실패:

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

내용 검증 실패:

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

인증 실패:

- 401 `AUTHORIZATION_REQUIRED`
- 403 `FORBIDDEN_REQUEST`

### 동작

- `AuthInterceptor`가 JWT에서 `memberPk`를 꺼내 request attribute에 저장한다.
- 서비스에서 request의 `memberPk`를 인증된 회원 PK로 설정한다.
- `postTitle`, `postContent`가 `null`, 빈 문자열, 금지어 포함 상태이면 400 응답이 발생한다.

</details>

<details>
<summary><code>DELETE /post/{post_pk}</code> - 게시글 삭제 처리 / 인증 필요 / Path: <code>post_pk</code> / Response: <code>postPk, postDeleted</code></summary>

### Request

```http
DELETE /post/1
Cookie: token={jwt}
```

### Response 200

```json
{
  "postPk": 1,
  "postDeleted": true
}
```

### Error

게시글이 없거나 이미 삭제된 경우:

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

작성자가 아닌 사용자가 삭제하려는 경우:

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

### 동작

- 실제 row 삭제가 아니라 `post_deleted = true`로 변경한다.
- 삭제 전 `post_pk`가 존재하고 삭제되지 않은 게시글인지 확인한다.
- 작성자 본인인지 확인한다.

</details>

<details>
<summary><code>PATCH /post/{post_pk}</code> - 게시글 수정 / 인증 필요 / Body: <code>postTitle, postContent</code> / Response: <code>postPk, postTitle, postContent</code></summary>

### Request

```http
PATCH /post/1
Content-Type: application/json
Cookie: token={jwt}
```

```json
{
  "postTitle": "수정된 제목",
  "postContent": "수정된 내용"
}
```

### Response 200

```json
{
  "postPk": 1,
  "postTitle": "수정된 제목",
  "postContent": "수정된 내용",
  "memberPk": null
}
```

### Error

작성자가 아닌 사용자가 수정하려는 경우:

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

게시글이 없거나 이미 삭제된 경우:

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

### 동작

- 수정 전 작성자 본인인지 확인한다.
- 그 다음 `post_pk`가 존재하고 삭제되지 않은 게시글인지 확인한다.
- `post_updated_at`을 현재 시각으로 변경한다.
- 현재 코드 기준 수정 요청의 제목/내용 null 또는 빈 문자열 검증은 없다.

주의: 현재 서비스는 소유자 확인 시 path variable `post_pk`가 아니라 request body의 `postPk`를 사용한다.

</details>

---

## Test API

<details>
<summary><code>GET /test</code> - 테스트 API / 공개 / Response: <code>{ message }</code></summary>

### Request

```http
GET /test
```

### Response 200

```json
{
  "message": "hello"
}
```

### 동작

- 내부 `AtomicInteger` 값을 증가시키고 콘솔에 출력한다.
- 약 100ms 대기 후 응답한다.

</details>

<details>
<summary><code>POST /login</code> - 테스트 로그인 API / 인증 필요 / 현재 정상 응답 없음</summary>

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

### 동작

- 현재 `AuthInterceptor.passing`에 없으므로 인증 필요 API다.
- 기존 JWT 테스트 코드가 주석 처리되어 있다.
- 실제 로그인은 `POST /member/login`을 사용한다.

</details>

<details>
<summary><code>GET /check</code> - Authorization 헤더 JWT 확인 / 인증 필요 / Header: <code>Authorization</code> / Response: <code>username</code></summary>

### Request

```http
GET /check
Authorization: Bearer {jwt}
Cookie: token={jwt}
```

### Response 200

```json
{
  "username": "1"
}
```

### 동작

- 현재 `AuthInterceptor.passing`에 없으므로 인증 필요 API다.
- `Authorization` 헤더에서 `Bearer ` 문자열을 제거한 뒤 JWT를 파싱한다.
- `jwtService.getMemberPk(token)` 결과를 `username` 필드명으로 반환한다.
- 실제 의미는 username이 아니라 memberPk에 가깝다.
- `Authorization` 헤더가 없으면 전역 예외 핸들러에서 500 `INTERNAL_SERVER_ERROR`로 처리될 수 있다.

</details>
