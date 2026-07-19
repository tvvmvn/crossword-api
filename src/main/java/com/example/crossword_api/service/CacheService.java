package com.example.crossword_api.service;

import java.time.LocalDate;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crossword_api.entity.Puzzle;
import com.example.crossword_api.repository.PuzzleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CacheService {

  private final PuzzleRepository puzzleRepository;

  // 캐시에 퍼즐이 있으면 메서드 내부를 실행하지 않습니다.
  @Cacheable(value = "todayPuzzle")
  public Puzzle getPuzzleByDate(LocalDate date) {

    log.info("🎯 [Cache Miss] 캐시에 퍼즐이 없어서 진짜 DB를 조회합니다! (날짜: {})", date);
    
    return puzzleRepository.findByPublishDate(date)
      .orElseThrow(() -> new IllegalArgumentException(date + " 자 퍼즐이 DB에 존재하지 않습니다."));
  }

  // 무조건 메서드 내부를 실행!
  @CachePut(value = "todayPuzzle")
  public Puzzle forceCacheTodayPuzzle(LocalDate date) {
    
    log.info("🔥 [Cache Warming] 데일리 퍼즐을 DB에서 조회해 캐시에 선제 장전합니다. (날짜: {})", date);
    
    return puzzleRepository.findByPublishDate(date)
      .orElseThrow(() -> new IllegalStateException("배포 오류: 오늘 자 퍼즐이 DB에 없습니다!"));
  }

  // 캐시를 비웁니다
  @CacheEvict(value = "todayPuzzle", allEntries = true)
  public void clearAllPuzzleCaches() {
    
    log.info("🧹 오래된 퍼즐 캐시를 메모리에서 전부 삭제합니다.");
  }
}