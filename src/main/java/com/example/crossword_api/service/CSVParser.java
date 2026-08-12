package com.example.crossword_api.service;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.crossword_api.entity.Word;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CSVParser {
  
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
      CSVReader csvReader = new CSVReaderBuilder(reader).build()) {

      // 첫번째 줄을 읽습니다
      String[] tokens = csvReader.readNext();

      while (tokens != null) {
        // tokens 예시: ['APPLE', '사과', 'Easy']
        String name = tokens[0].trim();
        String meaning = tokens[1].trim();
        String level = tokens[2].trim();
        // boolean isActive = Boolean.parseBoolean(tokens[3]);

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
