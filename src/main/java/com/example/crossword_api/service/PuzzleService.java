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
// 조회 연산(R)에서는 '읽기 전용'을 활성화 합니다. (기본값 false)
// JPA가 변경 감지(Dirty Checking)를 위한 영속성 스냅샷을 만들지 않아 메모리가 절약됩니다.
// @Transactional(readOnly = true)
public class PuzzleService {

  private final WordRepository wordRepository;

  private final PuzzleRepository puzzleRepository;

  public PuzzleResponse getPuzzleByDate(LocalDate date) {
    // DB에 오늘자 퍼즐이 있으면 가져오고 없으면 만듭니다 (Lazy Loading)
    Puzzle puzzle = puzzleRepository.findByPublishDate(date)
        .orElseGet(() -> {
          log.info("[development] DB에 퍼즐이 없어 새롭게 생성합니다.");
          return createPuzzle(date);
        });

    // DTO 반환
    return new PuzzleResponse(puzzle.getPuzzleData(), puzzle.getPublishDate());
  }

  public Puzzle createPuzzle(LocalDate date) {
    int boardSize = 10;
    int quizCount = 10;
    List<Word> words = wordRepository.getWordsForPuzzle(boardSize, quizCount * 4);

    PuzzleData puzzleData = PuzzleGenerator.generate(words, boardSize, quizCount);
    
    return puzzleRepository.save(new Puzzle(puzzleData, date));
  }
}