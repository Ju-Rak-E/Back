package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.RadiusAreaRequestDto;
import com.example.spring.rmago.dto.PlaceDto;
import com.example.spring.rmago.dto.Recommend.RecommendRequestDto;
import com.example.spring.rmago.dto.Recommend.RecommendedPlaceDto;
import com.example.spring.rmago.entity.Place;
import com.example.spring.rmago.service.PlaceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
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
@Slf4j
public class PlaceController {

    private final PlaceService placeService;

    @GetMapping("/area")
    public ResponseEntity<List<RecommendedPlaceDto>> getNearbyTourSpots(@RequestBody RecommendRequestDto request) {
        double lat = request.getLatitude();
        double lng = request.getLongitude();
        int radiusMeters = (int) (request.getRadius() * 1000); // km → m 변환
        String category = request.getCategory();

        List<RecommendedPlaceDto> response = placeService
                .getRecommendedPlaces(lat, lng, radiusMeters, category)
                .block();

        if (response == null) response = Collections.emptyList();

        log.info("🎯 반환할 장소 개수: {}", response.size());
        return ResponseEntity.ok(response);
    }



}
