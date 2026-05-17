# 스프링 공부용 프로젝트
- 3.5.14
- gradle
- Java17
## 사용중인 의존성
- spring-boot-starter: 스프링 컨테이너 사용, 자동 설정 기반 등을 위해 사용
- spring-boot-starter-web: 기본 RestController과 Mapping 어노테이션을 이용하기 위해
- spring-security-crypto: spring security에서 BCrypto를 통한 BCryptPasswordEncoder()을 사용하기 위해
- jjwt-api:0.13.0: jwt토큰 생성 및 디코딩을 위해 (개발용)
- jjwt-impl: 런타임용 실제 jwt 파싱 내부 구현체 (런타임)
- jjwt-jackson: 위와 같은 JWT안의 payload의 직렬화/역직렬화용 (런타임)
- mybatis-spring-boot-starter:3.0.5: mybatis를 사용하기 위한 의존성
- postgresql: 런타임에 실제 postgres서버에 접속 가능하게 해주기 위한 JDBC 드라이버

## applicaiton.properties
- spring.datasource
  - driver-class-name: 드라이버 생성 사용할 클래스
    - url: 드라이버 생성 시 필요한 DB서버 url (jdbc:postgresql://출처/테이블)
    - username: 스키마 이름
    - password: 스키마 비밀번호
- -mybatis.mapper-locations-: 현재는 어노테이션을 쓰기 때문에 필요 없지만 DAO 방식을 사용할 때에는 필수적으로 해야하는 설정

# 프로젝트 이해
## 2026-05-09
### 기본 요청 매핑
- TestController은 이제 안쓰는 클래스이다.
- MemberController은 기본적인 로그인, 로그아웃 상태관리에만 사용중이다.
- Post /login 
  을 받을때에는 특별히 비즈니스 로직으로 뺴지 않고 직접 id체크, 비번체크, ResponseCookie 생성까지 담당해준다.
### 웹 설정
- 빈 방식으로 WebMvcConfigurer 객체 생성하는 로직으로 WebMvcConfigurer 인터페이스 익명 구현체를 생성하는 방식을 사용했다.
  addCorsMappings(registry), addInterceptors(InterceptorRegistry registry)
  를 통해 메서드를 생성하였다.
- PasswordConfig를 통해 BCryptPasswordEncoder 빈을 만들었다.
- 예외 같은 경우에는 권한검사 에외, 포스트 탐지 예외, 서버 에러를 따로 만들어주었다.
### DB 설정
- MyBatis 방식의 `@Mapper` 방식을 사용하였다.
- Postgresql을 사용하였으며 설치가 귀찮아 도커를 썼다.
- 대부분의 Mapper의 파라미터 및 반환값은 dto로 만들어서 사용하였다.
- lombok는 그냥 귀찮아서 안넣었는데 슬슬 넣을까 생각중
- 
### 인증/인가 설정
- interceptor/AuthInterceptor 에서 요청 uri를 뜯어보아 인증이 필요할 경우 쿠키를 전부 뜯으면서 첫번째 값을 탐색 -> 존재해야 true + memberPk를 Long로 치환하여 request.setAttribute를 해준다.

---
# 진행도
## 2026-05-10
### 한것 (15:30)
개발보다는 Controller에 있는 복잡하거나 확장 가능성 있는 로직들을 모두 MemberService, PostService로 옮겼으며 Jwt나 Password를 toString로 출력하게 하던것을 삭제하였으며 jwt는 jwtExist를 통해 존재하는지만 확인하였음
401, 403, 409, 400 등에 대한 에러 추가하였음.
@RequestParam에러 page, count, pk 등을 받을 때 String로 받던걸 자동 파싱 개념을 알고 그냥 Long, Integer, int로 받았음.
### Todo
- [x] 현재 집(데스크탑) 카페(노트북)으로 개발중 오늘은 노트북으로 했는데 Intellij나 기타 개발시에 단축키 등에 대한 불편함이 조금 있었다. 해결이 필요함.
- [x] alidation을 도입하는 것을 고려해야겠다.
- Spring Security를 도입하고싶은데 아직은 그정도의 규모는 잡힌것인지 의문이다.
- [x] dto, service 등의 네이밍 규칙이 슬슬 필요해지는거같다.
- [x] AuthInterceptor과 WebMvcCorsConfig.addInterceptors 에서 url검사 역할 분리가 필요하다.
### 19:00
- 창 우클릭하면 `Pin Tab` 라는 기능하고 `File Properties > Make File ReadOnly`가 존재하는 것을 알 수 있었다. 이제 오른쪽은 대체로 보면서 왼쪽 창에서 작업할 예정이다.
- 일단 네이밍 규칙같은 경우에는 점진적으로 해결할 생각이다. 클래스 명같은 경우에는 좀 둘러볼 예정이다.
  - `config/`: 나쁘지 않아서 패스
  - `controller/`: 이것도 여지없이 패스
  - `domain/`: 동일
  - `dto/`: 이건 좀 유심히 봐야할듯
    - `MemberAuthInfo`: 로그인 시 id/pw 확인용
    - `MemberInfoResponse`: // 현재에는 `/member/me`에 대한 요청에 응답하기 위해 만들어진 dto
    - `MemberLoginRequest`: 유지
    - `MemberLoginResponse`: 유지
    - `MemberRegisterRequest`: 유지 (응답은 그냥 memberPk만 이루어지기 때문에`MemberRegisterResponse`는 없음)
    - `PostBoardResponse`: `/post/all`에 사용됨 (Request는 `/{var}` 로 해결되어서 없음)
    - `PostCreateRequest`: 유지
    - `PostDeleteResponse`: 2개 이상 전달하기 위한 DTO
    - `PostUpdateDto`: 이게 잘 저장되었는지 보여주기 위해서 Requset/Response 모두에 쓰였었는데 본질은 Request이니까 Request로 변경
    - `PostViewResponse`: 1개 선택 시 사용. Detail 페이지
  - `exception/`: 패스
  - `interceptor/`: 패스
  - `mapper/`: 패스
  - `service/`: 패스
- 이후에 메서드 명도 좀 고쳤다. 구체적으로 쓰려했는데 좀 보기 불편해서.
  - getPostService -> getPost
  - PostService.calcMaxPageCountByMaxPostCount -> getMaxPageCount
  - PostService.getPostService -> getPost
  - PostService.getAllPost -> getPosts
  - PostService.setPostDeletedTrueByUserDeleteReqeust -> setPostDeletedTrueByUserDeleteRequest
  - PostService.updatePostTitleAndPostContentByPk -> updatePost
  - MemberService.tryMemberLogin -> login
  - MemberService.memberRegister -> register
  - MemberService.calcMaxPageCountByMaxPostCount -> getMaxPageCount
- addInterceptors와 AuthInterceptor에서 겹치는 url 역할이 있어서 그냥 전부다 AuthInterceptor에 위임하였다.
- 
### Todo
- Security 써보기
- 권한/인가 구조 정리
- 요청 DDos Dos 등에 대해서 방안 찾아보기
- 테스트 정리해보기
- 그리고 확장해보기
- 예외/응답 구조 통일하기

## 2026-05-11
### 06:29
- post 수정/삭제에서 본인인지 확인하는 제대로된 코드가 없었어서 RequestAttribute("memberPk") 를 이용하여 확인하는 절차를 가짐 (checkPostByPostPkAndMemberPk)
### 07:51
- 응답 구조가 일관적이지 않은게 조금 신경쓰여서 리팩토링.
  1. dto.ApiResponse: [success, data, error]으로 이루어진 DTO. success(data), fail(error) 으로 이루어진 메서드를 통해 객체 생성 가능
  2. dto.ErrorResponse: [code, status, message, detail(or null)]로 이루어진 에러 응답 전용 에러 ApiResponse 제작에 쓰기 위해 만들어진 dto
  3. ErrorCode: Error[code, status, message]를 넣기위해 만들어진 Enum
  4. BusinessException: 기존의 모든 사용자 정의 예외(UDE)를 대체하기 위해 만든 예외 핸들러
  5. 이후 모든 `throw new ...`를 `BusinessException`으로 변경함. 일반 응답도 바꿔야하는데 아직은 못함.

### 10:42
- 응답 구조도 싹다 ResponseEntity<ApiResponse<?>> 형태로 바꾸었음.

---
