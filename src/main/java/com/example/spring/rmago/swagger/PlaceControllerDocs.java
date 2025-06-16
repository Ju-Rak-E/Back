package com.example.spring.rmago.swagger;

import com.example.spring.rmago.dto.PlaceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Tour API", description = "한국관광공사 관광지 조회 API")
public interface PlaceControllerDocs {

    @Operation(summary = "지역 기반 관광지 조회", description = "지역 코드와 시군구 코드를 기준으로 관광지를 조회합니다.")
    ResponseEntity<PlaceDto> getAreaBasedTourList(
            @RequestParam String baseYm,
            @RequestParam String areaCd,
            @RequestParam String signguCd
    );

    @Operation(summary = "키워드 기반 관광지 검색", description = "키워드를 이용해 관광지를 검색합니다.")
    ResponseEntity<?> searchTourByKeyword(
            @RequestParam String baseYm,
            @RequestParam String keyword
    );
}
