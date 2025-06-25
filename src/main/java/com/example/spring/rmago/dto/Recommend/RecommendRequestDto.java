package com.example.spring.rmago.dto.Recommend;

import lombok.Data;

@Data
public class RecommendRequestDto {
    private double latitude;       // 사용자 현재 위도
    private double longitude;      // 사용자 현재 경도
    private double radius;       // 이동 가능 거리 (km 단위)
    private String category;       // 관심 장소 유형 (예: 음식점, 관광지 등)
    private String baseYm;
}