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
  // 0 0 6 * * 5 => 0초 0분 6시 매일 매월 금요일
  // UTC 기준으로 매주 금요일 오전 6시에 다음주 퍼즐을 생성합니다
  @Scheduled(cron = "0 0 6 * * 5", zone = "GMT")
  public void preparePuzzle() {
    // UTC 기준 금요일.
    LocalDate friday = LocalDate.now();

    // 3(월) 4(화) 5(수) 6(목) 7(금) 8(토) 9(일)
    log.info("다음주 퍼즐 생성을 시작합니다 (예시)");
    for (int i = 3; i <= 9; i++) {
      LocalDate date = friday.plusDays(i);
      log.info("{}자 퍼즐을 생성했습니다", date);
    }
  }
}