package com.example.crossword_api.scheduler;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.crossword_api.config.AppConstants;
import com.example.crossword_api.service.PuzzleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PuzzleScheduler {

  private final PuzzleService puzzleService;
  
  // 한국 시간 기준으로 매일 새벽 4시에 새로운 퍼즐로 캐시를 갱신합니다.
  // 크론 표현식: 초 / 분 / 시 / 일 / 월 / 요일 
  // 0 0 4 * * * => 0초 0분 4시 매일 매월 모든-요일
  @Scheduled(cron = "0 0 4 * * *", zone = AppConstants.TIME_ZONE)
  public void refreshDailyPuzzleCache() {

    LocalDate today = LocalDate.now(ZoneId.of(AppConstants.TIME_ZONE));

    log.info("🕒 [스케줄러] 자정 정각 데일리 퍼즐 갱신을 시작합니다: {}", today);

    try {
      // 퍼즐을 생성하고 DB에 저장합니다
      puzzleService.createPuzzle(today);

      log.info("✅ 오늘의 퍼즐 캐시 갱신이 완료되었습니다!");
      
    } catch (Exception e) {
      log.error("❌ 퍼즐 캐시 갱신 실패!", e);
    }
  }
}