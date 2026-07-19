package com.example.crossword_api.config;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.crossword_api.entity.Word;
import com.example.crossword_api.repository.WordRepository;
import com.example.crossword_api.service.PuzzleService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedDatabase implements CommandLineRunner {

  private final WordRepository wordRepository;

  private final PuzzleService puzzleService;

  @Override
  public void run(String... args) throws Exception {

    // DB에 저장할 단어 목록들
    List<Word> words = Arrays.asList(
      new Word("strawberry", "meaning for strawberry"),
      new Word("watermelon", "meaning for watermelon"),
      new Word("pineapple", "meaning for pineapple"),
      new Word("tomato", "meaning for tomato"),
      new Word("banana", "meaning for banana"),
      new Word("orange", "meaning for orange"),
      new Word("mango", "meaning for mango"),
      new Word("apple", "meaning for apple"),
      new Word("melon", "meaning for melon"),
      new Word("pear", "meaning for pear")
    );

    // 일괄 저장
    wordRepository.saveAll(words);

    // 퍼즐 생성
    LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));

    puzzleService.createPuzzle(today);

    log.info("씨드 데이터 저장을 완료했습니다.");
  }
}

