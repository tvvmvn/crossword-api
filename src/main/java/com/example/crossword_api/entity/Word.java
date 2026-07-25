package com.example.crossword_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
public class Word {
  
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer id;
  
  private String name;

  @Lob // LONGTEXT (MySQL)
  private String meaning;

  private String level;

  // 엔티티 생성용
  public Word(String name, String meaning, String level) {
    this.name = name;
    this.meaning = meaning;
    this.level = level;
  }
}
