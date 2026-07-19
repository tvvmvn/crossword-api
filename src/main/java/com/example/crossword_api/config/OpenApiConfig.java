package com.example.crossword_api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {

    Info info = new Info()
        .title("십자말 풀이 앱 API 명세서")
        .description("매일 자정 갱신되는 데일리 십자말 퍼즐 API")
        .version("v1.0.0");
    
    return new OpenAPI().info(info);
  }
}