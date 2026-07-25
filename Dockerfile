# **빌드 스테이지**
# FROM gradle:8-jdk24: Gradle 8 버전과 JDK 24가 설치된 기본 도커 이미지를 다운로드하여 시작합니다.
# AS build: 이 빌드 단계를 build라는 이름의 별칭(Alias)으로 지정합니다. 
# 나중에 2단계 실행 스테이지에서 이 이름을 참조해 완성된 결과물(JAR)만 빼올 수 있게 됩니다.
FROM gradle:8-jdk24 AS build
# COPY . /home/src: 내 컴퓨터(호스트)의 현재 디렉터리(.)에 있는 모든 프로젝트 소스코드를 
# 컨테이너 내부의 /home/src 경로로 복사합니다.
# --chown=gradle:gradle: 파일들을 복사하면서, 해당 파일들의 소유자(Owner)와 그룹(Group)을 gradle로 설정합니다.
COPY --chown=gradle:gradle . /home/src
# WORKDIR: 작업 디렉터리를 /home/src로 이동합니다. (cd 역할)
# 이후 실행되는 명령어(RUN 등)는 모두 이 폴더 기준(상대 경로 기준점)으로 작동합니다.
WORKDIR /home/src
# RUN: 컨테이너 안에서 명령어를 실행합니다.
# gradle build: 스프링 부트 프로젝트를 빌드하여 build/libs/ 폴더 내에 .jar 파일을 생성합니다.
# --no-daemon: 도커 컨테이너 환경에서는 빌드가 끝나면 컨테이너가 닫히므로, 
# 메모리를 계속 갉아먹는 Gradle 데몬 프로세스를 켜두지 않도록 끕니다.
# -x test: 빌드 시 테스트 코드 실행(test 작업)을 스킵합니다. (빌드 속도 향상)
RUN gradle build --no-daemon -x test

# **실행 스테이지**
# FROM eclipse-temurin:24-jre-alpine: 실행 전용 베이스 이미지를 새로 불러옵니다.
# JRE: 빌드 도구(Gradle)나 컴파일러(JDK) 없이, 자바를 실행하기 위한 필수 라이브러리만 들어있어 용량이 매우 가볍습니다.
# alpine: 초경량 리눅스 배포판(Alpine Linux) 기반으로 만들어져 전체 이미지 크기를 대폭 줄여줍니다.
FROM eclipse-temurin:24-jre-alpine
# EXPOSE 8080: 이 컨테이너가 8080번 포트를 사용한다는 것을 명시하는 문서화 역할 기능입니다. 
# (실제 포트 포워딩은 컨테이너 실행 시 -p 8080:8080 옵션으로 수행합니다.)
EXPOSE 8080
# COPY --from=build: 1단계(AS build)에서 빌드했던 컨테이너 내부의 /home/src/build/libs/*.jar 파일만 쏙 뽑아옵니다.
# app.jar: 가져온 JAR 파일을 현재 실행 컨테이너의 루트 경로(/)에 app.jar라는 깔끔한 이름으로 저장합니다. 
# (1단계의 소스코드, 무거운 Gradle 도구 등은 모두 버리고 최종 결과물인 JAR만 가져오는 핵심 구문입니다.)
COPY --from=build /home/src/build/libs/*.jar app.jar
# ENTRYPOINT: 도커 컨테이너가 시작될 때 최초로 실행할 명령어를 지정합니다.
# 결과적으로 컨테이너가 켜지면 java -jar /app.jar 명령어가 실행되면서 스프링 부트 애플리케이션이 구동됩니다.
ENTRYPOINT ["java", "-jar", "/app.jar"]
