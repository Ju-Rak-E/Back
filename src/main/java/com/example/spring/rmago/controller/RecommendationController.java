package com.example.spring.rmago.controller;

import com.example.spring.rmago.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController {
    private final RecommendationService recommendationService;
}
