package com.example.crossword_api.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.crossword_api.entity.Puzzle;

public interface PuzzleRepository extends JpaRepository<Puzzle, Long> {
  //
  Optional<Puzzle> findByPublishDate(LocalDate publishDate);
}