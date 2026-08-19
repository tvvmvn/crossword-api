package com.example.crossword_api.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.example.crossword_api.entity.Word;

import lombok.NoArgsConstructor;
import lombok.AccessLevel;

// 기본 생성자를 private으로 설정해 외부에서 인스턴스를 생성할 수 없게 만듭니다.
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PuzzleGenerator {

  // 퍼즐을 생성하는 하나의 핵심 메서드
  public static PuzzleData generate(List<Word> words, int boardSize, int quizCount) {
    
    // 현재의 퀴즈 개수
    int currentQuizCount = 0;

    // PuzzleData의 내용물인 보드와 캡션
    Board board = new Board(boardSize, boardSize);
    List<Caption> captions = new ArrayList<>();

    // 우선 첫번째 단어를 가져옵니다
    Word firstWord = words.get(0);
    // 0과 9사이의 숫자 중에서 랜덤으로 한개를 추출합니다.
    int randomRow = ThreadLocalRandom.current()
        .nextInt(0, firstWord.getName().length());

    // 첫번째 단어를 보드에 집어넣습니다
    for (int z = 0; z < firstWord.getName().length(); z++) {
      Cell cell = new Cell();
      cell.setValue(firstWord.getName().charAt(z));
      cell.setAcrossId(firstWord.getId());
      board.add(randomRow, z, cell);
    }

    // 본격적으로 보드 생성을 시작합니다.
    for (int i = 1; i < words.size(); i++) {

      // 퀴즈 개수가 지정한 목표를 달성했으므로 빠져나옵니다
      if (currentQuizCount >= quizCount) {
        break;
      }

      // DB로부터 추출한 각각의 단어
      Word word = words.get(i);

      // 셀 순회하기
      iteratingCells: for (int r = 0; r < board.getRowCount(); r++) {
        for (int c = 0; c < board.getColCount(); c++) {
          // 빈 셀은 무시합니다
          if (!board.notEmpty(r, c)) {
            continue;
          }

          // 현재 셀이 속한 퀴즈가 어떤 방향(가로/세로)인지 판단합니다
          boolean across = board.notEmpty(r, c - 1) || board.notEmpty(r, c + 1);
          boolean downward = board.notEmpty(r - 1, c) || board.notEmpty(r + 1, c);
          
          // 이미 크로스된 셀은 무시
          if (across && downward) {
            continue;
          }

          // 셀에 현재 가지고있는 단어를 추가할 수 있는지 확인해보자.
          iteratingLetters: for (int l = 0; l < word.getName().length(); l++) {
            // 단어와 셀에 일치하는 문자가 있어. 따라서 가능성 있어
            if (board.get(r, c).getValue() == word.getName().charAt(l)) {
              // 현재 셀의 문자가 세로 퀴즈에 속하므로 가로 방향으로 추가해야 해.
              if (downward) {
                int colStart = c - l;
                int colEnd = colStart + word.getName().length() - 1;

                // 이런, 단어가 보드 밖으로 삐져나가는 군.
                if (!board.isValidCrds(r, colStart) || !board.isValidCrds(r, colEnd)) {
                  continue;
                }

                // 단어 위/아래가 이미 점유된 셀이라 불가능!
                if (board.notEmpty(r, colStart - 1) || board.notEmpty(r, colEnd + 1)) {
                  continue;
                }

                // 이제 새 단어를 추가할 자리의 주변을 살펴보자
                for (int k = 0; k < word.getName().length(); k++) {
                  // 일단 단어의 진행 방향이 비어있지 않아.
                  if (board.notEmpty(r, colStart + k)) {
                    // 두 문자가 같으면 괜찮지만 다르다면? 실패!
                    if (board.get(r, colStart + k).getValue() != word.getName().charAt(k)) {
                      continue iteratingLetters;
                    }
                  // 단어의 진행 방향이 비어있어
                  } else {
                    // 하지만 왼쪽이나 오른쪽이 점유되었다면? 실패!
                    if (board.notEmpty(r - 1, colStart + k) || board.notEmpty(r + 1, colStart + k)) {
                      continue iteratingLetters;
                    }
                  }
                }

                // 모든 조건을 합격했으니 이제 단어를 보드에 추가하자
                for (int p = 0; p < word.getName().length(); p++) {
                  // 이미 점유된 셀인 경우
                  if (board.notEmpty(r, colStart + p)) {
                    // 기존 셀에 가로 단어ID만 추가해주면 끝
                    board.get(r, colStart + p).setAcrossId((word.getId()));
                  // 빈 셀을 다루는 경우
                  } else {
                    // 셀을 만들고 값, 단어ID를 할당한 다음 그리드에 추가하자.
                    Cell cell = new Cell();
                    cell.setValue(word.getName().charAt(p));
                    cell.setAcrossId(word.getId());
                    board.add(r, colStart + p, cell);
                  }
                }

                // 퀴즈 개수를 하나 증가시키자.
                currentQuizCount++;
                // 단어가 이미 추가되었으니 나머지 셀들은 보면 안되겠지 (또 추가될 수 있잖아)
                break iteratingCells;
              }

              // 셀이 가로 퀴즈에 속하므로, 이번엔 세로로 단어를 추가할거야. 이하 로직은 같아.
              if (across) {
                int rowStart = r - l;
                int rowEnd = rowStart + word.getName().length() - 1;

                if (!board.isValidCrds(rowStart, c) || !board.isValidCrds(rowEnd, c)) {
                  continue;
                }

                if (board.notEmpty(rowStart - 1, c) || board.notEmpty(rowEnd + 1, c)) {
                  continue;
                }

                for (int k = 0; k < word.getName().length(); k++) {
                  if (board.notEmpty(rowStart + k, c)) {
                    if (board.get(rowStart + k, c).getValue() != word.getName().charAt(k)) {
                      continue iteratingLetters;
                    }
                  } else {
                    if (board.notEmpty(rowStart + k, c - 1) || board.notEmpty(rowStart + k, c + 1)) {
                      continue iteratingLetters;
                    }
                  }
                }

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

    // 보드에 라벨링 & 캡션 만들기
    int label = 1;
  // 단어가 여기서 다시 필요해. 빨리 찾기 위해 단어맵을 만들자.
    Map<Integer, Word> wordsMap = words.stream()
        .collect(Collectors.toMap(word -> word.getId(), word -> word));

    // 셀 순회를 시작하자
    for (int r = 0; r < board.getRowCount(); r++) {
      for (int c = 0; c < board.getColCount(); c++) {

        // 빈 셀은 패스!
        if (!board.notEmpty(r, c)) {
          continue;
        }

        // 가로 또는 세로로 시작되는 셀인지 판단하고
        boolean acrossStart = !board.notEmpty(r, c - 1) && board.notEmpty(r, c + 1);
        boolean downStart = !board.notEmpty(r - 1, c) && board.notEmpty(r + 1, c);

        // 방향과 상관없이 퀴즈가 시작되는 셀은 라벨이 필요해
        if (acrossStart || downStart) {
          board.get(r, c).setLabel(label++);
        }

        // 가로 퀴즈가 시작되는 셀인 경우
        if (acrossStart) {
          Word word = wordsMap.get(board.get(r, c).getAcrossId());

          // 캡션 생성 및 추가!
          Caption caption = new Caption();
          caption.setWordId(word.getId());
          caption.setWord(word.getName());
          caption.setContent(word.getMeaning());
          caption.setLabel(board.get(r, c).getLabel());
          caption.setAcrossward(true);
          captions.add(caption);
        }

        // 세로 퀴즈가 시작되는 셀이 경우
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

    // 완성된 퍼즐 데이터를 돌려주자
    return new PuzzleData(board.getGrid(), captions);
  }
}
