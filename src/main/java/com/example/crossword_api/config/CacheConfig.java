package com.example.crossword_api.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

  // 캐시 매니저를 빈으로 등록합니다
  @Bean
  public CacheManager cacheManager() {
    CaffeineCacheManager cacheManager = new CaffeineCacheManager("todayPuzzle");
    
    cacheManager.setCaffeine(Caffeine.newBuilder()
      // 데이터 저장 후 1일 뒤 만료됩니다
      .expireAfterWrite(1, TimeUnit.DAYS) 
      .maximumSize(10)); // 하루 한 건 위주이므로 작게 설정
    
    return cacheManager;
  }
}