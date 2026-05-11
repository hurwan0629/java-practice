FROM gradle:8.14.4-jdk17 AS build
# 도커에 /app 생성
WORKDIR /app

# 현재 설정 파일들을 도커 컨테이너 /app에 복사
COPY build.gradle settings.gradle gradlew ./
# gradle도 ./gradle로 복사
COPY gradle ./gradle

# ./gradlew에 execute 권한을 줌
RUN chmod +x ./gradlew

# src 폴더도 복사
COPY src ./src
# jar파일로 빌드 시작
RUN ./gradlew bootJar --no-daemon

# 2번째 공간 생성 (자바 런타임)
FROM eclipse-temurin:17-jre
WORKDIR /app

# 빌드되어있는 .jar 파일을 app.jar 라는 이름으로 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 8080에서 실행할 예정임을 보임 (실제로는 설정 따라감)
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
