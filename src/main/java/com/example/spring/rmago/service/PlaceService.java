package com.example.spring.rmago.service;

import com.example.spring.rmago.dto.PlaceDto;
import com.example.spring.rmago.dto.Recommend.RecommendedPlaceDto;
import com.example.spring.rmago.entity.Place;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PlaceService {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final String encodedServiceKey;

    public PlaceService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper, @Value("${tourapi.service-key}") String serviceKey) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
        this.encodedServiceKey = serviceKey;
    }

    /**
     * 위치 기반 관광지 조회 API 호출
     */
    public Mono<PlaceDto> fetchLocationBasedTourList(double latitude, double longitude, int radius, int contentTypeId) {
        final int radiusToUse;
        if (radius <= 0) {
            log.warn("⚠ 반경이 0 이하입니다. 기본값 1000m로 대체합니다.");
            radiusToUse = 1000;
        } else {
            radiusToUse = radius;
        }

        // URL 문자열을 직접 만들고, WebClient가 인코딩하지 못하게 URI 객체로 변환
        String finalUrl = String.format(
                "https://apis.data.go.kr/B551011/KorService2/locationBasedList2?serviceKey=%s&MobileOS=AND&MobileApp=Rmago&mapX=%f&mapY=%f&radius=%d&contentTypeId=%d&numOfRows=10&_type=json",
                this.encodedServiceKey, longitude, latitude, radiusToUse, contentTypeId
        );

        log.info("📡 [API 요청] 최종 URL: {}", finalUrl);

        URI uri;
        try {
            uri = new URI(finalUrl);
        } catch (URISyntaxException e) {
            log.error("❌ 잘못된 URI 문법입니다: {}", finalUrl, e);
            return Mono.error(e);
        }

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(responseBody -> log.info("📡 [API 응답] 수신: {}", responseBody.substring(0, Math.min(responseBody.length(), 200))))
                .flatMap(responseBody -> {
                    if (responseBody.startsWith("<")) {
                        log.error("❌ API 서버가 XML 에러를 반환했습니다:\n{}", responseBody);
                        return Mono.error(new RuntimeException("API Server returned an XML error. Check Service Key and request parameters."));
                    }
                    try {
                        PlaceDto placeDto = objectMapper.readValue(responseBody, PlaceDto.class);
                        log.info("✅ 응답 파싱 성공.");
                        return Mono.just(placeDto);
                    } catch (Exception e) {
                        log.error("❌ JSON 응답 파싱 실패: {}", e.getMessage(), e);
                        return Mono.error(e);
                    }
                });
    }


    /**
     * 위도/경도, 반경, 카테고리로 장소 리스트 조회
     */
    public Mono<List<RecommendedPlaceDto>> getRecommendedPlaces(double latitude, double longitude, int radiusMeters, String category) {
        int contentTypeId = resolveContentTypeId(category);

        return fetchLocationBasedTourList(latitude, longitude, radiusMeters, contentTypeId)
                .map(dto -> {
                    List<RecommendedPlaceDto> result = new ArrayList<>();
                    if (dto == null || dto.getResponse() == null || dto.getResponse().getBody() == null || dto.getResponse().getBody().getItems() == null) {
                        log.warn("❌ 응답 데이터의 Items 또는 상위 구조가 null입니다.");
                        return result;
                    }

                    List<PlaceDto.ItemDto> items = dto.getResponse().getBody().getItems().getItem();
                    if (items == null) {
                        log.info("📍 API 응답에 해당하는 장소가 없습니다.");
                        return result;
                    }

                    log.info("📍 좌표 기반 검색 결과 수: {}", items.size());

                    for (PlaceDto.ItemDto item : items) {
                        if (item.getMapx() == null || item.getMapy() == null) continue;

                        double dist = calculateDistanceKm(latitude, longitude, item.getMapy(), item.getMapx());

                        RecommendedPlaceDto dtoItem = RecommendedPlaceDto.builder()
                                .name(item.getTitle())
                                .category(category)
                                .lat(item.getMapy()) // ✅ 위도
                                .lng(item.getMapx()) // ✅ 경도
                                .distanceKm(dist)
                                .description("")
                                .build();

                        result.add(dtoItem);
                    }
                    return result;
                });
    }


    private double calculateDistanceKm(double lat1, double lng1, double lat2, double lng2) {
        final int earthRadius = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return Math.round(earthRadius * c * 100.0) / 100.0;
    }




    /**
     * 카테고리 문자열 → contentTypeId 변환
     */
    public int resolveContentTypeId(String category) {
        if (category == null || category.isBlank()) {
            log.warn("⚠ category가 null 또는 비어있음 → 기본값 '관광지(39)' 사용");
            return 25;
        }

        return switch (category.toLowerCase()) {
            case "관광지" -> 12;
            case "문화시설" -> 14;
            case "축제공연행사" -> 15;
            case "여행코스" -> 25;
            case "레포츠" -> 28;
            case "숙박" -> 32;
            case "쇼핑" -> 38;
            case "음식점" -> 39;
            default -> {
                log.warn("⚠ 알 수 없는 category '{}', 기본값 '관광지(12)' 사용", category);
                yield 39;
            }
        };
    }
}