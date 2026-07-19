package com.example.crossword_api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;

@Entity
public class Word {
  
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Integer id;
  
  private String name;

  @Lob
  private String meaning;

  public Word() {}

  public Word(String name, String meaning) {
    this.name = name;
    this.meaning = meaning;
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getMeaning() {
    return meaning;
  }
}
