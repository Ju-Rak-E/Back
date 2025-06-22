package com.example.spring.rmago.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadiusAreaRequestDto {
    private String baseYm;
    private double latitude;
    private double longitude;
    private double radius;
}
