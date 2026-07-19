package com.example.crossword_api.domain;

import lombok.Getter;

@Getter
public class Board {

  private int rowCount;

  private int colCount;

  private Cell[][] grid;

  public Board(int rowCount, int colCount) {
    this.rowCount = rowCount;
    this.colCount = colCount;
    this.grid = new Cell[rowCount][colCount];
  }

  // utils
  public Cell get(int r, int c) {
    if (isValidCrds(r, c)) {
      return grid[r][c];
    }
    return null;
  }

  public void add(int r, int c, Cell cell) {
    if (isValidCrds(r, c)) {
      grid[r][c] = cell;
    }
  }

  public boolean notEmpty(int r, int c) {
    if (isValidCrds(r, c)) {
      return grid[r][c] != null;
    }
    return false;
  }

  public boolean isValidCrds(int r, int c) {
    return r >= 0 && r < rowCount && c >= 0 && c < colCount;
  }
}