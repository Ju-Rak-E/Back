package com.example.spring.rmago.service;

import com.example.spring.rmago.dto.PlaceDto;
import com.example.spring.rmago.entity.Place;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    @Value("${tourapi.service-key}")
    private String serviceKey;

    private final RestTemplate restTemplate;

    public PlaceDto fetchLocationBasedTourList(double latitude, double longitude, int radius, int contentTypeId) {
        if (radius <= 0) {
            log.warn("⚠ 반경이 0 이하입니다. 기본값 1000m로 대체합니다.");
            radius = 1000;
        }

        String encodedServiceKey;
        try {
            // 가져온 디코딩된 serviceKey를 URL에 안전하게 포함시키기 위해 인코딩
            encodedServiceKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8.toString());
            log.info("✔️ 인코딩된 서비스 키 (최종 URL에 포함될 값): {}", encodedServiceKey); // 확인용 로그
        } catch (Exception e) {
            log.error("❌ 서비스 키 인코딩 실패: {}", e.getMessage());
            return null;
        }

        String url = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/KorService2/locationBasedList2")
                .queryParam("serviceKey", encodedServiceKey) // 인코딩된 키 사용
                .queryParam("MobileOS", "AND")
                .queryParam("MobileApp", "Rmago")
                .queryParam("mapX", longitude)
                .queryParam("mapY", latitude)
                .queryParam("radius", radius)
                .queryParam("contentTypeId", contentTypeId)
                .queryParam("numOfRows", 10)
                .queryParam("_type", "json")
                .build(false)
                .toUriString();

        log.info("📡 [API 요청] 위치 기반 장소 검색: {}", url);

        try {
            HttpHeaders header = new HttpHeaders();
            header.set(HttpHeaders.ACCEPT, "application/json");
            HttpEntity<?> entity = new HttpEntity<>(header);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            // --- 핵심 수정 부분: 응답 상태 코드 및 헤더 확인 ---
            HttpStatus statusCode = (HttpStatus) response.getStatusCode();
            HttpHeaders responseHeaders = response.getHeaders();
            String responseBody = response.getBody();

            log.info("✅ HTTP 상태 코드: {}", statusCode);
            log.info("✅ 응답 헤더 Content-Type: {}", responseHeaders.getContentType());

            if (responseBody != null && !responseBody.isEmpty()) {
                log.info("📦 원본 응답 본문 (앞부분 500자): {}", responseBody.substring(0, Math.min(500, responseBody.length())));
                ObjectMapper mapper = new ObjectMapper();
                PlaceDto placeDto = mapper.readValue(responseBody, PlaceDto.class);

                if (placeDto != null) {
                    log.info("✅ PlaceDto 파싱 성공: {}", placeDto);
                    if (placeDto.getResponse() != null) {
                        log.info("✅ PlaceDto.response 성공: {}", placeDto.getResponse());
                        if (placeDto.getResponse().getBody() != null) {
                            log.info("✅ PlaceDto.response.body 성공: {}", placeDto.getResponse().getBody());
                            if (placeDto.getResponse().getBody().getItems() != null) {
                                log.info("✅ PlaceDto.response.body.items 성공. 항목 수: {}",
                                        placeDto.getResponse().getBody().getItems().getItem() != null ?
                                                placeDto.getResponse().getBody().getItems().getItem().size() : 0);
                            }
                        }
                    }
                }
                return placeDto;
            } else {
                log.warn("❌ API 응답 본문이 비어있습니다.");
                // HTTP 상태 코드가 200 OK인데 본문이 비었다면 서버 문제일 가능성 높음
                if (statusCode.is2xxSuccessful()) {
                    log.warn("⚠ 2xx 성공 상태 코드이나 응답 본문이 비어있습니다. 서버 또는 API 문제일 수 있습니다.");
                }
                return null;
            }

        } catch (Exception e) {
            log.error("❌ 위치 기반 장소 검색 실패: {}", e.getMessage());
            // 디버깅을 위해 스택 트레이스를 출력해볼 수도 있습니다.
            // log.error("❌ 위치 기반 장소 검색 실패", e);
            return null;
        }
    }

    /**
     * 위도/경도, 반경, 카테고리로 장소 조회
     */
    // 2. 위도/경도 기반 장소 리스트 생성
    public List<Place> getPlacesByCoordinate(double latitude, double longitude, int radiusMeters, String category) {
        int contentTypeId = resolveContentTypeId(category);

        PlaceDto dto = fetchLocationBasedTourList(latitude, longitude, radiusMeters, contentTypeId);
        List<Place> result = new ArrayList<>();

        if (dto == null || dto.getResponse() == null || dto.getResponse().getBody() == null) {
            log.warn("❌ 위도 경도, 반경 ,카테고리 기반 장소 검색 실패 - 응답 데이터 없음");
            return result;
        }

        List<PlaceDto.ItemDto> items = dto.getResponse().getBody().getItems().getItem();
        if (items == null) return result;

        log.info("📍 좌표 기반 검색 결과 수: {}", items.size());

        for (PlaceDto.ItemDto item : items) {
            if (item.getMapx() == null || item.getMapy() == null) continue;

            Place place = new Place();
            place.setName(item.getTitle());
            place.setAddress(item.getAddr1());
            place.setLng(item.getMapx());
            place.setLat(item.getMapy());
            place.setCategory(category);

            log.info("✅ 장소 등록: [{}] {} ({}, {})", category, place.getName(), place.getLat(), place.getLng());
            result.add(place);
        }

        return result;
    }

    // ✅ 이 메서드를 클래스의 바깥으로 꺼냄
    public int resolveContentTypeId(String category) {
        if (category == null || category.isBlank()) {
            log.warn("⚠ category가 null 또는 비어있음 → 기본값 '관광지(12)' 사용");
            return 12;
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
                yield 12;
            }
        };
    }
}