package com.example.crossword_api.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.crossword_api.domain.PuzzleData;
import com.example.crossword_api.domain.PuzzleGenerator;
import com.example.crossword_api.entity.Puzzle;
import com.example.crossword_api.entity.Word;
import com.example.crossword_api.repository.PuzzleRepository;
import com.example.crossword_api.repository.WordRepository;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeedDatabase implements CommandLineRunner {

  private final WordRepository wordRepository;

  private final PuzzleRepository puzzleRepository;

  private final PuzzleGenerator puzzleGenerator;

  @Override
  // 만약 단어는 성공적으로 DB에 저장했는데 퍼즐 생성 도중에 오류가 발생하면 롤백함!
  @Transactional
  /*
  # throws Exception
  CSV 데이터를 읽어 DB에 넣는 필수 시딩(Seed) 작업에 실패했는데, 프로그램이 억지로 켜지면 어떻게 될까요? 
  데이터가 없는 빈 껍데기 상태로 서비스가 돌아가서 나중에 더 큰 버그가 생깁니다.
  run() 메서드 밖으로 Exception을 던져버리면, 스프링 부트 프레임워크가 이 예외를 전달받고 
  "아, 초기화 실패했구나!" 하고 애플리케이션 실행을 안전하게 중단(Shutdown)시킵니다.
  */
  public void run(String... args) throws Exception {
    // CSV로부터 단어를 DB에 저장합니다.
    if (wordRepository.count() == 0) {
      log.info("CSV로부터 씨드 단어 생성을 시작합니다...");
      List<Word> words = convertCSVtoList("data/words.csv");
      // JPA saveAll로 한번에 벌크 저장
      wordRepository.saveAll(words);
      log.info("✅ 단어 {}개 DB 장전 완료!", words.size());
    }

    // 서버가 처음 작동하는 경우 오늘자 퀴즈를 DB에 추가해줍니다.
    if (puzzleRepository.count() == 0) {
      log.info("샘플 퍼즐을 생성합니다...");
      // 한국 시간을 기준으로 오늘의 퍼즐을 생성합니다.
      LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
      PuzzleData puzzleData = puzzleGenerator.generate();
      puzzleRepository.save(new Puzzle(puzzleData, today));
      log.info("퍼즐 생성 완료!");
    }    
  }

  // throws IOException or CsvValidationException
  public List<Word> convertCSVtoList(String csvPath) throws Exception { 
    // **CSV가 간단한 텍스트 파일 포멧이니까 초기화용으로 적합!**
    log.info("🚀 CSV 단어 데이터 초기 장전을 시작합니다...");

    // 임시로 단어들을 담을 바구니
    List<Word> words = new ArrayList<>();
    // ClassPathResource는 프로젝트의 src/main/resources 폴더를 기준(Root)으로 상대 경로를 찾습니다.
    ClassPathResource resource = new ClassPathResource(csvPath);

    try (
      // 파일이나 네트워크에서 데이터를 아무런 해석 없이 단순 바이트(byte, 0과 1) 단위로 받아오는 통로입니다.
      InputStream inputStream = resource.getInputStream();
      // InputStream이 가져온 0과 1의 바이트 덩어리를 사람이 읽을 수 있는 '문자(Character/Text)'로 번역(디코딩)합니다.
      InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
      // InputStreamReader가 번역해 준 텍스트 덩어리를 읽어서 CSV 포맷 규칙(쉼표, 따옴표, 줄바꿈)에 맞춰 단어별로 쪼개주는 역할을 합니다.
      CSVReader csvReader = new CSVReaderBuilder(reader)
          .withSkipLines(1) // 맨 첫 번째 줄(헤더)을 스킵합니다.
          .build()) {

      // 첫번째 줄을 읽습니다
      String[] tokens = csvReader.readNext();

      while (tokens != null) {
        // tokens 예시: ['APPLE', '사과', 'Easy']
        String name = tokens[0].trim();
        String meaning = tokens[1].trim();
        String level = tokens[2].trim();

        // DB에 저장
        words.add(new Word(name, meaning, level));
        
        // 다음 줄을 읽습니다.
        tokens = csvReader.readNext();
      }
      return words;
      
    } catch (Exception e) {
      log.error("CSV 파일 읽기 실패!", e);
      throw e;
    }
  }
}

/*
# 주 스트림
파일이나 메모리에 직접 빨대를 꽂는 1차 클래스들입니다.

1. 바이트 스트림 (Byte Stream)
크기: 1byte (0과 1의 원시 데이터)
대상: 이미지, 동영상, 음악, 압축파일, 텍스트 등 모든 파일
이름: ...InputStream / ...OutputStream

2. 문자 스트림 (Character Reader/Writer)
크기: 2byte (한글, 영어 등 텍스트 문자로 해석)
대상: 오직 텍스트 파일(.txt, .csv, .json 등)
이름: ...Reader / ...Writer


# 보조 스트림
주 스트림 혼자서는 성능이 떨어지거나 문자 번역이 안 되므로, 주 스트림을 감싸서 도와주는 2차 클래스들입니다.

1. InputStreamReader, OutputStreamReader
바이트 ➔ 문자 변환 보조 스트림입니다.

2. BufferedReader, BufferedWriter
버퍼(Buffer, 메모리 임시 저장소)를 이용해 읽기/쓰기 성능을 극대화합니다.
*/