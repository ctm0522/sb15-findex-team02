# 1단계: 빌드 환경 (Gradle Wrapper 사용 기준)
FROM eclipse-temurin:17-jdk-jammy AS builder
WORKDIR /app

# 캐시 효율을 위해 빌드 스크립트 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Gradle 실행 권한 부여 및 의존성 다운로드
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사 및 실행 가능한 jar 빌드
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# 2단계: 경량 실행 환경 (JRE)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# 빌드 결과물 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# Render가 주입하는 PORT 환경변수 기본값 설정 및 포트 노출
ENV PORT=8080
EXPOSE ${PORT}

# 서버 실행 (512MB 메모리 제한 방지용 -Xmx 옵션 적용)
ENTRYPOINT ["java", "-Dserver.port=${PORT}", "-Xmx380m", "-jar", "app.jar"]