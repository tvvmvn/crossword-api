package com.example.crossword_api.controller;

import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.crossword_api.dto.PuzzleResponse;
import com.example.crossword_api.entity.Puzzle;
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

    // 한국 시간 기준으로 오늘의 날짜
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    // 캐시로부터 오늘 날짜에 해당하는 퍼즐을 가져옵니다.
    PuzzleResponse puzzleResponse = puzzleService.getPuzzleByDate(today);

    // 200 OK
    return ResponseEntity.ok(puzzleResponse);
  }
}
