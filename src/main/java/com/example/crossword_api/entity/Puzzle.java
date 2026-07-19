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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Puzzle {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 객체를 JSON 형태로 저장합니다.
  // @JdbcType(JsonJdbcType.class)
  @JdbcTypeCode(SqlTypes.JSON)
  @Column(columnDefinition = "json", nullable = false)
  private PuzzleData puzzleData;
  
  @Column(unique = true)
  private LocalDate publishDate;

  public Puzzle(PuzzleData puzzleData, LocalDate publishDate) {
    this.puzzleData = puzzleData;
    this.publishDate = publishDate;
  }
}
