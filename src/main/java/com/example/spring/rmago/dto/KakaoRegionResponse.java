package com.example.spring.rmago.dto;

import lombok.Data;

import java.util.List;

@Data
public class KakaoRegionResponse {
    private List<Document> documents;

    @Data
    public static class Document {
        private String region_type;
        private String address_name;
        private String region_1depth_name; // 예: 서울특별시
        private String region_2depth_name; // 예: 종로구
    }
}
