package com.example.spring.rmago.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PlaceDto {
    @JsonProperty("response")
    private ResponseDto response;

    @Data
    public static class ResponseDto {
        @JsonProperty("header")
        private HeaderDto header;
        @JsonProperty("body")
        private BodyDto body;
    }

    @Data
    public static class HeaderDto {
        @JsonProperty("resultCode")
        private String resultCode;
        @JsonProperty("resultMsg")
        private String resultMsg;
    }

    @Data
    public static class BodyDto {
        @JsonProperty("pageNo")
        private Integer pageNo; // int 대신 Integer (null 가능성 대비)
        @JsonProperty("totalCount")
        private Integer totalCount; // int 대신 Integer
        @JsonProperty("numOfRows")
        private Integer numOfRows; // int 대신 Integer

        @JsonProperty("items")
        private ItemsDto items;
    }

    @Data
    public static class ItemsDto {
        @JsonProperty("item")
        private List<ItemDto> item;
    }

    @Data
    public static class ItemDto {
        @JsonProperty("addr1") private String addr1;
        @JsonProperty("addr2") private String addr2;
        @JsonProperty("zipcode") private String zipcode;
        @JsonProperty("areacode") private String areacode;
        @JsonProperty("cat1") private String cat1;
        @JsonProperty("cat2") private String cat2;
        @JsonProperty("cat3") private String cat3;
        @JsonProperty("contentid") private String contentid;
        @JsonProperty("contenttypeid") private String contenttypeid;
        @JsonProperty("createdtime") private String createdtime;
        @JsonProperty("dist") private Double dist;
        @JsonProperty("firstimage") private String firstimage;
        @JsonProperty("firstimage2") private String firstimage2;
        @JsonProperty("cpyrhtDivCd") private String cpyrhtDivCd;
        @JsonProperty("mapx") private Double mapx;
        @JsonProperty("mapy") private Double mapy;
        @JsonProperty("mlevel") private String mlevel;
        @JsonProperty("modifiedtime") private String modifiedtime;
        @JsonProperty("sigungucode") private String sigungucode;
        @JsonProperty("tel") private String tel;
        @JsonProperty("title") private String title;
        @JsonProperty("lDongRegnCd") private String lDongRegnCd;
        @JsonProperty("lDongSignguCd") private String lDongSignguCd;
        @JsonProperty("lclsSystm1") private String lclsSystm1;
        @JsonProperty("lclsSystm2") private String lclsSystm2;
        @JsonProperty("lclsSystm3") private String lclsSystm3;
    }
}