package com.example.spring.rmago.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RadiusRequestDto {
    private double latitude;
    private double longitude;
    private int fare;
}