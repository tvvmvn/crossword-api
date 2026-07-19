package com.example.crossword_api.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.crossword_api.service.CacheService;
import com.example.crossword_api.service.PuzzleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuzzleScheduler {

  private final CacheService cacheService;
  
  private final PuzzleService puzzleService;

  // 1단계: 전날 밤 10시에 백그라운드에서 "내일 자정(즉 다음날)에 오픈될 퍼즐"을 미리 랜덤 생성하여 DB에 넣습니다.
  // (유저가 아무도 신경 안 쓰는 시간에 조용히 연산하고, 필요시 관리자가 미리 확인 및 수정할 수 있게 유도)
  @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Seoul")
  public void generateTomorrowPuzzleAutomatically() {

    LocalDate tomorrow = LocalDate.now(ZoneId.of("Asia/Seoul")).plusDays(1);
    
    log.info("🕒 [자동 생성 스케줄러] 내일({}) 퍼즐을 생성합니다", tomorrow);
    
    try {
      puzzleService.createPuzzle(tomorrow);
      
      log.info("✅ 내일자 퍼즐 자동 생성 완료!");

    } catch (Exception e) {
      log.error("❌ 내일자 퍼즐 자동 생성 실패: ", e);
    }
  }

  // 매일 자정 정각 00시 00분 00초 작동 (한국 시간 기준)
  // 새로운 퍼즐로 캐시를 갱신합니다.
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void refreshDailyPuzzleCache() {

    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    log.info("🕒 [스케줄러] 자정 정각 데일리 퍼즐 캐시 갱신을 시작합니다: {}", today);

    try {
      // 1. 구형 캐시 삭제
      cacheService.clearAllPuzzleCaches();

      // 2. 새로운 오늘자 퍼즐 캐시에 주입 (Cache Warming)
      cacheService.forceCacheTodayPuzzle(today);

      log.info("✅ 오늘의 퍼즐 캐시 갱신이 완료되었습니다!");
      
    } catch (Exception e) {
      log.error("❌ 자정 퍼즐 캐시 갱신 실패! (DB 등록 확인 필요)", e);
    }
  }
}