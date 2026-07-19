# 1. 빌드 스테이지 (자바 24 공식 지원하는 최신 그레들 이미지)
FROM gradle:8-jdk24 AS build
COPY --chown=gradle:gradle . /home/src
WORKDIR /home/src
# 데몬 끄고 테스트 패스해서 속도업
RUN gradle build --no-daemon -x test

# 2. 실행 스테이지 (자바 24 런타임 환경)
FROM openjdk:24-slim
EXPOSE 8080
COPY --from=build /home/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]