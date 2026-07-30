package com.example.crossword_api.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.crossword_api.service.PuzzleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuzzleScheduler {

  private final PuzzleService puzzleService;
  
  // 한국 시간 기준으로 매일 자정 정각 (00시 00분 00초)에 새로운 퍼즐로 캐시를 갱신합니다.
  @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
  public void refreshDailyPuzzleCache() {

    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    log.info("🕒 [스케줄러] 자정 정각 데일리 퍼즐 갱신을 시작합니다: {}", today);

    try {
      // 매모리로부터 구형 캐시(이전 퍼즐)를 삭제합니다
      puzzleService.clearAllPuzzleCaches();

      // 퍼즐을 생성하고 DB와 캐시에 저장합니다.
      puzzleService.savePuzzle(today);

      log.info("✅ 오늘의 퍼즐 캐시 갱신이 완료되었습니다!");
      
    } catch (Exception e) {
      log.error("❌ 퍼즐 캐시 갱신 실패!", e);
    }
  }
}