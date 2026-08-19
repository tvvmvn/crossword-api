package com.example.crossword_api.controller;

import java.time.LocalDate;
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
  @Operation(summary = "오늘의 퍼즐 조회 (유저용)", description = "오늘 날짜에 해당하는 퍼즐을 조회합니다.")
  @ApiResponse(
    responseCode = "200", 
    description = "조회 성공", 
    content = @Content(schema = @Schema(implementation = PuzzleResponse.class))
  )
  @GetMapping("/puzzle")
  public ResponseEntity<PuzzleResponse> getTodayPuzzle() {
    // 한국 시간 기준 오늘의 날짜를 구합니다. 값: 7/30
    LocalDate today = LocalDate.now(ZoneId.of(AppConstants.TIME_ZONE));
    // 7/30일자 퍼즐을 찾습니다. 
    PuzzleResponse puzzleResponse = puzzleService.getPuzzleByDate(today);
    
    log.info("원본 서버가 퍼즐을 전송합니다.");
    // 남은 시간만큼만 maxAge(최대 수명) 적용합니다. maxAge: 1일
    return ResponseEntity.ok()
        // maxAge(최대 수명, 값의 단위)
        .cacheControl(CacheControl.maxAge(1, TimeUnit.DAYS).cachePublic())
        .body(puzzleResponse);
  }

  // 비밀 요청: 즉시 퍼즐을 생성해 DB에 저장하기 (오늘자 있으면 건너뜀)
  @GetMapping("/add")
  public ResponseEntity<String> createNewPuzzle() {
    
    LocalDate today = LocalDate.now(ZoneId.of(AppConstants.TIME_ZONE));
    puzzleService.createPuzzle(today);

    log.info("퍼즐 생성 요청을 처리했습니다");

    return ResponseEntity.ok().body("Done!");
  }
}

/*
# maxAge(1, TimeUnit.DAYS): 
CDN이 이 퍼즐 데이터를 딱 1일(86400초) 동안 엣지 서버에 들고 있게 만들고, 
이 기간 동안은 원본 서버를 거치지 않고 바로 0초 만에 응답하도록 지시합니다.

# cachePublic(): 
프락시 서버나 CDN(Railway Edge 등)이 이 응답을 공용 캐시로 안전하게 저장(public)할 수 있게 합니다.
*/