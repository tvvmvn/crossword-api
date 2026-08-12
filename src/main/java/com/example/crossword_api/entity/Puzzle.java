package com.example.crossword_api.entity;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.crossword_api.domain.PuzzleData;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Puzzle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // JSON <> 객체 간의 변환(직렬화/역직렬화)을 수행합니다
  @JdbcTypeCode(SqlTypes.JSON)
  // json: 이 필드를 MySQL의 JSON 자료형으로 설정합니다.
  @Column(columnDefinition = "json", nullable = false)
  private PuzzleData puzzleData;
  
  @Column(nullable = false, unique = true)
  private LocalDate publishDate;

  public Puzzle(PuzzleData puzzleData, LocalDate publishDate) {
    this.puzzleData = puzzleData;
    this.publishDate = publishDate;
  }
}
