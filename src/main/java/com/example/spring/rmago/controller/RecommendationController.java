package com.example.spring.rmago.controller;

import com.example.spring.rmago.swagger.RecommendationRepositoryDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendationController implements RecommendationRepositoryDocs {



}
