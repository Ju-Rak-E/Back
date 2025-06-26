package com.example.spring.rmago.dto;

import com.example.spring.rmago.type.RadiusMode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class RadiusRequestDto {
    private double fare;
    private double latitude;
    private double longitude;
    private RadiusMode mode; // SINGLE 또는 MULTI
    private String category;

}