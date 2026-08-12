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
      // 데이터 저장 후 3일 뒤 만료됩니다
      .expireAfterWrite(3, TimeUnit.DAYS) 
      // 오래된 날짜의 캐시는 메모리 한도 초과(LRU 알고리즘)로 알아서 밀려나 삭제
      .maximumSize(10)); 
    
    return cacheManager;
  }
}