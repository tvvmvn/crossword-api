package com.example.crossword_api.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crossword_api.domain.PuzzleData;
import com.example.crossword_api.domain.PuzzleGenerator;
import com.example.crossword_api.dto.PuzzleResponse;
import com.example.crossword_api.entity.Puzzle;
import com.example.crossword_api.entity.Word;
import com.example.crossword_api.repository.PuzzleRepository;
import com.example.crossword_api.repository.WordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
// 조회 연산(R)에서는 '읽기 전용'을 활성화 합니다.
// JPA가 변경 감지(Dirty Checking)를 위한 영속성 스냅샷을 만들지 않아 메모리가 절약됩니다.
@Transactional(readOnly = true)
public class PuzzleService {

  private final WordRepository wordRepository;

  private final PuzzleRepository puzzleRepository;

  public PuzzleResponse getPuzzleByDate(LocalDate date) {
    // DB 검색 
    Puzzle puzzle = puzzleRepository.findByPublishDate(date)
      .orElseThrow(() -> new IllegalArgumentException("이 날짜의 퍼즐이 DB에 없습니다!"));

    return new PuzzleResponse(puzzle.getPuzzleData(), puzzle.getPublishDate());
  }
  
  // 메서드 실행 중 중간에 예외가 발생하면 롤백합니다. C/U/D 연산에서 필요함
  @Transactional
  public void createPuzzle(LocalDate date) {
    log.info("🔥 퍼즐 생성을 시작합니다. (날짜: {})", date);

    if (puzzleRepository.existsByPublishDate(date)) {
      log.info("해당 날짜({})에 해당하는 퍼즐이 이미 존재합니다. ", date);
      return;
    }

    // 퍼즐 생성에 필요한 데이터들을 준비합니다
    int boardSize = 10; // 10x10 보드
    int quizCount = 10; // 퀴즈 10개 생성을 목표로 합니다
    // 글자 수가 10개 이하인 단어들을 퀴즈 개수 * 4개 만큼 여유있게 확보합니다.
    List<Word> words = wordRepository.getWordsForPuzzle(boardSize, quizCount * 4);
    if (words.size() < 1) {
      throw new RuntimeException("퍼즐 생성용 단어가 없습니다!");
    }

    // 퍼즐을 생성하고 DB에 저장합니다
    PuzzleData puzzleData = PuzzleGenerator.generate(words, boardSize, quizCount);
    puzzleRepository.save(new Puzzle(puzzleData, date));
    log.info("퍼즐 생성을 완료했습니다.");
  }
}

/*
# Spring 캐시 어노테이션의 설계 의도
 
Spring의 @Cacheable, @CachePut, @CacheEvict는 AOP(관점 지향 프로그래밍) 기반입니다. 
비즈니스 로직(Service)에 선언적으로 붙여서 "서비스의 비즈니스 결과물을 선언적으로 캐싱한다"는 목적으로 설계되었습니다.
*/