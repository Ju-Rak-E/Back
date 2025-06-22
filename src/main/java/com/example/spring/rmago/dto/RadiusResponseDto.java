package com.example.spring.rmago.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadiusResponseDto {
    private double radius;
    private List<String> sigunguCodes;
}