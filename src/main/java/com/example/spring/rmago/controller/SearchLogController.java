package com.example.spring.rmago.controller;

import com.example.spring.rmago.service.SearchLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/searchlog")
@RequiredArgsConstructor
public class SearchLogController {
    private final SearchLogService searchLogService;
}
