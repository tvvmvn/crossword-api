package com.example.crossword_api.scheduler;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

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
  
  // 크론 표현식: 초 / 분 / 시 / 일 / 월 / 요일 
  // 0 0 0 * * * => 0초 0분 0시 매일 매월 매요일 (매일 자정)
  @Scheduled(cron = "0 0 0 * * *", zone = "GMT")
  public void prepareTomorrowPuzzle() {
    
    // UTC 기준 다음날 
    LocalDate tomorrow = LocalDate.now().plusDays(1);

    log.info("[스케줄러] {}자 퍼즐 생성을 시작합니다", tomorrow);

    puzzleService.createPuzzle(tomorrow);

    log.info("[스케줄러] {}자 퍼즐 생성을 완료했습니다", tomorrow);
  }
}