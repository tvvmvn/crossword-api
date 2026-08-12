package com.example.crossword_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  // CORS 멤버에 등록할 클라이언트 주소
  @Value("${spring.application.client}")
  private String clientUrl;

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    registry
        // 서버의 전체 경로를 대상으로 CORS가 가능하도록 합니다.
        .addMapping("/**") 
        // 모든 클라이언트의 요청을 허용합니다
        .allowedOriginPatterns( "*") 
        // preflight 요청은 OPTIONS 메서드로 전송됩니다
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        // 클라이언트가 요청할 때 Custom Header(Authorization, Content-Type 등)를 허용합니다.
        .allowedHeaders("*")
        // 쿠키/인증 헤더를 허용합니다
        .allowCredentials(true)
        .maxAge(3600);
  }
}

