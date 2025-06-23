package com.example.spring.rmago.service;

import com.example.spring.rmago.util.TaxiFarePolicy;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalTime;
import java.util.*;

@Slf4j
@Service
public class TaxiService {

    private final RestTemplate restTemplate;
    private final Dotenv dotenv;

    public TaxiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.dotenv = Dotenv.configure().ignoreIfMissing().load(); // 환경 변수 로딩
    }

    /**
     * 사용자가 입력한 요금에 따라 이동 가능한 거리 계산
     */
    public double calculateReachableDistance(int fareWon) {
        LocalTime now = LocalTime.now();

        int baseFare;
        int unitFare;
        String policyLabel;

        if (isLateNight(now)) {
            baseFare = TaxiFarePolicy.LATE_NIGHT_BASE_FARE;
            unitFare = TaxiFarePolicy.LATE_NIGHT_UNIT_FARE;
            policyLabel = "심야";
        } else if (isNight(now)) {
            baseFare = TaxiFarePolicy.NIGHT_BASE_FARE;
            unitFare = TaxiFarePolicy.NIGHT_UNIT_FARE;
            policyLabel = "야간";
        } else {
            baseFare = TaxiFarePolicy.DAY_BASE_FARE;
            unitFare = TaxiFarePolicy.DAY_UNIT_FARE;
            policyLabel = "주간";
        }

        log.info("🕒 현재 시각 기준 요금 정책: {}", policyLabel);
        log.info("🚕 받은 요금: {}원 | 기준요금: {}원 | 단위요금: {}원", fareWon, baseFare, unitFare);

        double baseDistance = TaxiFarePolicy.BASE_DISTANCE;
        double unitDistance = TaxiFarePolicy.UNIT_DISTANCE;

        if (fareWon <= baseFare) {
            log.info("💰 기준요금 이하 → baseDistance 반환: {}m", baseDistance);
            return baseDistance;
        }

        int remainingFare = fareWon - baseFare;
        int extraUnits = remainingFare / unitFare;
        double extraDistance = extraUnits * unitDistance;

        double totalDistance = baseDistance + extraDistance;

        log.info("➕ 추가 거리: {}m | 📏 총 이동 가능 거리: {}m", extraDistance, totalDistance);

        return Math.round(totalDistance * 10.0) / 10.0;
    }

    /**
     * 주어진 중심 좌표와 반경 내의 시군구 코드를 가져옴
     */
    public List<String> findSigunguCodesInRadius(double centerLat, double centerLng, double radiusInMeters) {
        final int sampleCount = 60;
        final double earthRadius = 6371000.0;
        Set<String> sigunguCodes = new HashSet<>();

        for (int i = 0; i < sampleCount; i++) {
            double angle = 2 * Math.PI * i / sampleCount;
            double dx = radiusInMeters * Math.cos(angle);
            double dy = radiusInMeters * Math.sin(angle);

            double sampledLat = centerLat + (dy / earthRadius) * (180 / Math.PI);
            double sampledLng = centerLng + (dx / (earthRadius * Math.cos(centerLat * Math.PI / 180))) * (180 / Math.PI);

            try {
                String url = UriComponentsBuilder.fromHttpUrl("https://maps.apigw.ntruss.com/map-geocode/v2/geocode'")
                        .queryParam("coords", sampledLng + "," + sampledLat)
                        .queryParam("output", "json")
                        .queryParam("orders", "admcode")
                        .build().toUriString();

                HttpHeaders headers = new HttpHeaders();
                headers.set("X-NCP-APIGW-API-KEY-ID", dotenv.get("NAVER_MAP_CLIENT_ID"));
                headers.set("X-NCP-APIGW-API-KEY", dotenv.get("NAVER_MAP_SECRET_KEY"));

                HttpEntity<Void> entity = new HttpEntity<>(headers);
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
                    if (results != null && !results.isEmpty()) {
                        String codeId = (String) ((Map<String, Object>) results.get(0).get("code")).get("id");
                        String sigunguCd = codeId.substring(0, 5);
                        sigunguCodes.add(sigunguCd);
                    }
                }
            } catch (Exception e) {
                log.warn("❗ Naver Reverse Geocode 실패: {}", e.getMessage());
            }
        }

        log.info("📦 포함된 시군구 코드 수: {}", sigunguCodes.size());
        log.info("🧭 시군구 코드 목록: {}", sigunguCodes);

        return new ArrayList<>(sigunguCodes);
    }

    private boolean isNight(LocalTime time) {
        return time.isAfter(LocalTime.of(22, 0)) || time.isBefore(LocalTime.of(4, 0));
    }

    private boolean isLateNight(LocalTime time) {
        return time.isAfter(LocalTime.of(23, 0)) || time.isBefore(LocalTime.of(2, 0));
    }
}
