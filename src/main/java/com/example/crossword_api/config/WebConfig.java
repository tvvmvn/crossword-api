package com.example.crossword_api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// API 키를 검증하는 절차는 간단하므로 스프링의 Web 라이브러리만으로 충분히 구현할 수 있습니다.
@Configuration
public class WebConfig implements WebMvcConfigurer {

  // CORS 멤버에 등록할 클라이언트 주소
  @Value("${spring.application.client}")
  private String clientUrl;

  @Override
  public void addCorsMappings(CorsRegistry registry) {

    // 서버의 전체 경로를 대상으로 CORS가 가능하도록 합니다.
    registry.addMapping("/**") 
        .allowedOrigins(clientUrl) 
        // .allowedOriginPatterns( "*") // 모든 요청을 허용합니다
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
        .allowedHeaders("*")
        .allowCredentials(true)
        .maxAge(3600);
  }
}
