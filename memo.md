# Crosswors API 

## JDK/JRE/JVM

포함 관계: JDK ⊃ JRE ⊃ JVM (엔진 / 자동차 / 자동차 + 정비 도구)

JDK (Java Development Kit - 개발 키트)
- javac (컴파일러)
- javadoc, jdb 등 개발/디버깅 

JRE (Java Runtime Environment - 실행 환경)
- 자바 표준 라이브러리 (java.lang, java.util 등) 

JVM (Java Virtual Machine - 가상 머신)
- 바이트코드 번역, 메모리 관리, GC 수행


# Web Config

## allowedOriginPatterns("*")

allowCredentials(true)인 경우 allowedOrigins("*")는 사용불가합니다.
따라서 allowedOriginPatterns("*")와 함께 사용해야 합니다. Expo Go처럼 IP나 포트가 자주 바뀌는 개발 환경에서 매우 유용합니다.

## addMapping("/**")

CORS 정책을 적용할 Spring Boot 서버의 URL 패턴을 지정합니다.
"/**"는 루트 밑의 모든 경로(예: /api/v1/puzzles, /users, /login 등) 전체에 이 CORS 설정을 적용하겠다는 뜻입니다.
만약 특정 API에만 적용하고 싶다면 addMapping("/api/**") 형태로 범위를 제한할 수 있습니다.

## allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

클라이언트에서 허용할 HTTP 메서드 목록을 지정합니다. 명시하지 않은 HTTP 메서드(예: PATCH 등)로 요청이 들어오면 서버 차원에서 405 Method Not Allowed 또는 CORS 에러로 차단합니다.

브라우저는 실제 요청(GET, POST 등)을 보내기 전에 서버가 안전한지 확인하는 사전 요청(Preflight Request)을 OPTIONS 메서드로 먼저 보냅니다. 따라서 OPTIONS는 반드시 포함되어야 브라우저 통신이 안 터집니다.

## allowedHeaders("*")

클라이언트가 요청할 때 Authorization (JWT 토큰 등), Content-Type 등의 커스텀 헤더 사용을 허용합니다. "*"로 지정하면 클라이언트가 어떤 헤더를 실어 보내든 전부 허용합니다.

## allowCredentials(true)

클라이언트가 쿠키(Cookie), Authorization 헤더(HTTP 기본 인증), TLS 인증서 등의 민감한 인증 정보를 요청에 담아 서버로 전달할 수 있게 허용합니다. 기본값은 false입니다.

만약 앱/웹에서 세션 쿠키를 쓰거나 axios / fetch로 쿠키 및 인증 헤더를 주고받아야 한다면 반드시 true로 설정해야 합니다. true로 설정했기 때문에 앞서 설명한 allowedOriginPatterns을 사용해야 합니다.

## maxAge(3600)

3600 = 3600초 (1시간).
브라우저/클라이언트가 Preflight(사전 OPTIONS 요청)의 결과를 캐싱(보관)할 시간(초 단위)입니다. 클라이언트가 API를 호출할 때마다 매번 사전 OPTIONS 요청을 먼저 보내면 네트워크 낭비와 지연 시간이 발생합니다. 3600으로 설정해 두면, 클라이언트는 1시간 동안 Preflight 결과를 재사용하여 두 번째 요청부터는 OPTIONS 검사 없이 즉시 실제 API 요청만 빠르게 날립니다.