package com.example.crossword_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.crossword_api.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer> {
  
  // 🚨 MySQL에서 지원하는 무작위 1건 조회 쿼리 활용
  // @Query(value = "SELECT * FROM movies ORDER BY RAND() LIMIT 1", nativeQuery = true)
  // Optional<Movie> findRandomMovie();
}
