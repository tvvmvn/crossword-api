package com.example.crossword_api.dto;

import java.time.LocalDate;
import com.example.crossword_api.domain.PuzzleData;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(description = "유저 제공용 퍼즐 응답 객체 (정답 제외)")
@Getter
@AllArgsConstructor
public class PuzzleResponse {

  @Schema(description = "퍼즐 보드", example = "2차원 표 형태") 
  private PuzzleData puzzleData;

  @Schema(description = "출시 날짜", example = "2026-07-19") 
  private LocalDate publishDate;
}
