package com.example.spring.rmago.dto;

//작성자 : 김병훈
//최초 작성일 : 2025-06-16
//한국 관광공사 open API의 응답 DTO 생성

import lombok.Data;
import java.util.List;

@Data
public class PlaceDto {
    private Response response;

    @Data
    public static class Response {
        private Body body;
    }

    @Data
    public static class Body {
        private Items items;
        private int numOfRows;
        private int pageNo;
        private int totalCount;
    }

    @Data
    public static class Items {
        private List<Item> item;
    }

    @Data
    public static class Item {
        private String title;
        private String addr1;
        private String contentid;
        private String mapx; // 경도
        private String mapy; // 위도
    }
}
