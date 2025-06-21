package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.RadiusRequestDto;
import com.example.spring.rmago.dto.RadiusResponseDto;
import com.example.spring.rmago.service.TaxiService;
import com.example.spring.rmago.swagger.TaxiControllerDocs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
@RestController
@RequestMapping("/api/taxi")
public class TaxiController implements TaxiControllerDocs {

    private final TaxiService taxiService;

    public TaxiController(TaxiService taxiService) {
        this.taxiService = taxiService;
    }

    @PostMapping("/estimate-radius")
    public ResponseEntity<RadiusResponseDto> estimateRadius(@RequestBody RadiusRequestDto request) {
        log.info("📥 [요청 도착] /api/taxi/estimate-radius - 입력 요금: {}", request.getFare());
        double radius = taxiService.calculateReachableDistance(request.getFare());
        log.info("📤 [응답 반환] 계산된 반경: {}m", radius);
        return ResponseEntity.ok(new RadiusResponseDto(radius));
    }

}
