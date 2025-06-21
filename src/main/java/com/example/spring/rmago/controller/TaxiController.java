package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.RadiusRequestDto;
import com.example.spring.rmago.dto.RadiusResponseDto;
import com.example.spring.rmago.service.TaxiService;
import com.example.spring.rmago.swagger.TaxiControllerDocs;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/taxi")
public class TaxiController implements TaxiControllerDocs {

    private final TaxiService taxiService;

    public TaxiController(TaxiService taxiService) {
        this.taxiService = taxiService;
    }

    @PostMapping("/estimate-radius")
    public ResponseEntity<RadiusResponseDto> estimateRadius(@RequestBody RadiusRequestDto request) {
        double radius = taxiService.calculateReachableDistance(request.getFare());
        return ResponseEntity.ok(new RadiusResponseDto(radius));
    }
}
