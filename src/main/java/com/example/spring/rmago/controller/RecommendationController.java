package com.example.spring.rmago.controller;

import com.example.spring.rmago.service.RecommendationService;
import com.example.spring.rmago.swagger.RecommendationRepositoryDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController implements RecommendationRepositoryDocs {
    private final RecommendationService recommendationService;
}
