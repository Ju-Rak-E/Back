package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.RadiusRequestDto;
import com.example.spring.rmago.dto.RadiusResponseDto;
import com.example.spring.rmago.type.RadiusMode;
import com.example.spring.rmago.service.PlaceService;
import com.example.spring.rmago.service.TaxiService;
import com.example.spring.rmago.swagger.TaxiControllerDocs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/taxi")
public class TaxiController implements TaxiControllerDocs {

    private final TaxiService taxiService;
    private final PlaceService placeService;

    @PostMapping("/estimate-radius")
    public ResponseEntity<RadiusResponseDto> estimateRadius(@RequestBody RadiusRequestDto request) {
        log.info("📥 [API 요청] /api/taxi/estimate-radius | 입력 요금: {}원", request.getFare());

        double radius = taxiService.calculateReachableDistance((int) request.getFare());
        log.info("📤 이동 가능 반경: {}m", radius);

        List<String> sigunguCodes;

        if (request.getMode() == RadiusMode.SINGLE) {
            log.info("🔎 단일 좌표 기반 시군구 코드 조회 실행");
            sigunguCodes = List.of(
                    placeService.getSigunguCodeFromNaver(request.getLatitude(), request.getLongitude())
            );
        } else {
            log.info("🌐 반경 내 시군구 코드 전체 조회 실행");
            sigunguCodes = placeService.getSigunguCodesWithinRadius(
                    request.getLatitude(),
                    request.getLongitude(),
                    radius
            );
        }

        log.info("📦 포함된 시군구 코드 수: {}", sigunguCodes.size());

        return ResponseEntity.ok(
                new RadiusResponseDto(
                        request.getLatitude(),
                        request.getLongitude(),
                        radius,
                        sigunguCodes
                )
        );
    }

}
