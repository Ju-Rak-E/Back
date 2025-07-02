package com.example.spring.rmago.dto.Recommend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecommendedPlaceDto {
    private String name;         // 장소 이름
    private String address;      // 주소
    private String category;     // 음식점, 관광지 등
    private double distanceKm;   // 사용자와 거리
    private String description;  // 요약 설명
    private double lat;  // ✅ 위도
    private double lng;  // ✅ 경도

}
