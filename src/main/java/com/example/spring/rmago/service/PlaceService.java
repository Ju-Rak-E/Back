package com.example.spring.rmago.service;

import com.example.spring.rmago.dto.PlaceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    @Value("${tourapi.service-key}")
    private String serviceKey;

    @Value("${naver.map.client-id}")
    private String naverClientId;

    @Value("${naver.map.secret-key}")
    private String naverSecretKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // ============================ Public APIs ============================

    public String getSigunguCodeFromNaver(double lat, double lng) {
        return querySigunguCode(lat, lng)
                .orElseThrow(() -> new RuntimeException("시군구 코드 조회 실패"));
    }

    public List<String> getSigunguCodesWithinRadius(double centerLat, double centerLng, double radiusMeters) {
        final int sampleCount = 60;
        final double earthRadius = 6371000.0;
        Set<String> result = new HashSet<>();

        for (int i = 0; i < sampleCount; i++) {
            double angle = 2 * Math.PI * i / sampleCount;
            double dx = radiusMeters * Math.cos(angle);
            double dy = radiusMeters * Math.sin(angle);

            double sampledLat = centerLat + (dy / earthRadius) * (180 / Math.PI);
            double sampledLng = centerLng + (dx / (earthRadius * Math.cos(Math.toRadians(centerLat)))) * (180 / Math.PI);

            querySigunguCode(sampledLat, sampledLng).ifPresent(result::add);
        }

        log.info("📍 반경 내 시군구 코드 수: {}, 목록: {}", result.size(), result);
        return new ArrayList<>(result);
    }

    public PlaceDto fetchAreaBasedTourList(String baseYm, String areaCd, String sigunguCd) {
        String url = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/TarRlteTarService1/areaBasedList1")
                .queryParam("serviceKey", UriUtils.encode(serviceKey, StandardCharsets.UTF_8))
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("MobileOS", "AND")
                .queryParam("MobileApp", "Rmago")
                .queryParam("baseYm", baseYm)
                .queryParam("areaCd", areaCd)
                .queryParam("signguCd", sigunguCd)
                .queryParam("_type", "json")
                .build().toUriString();

        return parsePlaceDtoFromUrl(url, areaCd, sigunguCd);
    }

    public PlaceDto fetchTourByKeyword(String baseYm, String keyword) {
        String url = UriComponentsBuilder.fromHttpUrl("https://apis.data.go.kr/B551011/TarRlteTarService1/searchKeyword1")
                .queryParam("serviceKey", UriUtils.encode(serviceKey, StandardCharsets.UTF_8))
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("MobileOS", "ETC")
                .queryParam("MobileApp", "Rmago")
                .queryParam("baseYm", baseYm)
                .queryParam("keyword", UriUtils.encode(keyword, StandardCharsets.UTF_8))
                .queryParam("_type", "json")
                .build().toUriString();

        return parsePlaceDtoFromUrl(url, null, null);
    }

    public List<PlaceDto> fetchTourListWithinRadius(String baseYm, double lat, double lng, double radius) {
        log.info("📡 반경 관광지 요청: baseYm={}, lat={}, lng={}, radius={}m", baseYm, lat, lng, radius);
        List<String> sigunguCdList = getSigunguCodesWithinRadius(lat, lng, radius);

        log.info("✅ 포함된 시군구 코드 리스트: {}", sigunguCdList);
        List<PlaceDto> results = new ArrayList<>();

        for (String sigunguCd : sigunguCdList) {
            if (sigunguCd.length() < 5) continue;
            String areaCd = sigunguCd.substring(0, 2);

            try {
                PlaceDto dto = fetchAreaBasedTourList(baseYm, areaCd, sigunguCd);
                results.add(dto);
                log.debug("✅ 관광지 조회 성공 - sigunguCd={}, 지역 개수={}",
                        sigunguCd, dto.getResponse().getBody().getItems().getItem().size());
            } catch (Exception e) {
                log.warn("❌ 관광지 조회 실패 - sigunguCd={}, 이유: {}", sigunguCd, e.getMessage());
            }
        }

        log.info("📦 전체 관광지 응답 수: {}", results.size());
        return results;
    }

    // ============================ Private Utils ============================

    private Optional<String> querySigunguCode(double lat, double lng) {
        try {
            String url = UriComponentsBuilder.fromHttpUrl("https://maps.apigw.ntruss.com/map-reversegeocode/v2/gc")
                    .queryParam("coords", lng + "," + lat)
                    .queryParam("output", "json")
                    .queryParam("orders", "admcode")
                    .build().toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-NCP-APIGW-API-KEY-ID", naverClientId);
            headers.set("X-NCP-APIGW-API-KEY", naverSecretKey);

            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
                if (results != null && !results.isEmpty()) {
                    Map<String, Object> code = (Map<String, Object>) results.get(0).get("code");
                    return Optional.ofNullable(((String) code.get("id"))).map(s -> s.substring(0, 5));
                }
            }
        } catch (Exception e) {
            log.warn("⚠️ 시군구 코드 조회 실패 (lat={}, lng={}): {}", lat, lng, e.getMessage());
        }
        return Optional.empty();
    }

    private PlaceDto parsePlaceDtoFromUrl(String url, String areaCd, String sigunguCd) {
        String json = restTemplate.getForObject(url, String.class);
        try {
            return objectMapper.readValue(json, PlaceDto.class);
        } catch (Exception e) {
            log.error("❌ 관광공사 응답 파싱 실패 - areaCd={}, sigunguCd={}, 에러={}",
                    areaCd, sigunguCd, e.getMessage());
            throw new RuntimeException("관광공사 응답 파싱 실패", e);
        }
    }
}
