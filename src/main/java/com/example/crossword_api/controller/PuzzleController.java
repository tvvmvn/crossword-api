package com.example.crossword_api.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crossword_api.config.AppConstants;
import com.example.crossword_api.dto.PuzzleResponse;
import com.example.crossword_api.service.PuzzleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "🎯 Puzzle API", description = "퍼즐 조회 컨트롤러")
@RestController
@RequiredArgsConstructor
public class PuzzleController {

  private final PuzzleService puzzleService;
  
  // 1. 유저용: 오늘의 퍼즐 요청 (캐시 적용)
  @Operation(
    summary = "오늘의 퍼즐 조회 (유저용)",
    description = "오늘 날짜에 해당하는 퍼즐을 조회합니다. 캐싱이 적용되어 있어 응답 속도가 매우 빠릅니다."
  )
  @ApiResponse(
    responseCode = "200", 
    description = "조회 성공", 
    content = @Content(schema = @Schema(implementation = PuzzleResponse.class))
  )
  @GetMapping("/puzzle")
  public ResponseEntity<PuzzleResponse> getTodayPuzzle() {
    // 7/30일 오전 7시에 요청이 도착했다고 가정해보자. (출근길 플레이!)

    // 한국 시간 기준 오늘의 날짜를 구합니다. 값: 7/30
    LocalDate today = LocalDate.now(ZoneId.of(AppConstants.TIME_ZONE));
    // 7/30일자 퍼즐을 찾습니다. 스케줄러가 이미 자정에 만들어서 캐시에 저장해줬지롱
    PuzzleResponse puzzleResponse = puzzleService.getPuzzleByDate(today);

    // 요청이 도착한 날짜, 시간을 구합니다. 값: 7/30일 오전 7시
    LocalDateTime now = LocalDateTime.now(ZoneId.of(AppConstants.TIME_ZONE));
    // 자정 바로 전을 의미합니다. 값: 23:59:59 
    LocalDateTime endOfToday = now.with(LocalTime.MAX); 
    // 요청이 도착한 오전 7시부터 자정까지 남은 시간(초)를 계산합니다. 값: 16:59:59
    long secondsUntilMidnight = Math.max(0, Duration.between(now, endOfToday).getSeconds());
    
    // 남은 시간만큼만 maxAge(최대 수명) 적용합니다. maxAge: 16.59.59시간
    return ResponseEntity.ok()
        // maxAge(최대 수명, 값의 단위)
        .cacheControl(CacheControl.maxAge(secondsUntilMidnight, TimeUnit.SECONDS).cachePublic())
        .body(puzzleResponse);
  }

  // 비밀 요청: 즉시 퍼즐을 생성해 DB에 저장하기 (오늘자 있으면 건너뜀)
  @GetMapping("/add")
  public void createNewPuzzle() {
    LocalDate today = LocalDate.now(ZoneId.of(AppConstants.TIME_ZONE));
    puzzleService.createPuzzle(today);

    log.info("퍼즐 생성 요청을 처리했습니다");
  }
}

/*
maxAge(1, TimeUnit.DAYS)처럼 단순 24시간으로 박아버리면, 
말씀하신 대로 아침 8시에 접속한 사용자는 다음 날 아침 8시까지 어제 퍼즐을 보게 되는 치명적인 버그가 생깁니다. 
밤 12시(자정)에 퍼즐이 새로 바뀌었는데도 말이죠.

그래서 "매일 자정에 데이터가 바뀌는 기능"에는 24시간 고정이 아니라 
"지금 이 순간부터 오늘 밤 자정까지 남은 시간"을 계산해서 동적으로 maxAge를 설정해야 합니다.
*/