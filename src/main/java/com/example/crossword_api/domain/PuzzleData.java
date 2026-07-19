package com.example.crossword_api.domain;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PuzzleData {
  
  private Cell[][] grid;

  private List<Caption> captions;
}
