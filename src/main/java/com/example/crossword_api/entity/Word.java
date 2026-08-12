package com.example.crossword_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Word {
  
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer id;
  
  @Column(nullable = false)
  @NotBlank(message = "단어는 공백일 수 없습니다")
  private String name;

  @Column(nullable = false, length = 400)
  @NotBlank(message = "뜻은 공백일 수 없습니다")
  private String meaning;

  @Column(nullable = false)
  @NotBlank(message = "레벨은 공백일 수 없습니다")
  private String level;

  // 엔티티 생성용
  public Word(String name, String meaning, String level) {
    this.name = name;
    this.meaning = meaning;
    this.level = level;
  }
}
