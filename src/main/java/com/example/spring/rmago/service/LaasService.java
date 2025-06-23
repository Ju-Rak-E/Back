package com.example.spring.rmago.service;

import com.example.spring.rmago.dto.Recommend.RecommendRequestDto;
import com.example.spring.rmago.dto.Recommend.RecommendedPlaceDto;
import com.example.spring.rmago.entity.Place;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class LaasService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final String apiKey = "YOUR_LAAS_API_KEY";         // 발급받은 키
    private final String projectId = "YOUR_PROJECT_ID";         // 라스 프로젝트 ID
    private final String hash = "YOUR_PRESET_HASH";             // 프리셋 해시

    public List<RecommendedPlaceDto> callPlaceRecommendationLaaS(RecommendRequestDto req, List<Place> places) {
        try {
            // 📦 1. 요청 본문 구성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("hash", hash);

            Map<String, Object> params = new HashMap<>();
            params.put("latitude", req.getLatitude());
            params.put("longitude", req.getLongitude());
            params.put("radiusKm", req.getRadiusKm());
            params.put("category", req.getCategory());

            // place 리스트를 LaaS에 넘기기 위한 요약 정보 추가
            List<Map<String, Object>> placeList = new ArrayList<>();
            for (Place place : places) {
                Map<String, Object> p = new HashMap<>();
                p.put("title", place.getTitle());
                p.put("mapx", place.getMapX());
                p.put("mapy", place.getMapY());
                p.put("address", place.getAddress());
                p.put("category", place.getContentTypeId());
                placeList.add(p);
            }
            params.put("places", placeList);
            requestBody.put("params", params);

            // 📬 2. HTTP 요청 전송
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("apiKey", apiKey);
            headers.set("project", projectId);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "https://api-laas.wanted.co.kr/api/preset/v2/chat/completions",
                    entity,
                    String.class
            );

            // ✅ 3. 응답 JSON 파싱 → 추천 리스트로 반환
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode data = root.path("result").path("response");

            // JSON 파싱 결과를 직접 리스트로 반환 (응답 포맷에 따라 조정)
            List<RecommendedPlaceDto> result = new ArrayList<>();
            JsonNode array = objectMapper.readTree(data.asText()); // string → 실제 JSON 배열
            for (JsonNode node : array) {
                result.add(RecommendedPlaceDto.builder()
                        .name(node.path("name").asText())
                        .address(node.path("address").asText())
                        .category(node.path("category").asText())
                        .distanceKm(node.path("distanceKm").asDouble())
                        .description(node.path("description").asText())
                        .build()
                );
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // 에러 시 빈 리스트 반환
        }
    }
}
