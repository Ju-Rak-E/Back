package com.example.spring.rmago.service;

import com.example.spring.rmago.dto.KakaoRegionResponse;
import com.example.spring.rmago.dto.PlaceDto;
import com.example.spring.rmago.util.CodeMapperService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class PlaceService {

    @Value("${tourapi.service-key}")
    private String serviceKey;

    @Value("${kakao.rest-api-key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CodeMapperService codeMapper;

    public PlaceDto fetchTourByLocation(double lat, double lng) {
        KakaoRegionResponse region = callKakaoRegionApi(lat, lng);
        String sido = region.getDocuments().get(0).getRegion_1depth_name();
        String sigungu = region.getDocuments().get(0).getRegion_2depth_name();

        String areaCd = codeMapper.getAreaCd(sido);
        String signguCd = codeMapper.getSignguCd(sido, sigungu);
        String baseYm = "202504";

        String url = "https://apis.data.go.kr/B551011/TarRlteTarService1/areaBasedList1"
                + "?serviceKey=" + UriUtils.encode(serviceKey, StandardCharsets.UTF_8)
                + "&pageNo=1&numOfRows=10&MobileOS=AND&MobileApp=Rmago"
                + "&baseYm=" + baseYm
                + "&areaCd=" + areaCd
                + "&signguCd=" + signguCd
                + "&_type=json";

        String json = restTemplate.getForObject(url, String.class);
        try {
            return objectMapper.readValue(json, PlaceDto.class);
        } catch (Exception e) {
            throw new RuntimeException("관광공사 응답 파싱 실패", e);
        }
    }

    private KakaoRegionResponse callKakaoRegionApi(double lat, double lng) {
        String url = UriComponentsBuilder.fromHttpUrl("https://dapi.kakao.com/v2/local/geo/coord2regioncode.json")
                .queryParam("x", lng)
                .queryParam("y", lat)
                .build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(url, HttpMethod.GET, entity, KakaoRegionResponse.class).getBody();
    }

    public PlaceDto fetchAreaBasedTourList(String baseYm, String areaCd, String signguCd) {
        String url = "https://apis.data.go.kr/B551011/TarRlteTarService1/areaBasedList1"
                + "?serviceKey=" + UriUtils.encode(serviceKey, StandardCharsets.UTF_8)
                + "&pageNo=1&numOfRows=10&MobileOS=AND&MobileApp=Rmago"
                + "&baseYm=" + baseYm
                + "&areaCd=" + areaCd
                + "&signguCd=" + signguCd
                + "&_type=json";

        String json = restTemplate.getForObject(url, String.class);
        try {
            return objectMapper.readValue(json, PlaceDto.class);
        } catch (Exception e) {
            throw new RuntimeException("관광공사 응답 파싱 실패", e);
        }
    }

    public PlaceDto fetchTourByKeyword(String baseYm, String keyword) {
        String url = "https://apis.data.go.kr/B551011/TarRlteTarService1/searchKeyword1"
                + "?serviceKey=" + UriUtils.encode(serviceKey, StandardCharsets.UTF_8)
                + "&pageNo=1&numOfRows=10&MobileOS=ETC&MobileApp=Rmago"
                + "&baseYm=" + baseYm
                + "&keyword=" + UriUtils.encode(keyword, StandardCharsets.UTF_8)
                + "&_type=json";

        String json = restTemplate.getForObject(url, String.class);

        try {
            return objectMapper.readValue(json, PlaceDto.class);
        } catch (Exception e) {
            throw new RuntimeException("관광공사 키워드 검색 응답 파싱 실패", e);
        }
    }

}
