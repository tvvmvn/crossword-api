package com.example.crossword_api.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Caption {
  
  private Integer wordId;

  private String word;

  private String content;

  private Integer label;
  
  private boolean acrossward;
}
