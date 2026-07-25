package com.example.crossword_api.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.crossword_api.entity.Word;
import com.example.crossword_api.repository.WordRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PuzzleGenerator {
  
  // 10 x 10 Board
  private final int boardSize = 10;

  // maximum quiz count per puzzle
  private final int maxQuizCount = 10;

  private final WordRepository wordRepository;
  
  public PuzzleData generate() {
    // save current quiz count
    int currentQuizCount = 0;

    // 글자 수가 10개 이하인 단어들을 문제 수 * 3개 만큼 우선 확보합니다.
    List<Word> words = wordRepository.findRandomWordsWithMaxLength(boardSize, maxQuizCount * 3);
    
    // 퍼즐 데이터
    Board board = new Board(boardSize, boardSize);
    List<Caption> captions = new ArrayList<>();

    // set first word
    Word firstWord = words.get(0);
    // get random number between 0 and 9
    int randomRow = ThreadLocalRandom.current()
      .nextInt(0, firstWord.getName().length());

    for (int z = 0; z < firstWord.getName().length(); z++) {
      Cell cell = new Cell();
      cell.setValue(firstWord.getName().charAt(z));
      cell.setAcrossId(firstWord.getId());
      board.add(randomRow, z, cell);
    }

    // creating board
    for (int i = 1; i < words.size(); i++) {

      // Done! let's get out
      if (currentQuizCount >= maxQuizCount) {
        break;
      }

      Word word = words.get(i);

      iteratingCells: for (int r = 0; r < board.getRowCount(); r++) {
        for (int c = 0; c < board.getColCount(); c++) {
          // ignore empty cell
          if (!board.notEmpty(r, c)) {
            continue;
          }

          boolean across = board.notEmpty(r, c - 1) || board.notEmpty(r, c + 1);
          boolean downward = board.notEmpty(r - 1, c) || board.notEmpty(r + 1, c);
          // acrossed already
          if (across && downward) {
            continue;
          }

          // let's compare words
          iteratingLetters: for (int l = 0; l < word.getName().length(); l++) {
            if (board.get(r, c).getValue() == word.getName().charAt(l)) {
              // let's add in across direction
              if (downward) {
                int colStart = c - l;
                int colEnd = colStart + word.getName().length() - 1;

                // out of board
                if (!board.isValidCrds(r, colStart) || !board.isValidCrds(r, colEnd)) {
                  continue;
                }

                // if not empty over or below this word
                if (board.notEmpty(r, colStart - 1) || board.notEmpty(r, colEnd + 1)) {
                  continue;
                }

                for (int k = 0; k < word.getName().length(); k++) {
                  // center is not empty
                  if (board.notEmpty(r, colStart + k)) {
                    // and not the same as existing character as well
                    if (board.get(r, colStart + k).getValue() != word.getName().charAt(k)) {
                      continue iteratingLetters;
                    }
                  } else {
                    // left or right is not empty
                    if (board.notEmpty(r - 1, colStart + k) || board.notEmpty(r + 1, colStart + k)) {
                      continue iteratingLetters;
                    }
                  }
                }

                // all passed, let's fill in
                for (int p = 0; p < word.getName().length(); p++) {
                  if (board.notEmpty(r, colStart + p)) {
                    board.get(r, colStart + p).setAcrossId((word.getId()));
                  } else {
                    Cell cell = new Cell();
                    cell.setValue(word.getName().charAt(p));
                    cell.setAcrossId(word.getId());
                    board.add(r, colStart + p, cell);
                  }
                }

                // word has been filled, so stop iterating rest cells
                currentQuizCount++;
                break iteratingCells;
              }

              // let's add a word in downward
              if (across) {
                int rowStart = r - l;
                int rowEnd = rowStart + word.getName().length() - 1;

                // out of board
                if (!board.isValidCrds(rowStart, c) || !board.isValidCrds(rowEnd, c)) {
                  continue;
                }

                // if not empty over or below this word
                if (board.notEmpty(rowStart - 1, c) || board.notEmpty(rowEnd + 1, c)) {
                  continue;
                }

                for (int k = 0; k < word.getName().length(); k++) {
                  // center is not empty
                  if (board.notEmpty(rowStart + k, c)) {
                    // also not the same as existing character
                    if (board.get(rowStart + k, c).getValue() != word.getName().charAt(k)) {
                      continue iteratingLetters;
                    }
                    // center is empty
                  } else {
                    // but left or right is not empty
                    if (board.notEmpty(rowStart + k, c - 1) || board.notEmpty(rowStart + k, c + 1)) {
                      continue iteratingLetters;
                    }
                  }
                }

                // all passed, let's fill in
                for (int p = 0; p < word.getName().length(); p++) {
                  if (board.notEmpty(rowStart + p, c)) {
                    board.get(rowStart + p, c).setDownId((word.getId()));
                  } else {
                    Cell cell = new Cell();
                    cell.setValue(word.getName().charAt(p));
                    cell.setDownId(word.getId());
                    board.add(rowStart + p, c, cell);
                  }
                }
                
                currentQuizCount++;
                break iteratingCells;
              }
            }
          }
        }
      }
    }

    // labeling board & creating captions
    int label = 1;
    Map<Integer, Word> wordsMap = words.stream()
        .collect(Collectors.toMap(word -> word.getId(), word -> word));

    for (int r = 0; r < board.getRowCount(); r++) {
      for (int c = 0; c < board.getColCount(); c++) {

        // skip empty cells
        if (!board.notEmpty(r, c)) {
          continue;
        }

        // cell to be labeled
        boolean acrossStart = !board.notEmpty(r, c - 1) && board.notEmpty(r, c + 1);
        boolean downStart = !board.notEmpty(r - 1, c) && board.notEmpty(r + 1, c);

        // add label
        if (acrossStart || downStart) {
          board.get(r, c).setLabel(label++);
        }

        // add acrossward caption
        if (acrossStart) {
          Word word = wordsMap.get(board.get(r, c).getAcrossId());

          Caption caption = new Caption();
          caption.setWordId(word.getId());
          caption.setWord(word.getName());
          caption.setContent(word.getMeaning());
          caption.setLabel(board.get(r, c).getLabel());
          caption.setAcrossward(true);

          captions.add(caption);
        }

        // add downward caption
        if (downStart) {
          Word word = wordsMap.get(board.get(r, c).getDownId());

          Caption caption = new Caption();
          caption.setWordId(word.getId());
          caption.setWord(word.getName());
          caption.setContent(word.getMeaning());
          caption.setLabel(board.get(r, c).getLabel());
          caption.setAcrossward(false);

          captions.add(caption);
        }
      }
    }

    return new PuzzleData(board.getGrid(), captions);
  }
}
