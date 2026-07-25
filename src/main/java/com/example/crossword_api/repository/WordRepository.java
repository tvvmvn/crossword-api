package com.example.crossword_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.crossword_api.entity.Word;

public interface WordRepository extends JpaRepository<Word, Integer> {
  
  // ORDER BY RAND()는 JPA에서 지원하지 않아 네이티브 쿼리를 사용합니다.
  // 서브쿼리가 FROM 절에 들어갈 때는 별칭이 필수입니다.
  @Query(value = """
      SELECT * FROM (
          SELECT * FROM word
          WHERE CHAR_LENGTH(name) <= :length
          ORDER BY RAND()
          LIMIT :count
      ) AS random_words
      ORDER BY CHAR_LENGTH(name) DESC, name ASC
      """, nativeQuery = true)
  List<Word> findRandomWordsWithMaxLength(@Param("length") int length, @Param("count") int count);
}
