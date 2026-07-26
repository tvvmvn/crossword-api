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

    // 한국 시간 기준 오늘 날짜를 퍼즐을 찾을 키로 사용합니다.
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
    // 캐시로부터 오늘 날짜에 해당하는 퍼즐을 가져옵니다.
    PuzzleResponse puzzleResponse = puzzleService.getPuzzleByDate(today);

    // 1. 현재 시간 및 오늘 밤 자정(23:59:59) 시간 구하기
    LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
    // endOfTody: 23:59:59.999999999
    LocalDateTime endOfToday = LocalDateTime.now().with(LocalTime.MAX); 
    // 2. 지금부터 자정까지 남은 시간(초) 계산
    long secondsUntilMidnight = Math.max(0, Duration.between(now, endOfToday).getSeconds());
    
    // 3. 남은 시간만큼만 Cache-Control 적용
    return ResponseEntity.ok()
        // maxAge(long maxAge, TimeUnit unit)
        .cacheControl(CacheControl.maxAge(secondsUntilMidnight, TimeUnit.SECONDS).cachePublic())
        .body(puzzleResponse);
  }
}

/*
maxAge(1, TimeUnit.DAYS)처럼 단순 24시간으로 박아버리면, 
말씀하신 대로 아침 8시에 접속한 사용자는 다음 날 아침 8시까지 어제 퍼즐을 보게 되는 치명적인 버그가 생깁니다. 
밤 12시(자정)에 퍼즐이 새로 바뀌었는데도 말이죠.

그래서 "매일 자정에 데이터가 바뀌는 기능"에는 24시간 고정이 아니라 
"지금 이 순간부터 오늘 밤 자정까지 남은 시간"을 계산해서 동적으로 maxAge를 설정해야 합니다.
*/