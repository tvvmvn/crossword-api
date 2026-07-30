# 다단계 도커파일 (Multi-stage Dockerfile)

# =========================1. 빌드 단계=========================
# JDK 24 + Alpine OS (초경량 리눅스) 이미지를 가져옵니다.
# 이 단계의 이름을 'build'로 정합니다.
FROM eclipse-temurin:24-jdk-alpine AS build
# 소스코드 전체를 /home/src로 복사합니다.
COPY . /home/src
# /home/src로 이동합니다.
WORKDIR /home/src
# RUN + 실행할 명령어
# root가 소유자(본인)에게 gradlew 실행 권한(x: execute)을 부여합니다.
RUN chmod +x ./gradlew
# rwxrwxrwx 소유자/그룹/기타 사용자
# 내장된 gradlew 스크립트로 빌드 수행!
RUN ./gradlew build --no-daemon -x test

# =========================2. 실행 단계=========================
# JRE(Java Runtime Enviroment): 실행 환경에 필요한 최소한의 기능으로만 구성된 패키지 (빌드 도구 X)
# Eclipse사의 Temurin JRE 24 + 알파인 리눅스
FROM eclipse-temurin:24-jre-alpine
# 'build' 단계로부터 결과물(*.jar)을 가져와 루트 경로에 app.jar라는 이름으로 복사합니다.
COPY --from=build /home/src/build/libs/*.jar app.jar
# 컨테이너가 켜지면 실행할 명령어
# ENTRYPOINT에 3대장 다이어트 옵션(-Xmx, -XX:MaxMetaspaceSize, -Xss) 추가!
# 차례대로 Heap 256MB, Metaspace 128MB, Thread Stack 256KB 다이어트 명령어!
ENTRYPOINT ["java", "-Xmx256m", "-XX:MaxMetaspaceSize=128m", "-Xss256k", "-jar", "app.jar"]


