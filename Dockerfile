# 1. 빌드 스테이지
FROM gradle:8-jdk24 AS build
COPY --chown=gradle:gradle . /home/src
WORKDIR /home/src
RUN gradle build --no-daemon -x test

# 2. 실행 스테이지 (openjdk 대신 전 세계 표준인 eclipse-temurin 자바 24 버전 사용)
FROM eclipse-temurin:24-jre-alpine
EXPOSE 8080
COPY --from=build /home/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]