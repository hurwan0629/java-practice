# 스프링 공부용 프로젝트 
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