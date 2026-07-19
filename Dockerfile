# 1. 빌드 스테이지 (Gradle 8.12 이상 버전 + JDK 24 환경 추천)
FROM gradle:8.12-jdk24 AS build
COPY --chown=gradle:gradle . /home/src
WORKDIR /home/src
RUN gradle build --no-daemon -x test

# 2. 실행 스테이지 (Eclipse Temurin JDK 24의 슬림한 실행 환경)
FROM eclipse-temurin:24-jre-noble
EXPOSE 8080
COPY --from=build /home/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]