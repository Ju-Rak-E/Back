package com.example.spring.rmago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadiusResponseDto {
    private double latitude;
    private double longitude;
    private double radius;
    private String category;
    private List<String> sigunguCodes;
}