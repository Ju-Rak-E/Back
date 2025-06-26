package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.RadiusRequestDto;
import com.example.spring.rmago.dto.RadiusResponseDto;
import com.example.spring.rmago.service.TaxiService;
import com.example.spring.rmago.swagger.TaxiControllerDocs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/taxi")
public class TaxiController implements TaxiControllerDocs {

    private final TaxiService taxiService;

    @PostMapping("/estimate-radius")
    public ResponseEntity<RadiusResponseDto> estimateRadius(@RequestBody RadiusRequestDto request) {
        log.info("📥 [API 요청] /api/taxi/estimate-radius | 입력 요금: {}원", request.getFare());

        double radius = taxiService.calculateReachableDistance((int) request.getFare());
        log.info("📤 이동 가능 반경: {}m", radius);

        return ResponseEntity.ok(
                new RadiusResponseDto(
                        request.getLatitude(),
                        request.getLongitude(),
                        radius,
                        request.getCategory(),
                        null  // 시군구 코드 리스트 제거됨
                )
        );
    }
}
