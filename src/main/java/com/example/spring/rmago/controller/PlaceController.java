package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.RadiusAreaRequestDto;
import com.example.spring.rmago.dto.PlaceDto;
import com.example.spring.rmago.service.PlaceService;
import com.example.spring.rmago.swagger.PlaceControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 한국관광공사에서 제공하는 OpenAPI를 호출하여
 * 지역 기반 또는 키워드 기반의 관광지 데이터를 프론트에 제공하는 컨트롤러
 *
 * 최초 작성자: 김병훈
 * 최초 작성일: 2025-06-15
 */
@RestController
@RequestMapping("/api/tour")
@RequiredArgsConstructor
public class PlaceController implements PlaceControllerDocs {

    private final PlaceService placeService;

    /**
     * [GET] /api/tour/area
     *
     * 지정된 baseYm, 지역 코드(areaCd), 시군구 코드(signguCd)를 기준으로
     * 지역 기반 관광지 리스트를 조회하는 엔드포인트입니다.
     *
     * @param baseYm   기준 년월 (YYYYMM 형식, 예: 202504)
     * @param areaCd   지역 코드 (예: 서울 = "1")
     * @param signguCd 시군구 코드 (예: 강남구 = "1")
     * @return 관광공사 OpenAPI에서 받아온 JSON 응답 (String 형태)
     */
    @GetMapping("/area")
    public ResponseEntity<PlaceDto> getAreaBasedTourList(
            @RequestParam String baseYm,
            @RequestParam String areaCd,
            @RequestParam String signguCd
    ) {
        // PlaceDto로 변환된 결과를 반환
        return ResponseEntity.ok(placeService.fetchAreaBasedTourList(baseYm, areaCd, signguCd));
    }

    /**
     * [GET] /api/tour/search
     *
     * 지정된 baseYm과 검색 키워드를 기준으로 관광지를 검색합니다.
     *
     * @param baseYm 기준 년월 (예: 202504)
     * @param keyword 검색 키워드 (예: "경복궁", "카페" 등)
     * @return 관광공사 OpenAPI JSON 응답
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchTourByKeyword(
            @RequestParam String baseYm,
            @RequestParam String keyword
    ) {
        return ResponseEntity.ok(placeService.fetchTourByKeyword(baseYm, keyword));
    }
    /**
     * [POST] /api/tour/multiple-areas
     *
     * 여러 시군구 코드 리스트를 기반으로 관광지 데이터를 조회합니다.
     * 요청 본문에는 baseYm과 sigunguCdList가 포함됩니다.
     */
    // ✅ [POST] 반경 내 관광지 목록 조회
    @PostMapping("/within-radius")
    public ResponseEntity<List<PlaceDto>> getTourListWithinRadius(@RequestBody RadiusAreaRequestDto request) {
        List<PlaceDto> result = placeService.fetchTourListWithinRadius(
                request.getBaseYm(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadius()
        );
        return ResponseEntity.ok(result);
    }
}
