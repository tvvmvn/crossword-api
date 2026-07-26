package com.example.crossword_api.service;

import java.time.LocalDate;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crossword_api.domain.PuzzleData;
import com.example.crossword_api.domain.PuzzleGenerator;
import com.example.crossword_api.dto.PuzzleResponse;
import com.example.crossword_api.entity.Puzzle;
import com.example.crossword_api.repository.PuzzleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
// 조회 연산(R)에서는 '읽기 전용'을 활성화 합니다.
// JPA가 변경 감지(Dirty Checking)를 위한 영속성 스냅샷을 만들지 않아 메모리가 절약됩니다.
@Transactional(readOnly = true)
public class PuzzleService {

  private final PuzzleGenerator puzzleGenerator;

  private final PuzzleRepository puzzleRepository;

  // ✅ 캐시에 퍼즐이 있으면 메서드 내부를 실행하지 않습니다.
  // CachePut과 마찬가지로 반환값을 캐시에 저장합니다.(중요)
  @Cacheable(value = "todayPuzzle") // 키: #date
  public PuzzleResponse getPuzzleByDate(LocalDate date) {
    log.info("🎯 [Cache Miss] 캐시에 퍼즐이 없어서 진짜 DB를 조회합니다! (날짜: {})", date);

    Puzzle puzzle = puzzleRepository.findByPublishDate(date)
      .orElseThrow(() -> new IllegalArgumentException("No puzzle found!"));

    return new PuzzleResponse(puzzle.getPuzzleData(), puzzle.getPublishDate());
  }
  
  // 메서드 실행 중 중간에 예외가 발생하면 롤백합니다.C/U/D 연산에서 필요함
  @Transactional
  // 무조건 메서드 내부를 실행!
  // CachePut: 반환한 값을 캐시에 저장합니다.
  @CachePut(value = "todayPuzzle") // 키: #date
  public PuzzleResponse savePuzzle(LocalDate date) {
    log.info("🔥 퍼즐을 DB에 저장하고 캐시에 장전합니다. (날짜: {})", date);

    PuzzleData puzzleData = puzzleGenerator.generate();
    Puzzle puzzle = puzzleRepository.save(new Puzzle(puzzleData, date));

    return new PuzzleResponse(puzzle.getPuzzleData(), puzzle.getPublishDate());
  }

  // 캐시를 비웁니다
  @CacheEvict(value = "todayPuzzle", allEntries = true)
  public void clearAllPuzzleCaches() {
    log.info("🧹 오래된 퍼즐 캐시를 메모리에서 전부 삭제합니다.");
  }
}