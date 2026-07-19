package com.example.crossword_api.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Cell {

  private Integer acrossId;

  private Integer downId;

  private Integer label;

  private char value;
}