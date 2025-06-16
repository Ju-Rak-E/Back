package com.example.spring.rmago.util;


//최초 작성자: 김병훈
//최초 작성일: 2025-06-16
//시군구/시 코드를 가져오기 위한 서비스
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Getter
public class CodeMapperService {

    private final Map<String, String> areaCdMap = new HashMap<>();
    private final Map<String, String> signguCdMap = new HashMap<>();

    @PostConstruct
    public void init() throws Exception {
        // resources 디렉토리에 있는 엑셀 파일 로드
        InputStream is = getClass().getClassLoader().getResourceAsStream("area-code.xlsx");
        Workbook workbook = new XSSFWorkbook(is);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            if (row.getRowNum() == 0) continue; // 헤더 제외

            String areaCd = getCellValue(row.getCell(0));
            String areaNm = getCellValue(row.getCell(1));
            String sigunguCd = getCellValue(row.getCell(2));
            String sigunguNm = getCellValue(row.getCell(3));

            areaCdMap.putIfAbsent(areaNm, areaCd); // 시도 → areaCd
            String key = areaNm + "_" + sigunguNm;
            signguCdMap.put(key, sigunguCd);       // 시도+시군구 → sigunguCd
        }
    }

    private String getCellValue(Cell cell) {
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    public String getAreaCd(String areaNm) {
        return areaCdMap.getOrDefault(areaNm, "11"); // 기본: 서울
    }

    public String getSignguCd(String areaNm, String sigunguNm) {
        return signguCdMap.getOrDefault(areaNm + "_" + sigunguNm, "11110"); // 기본: 종로구
    }
}