package com.example.spring.rmago.service;

import com.example.spring.rmago.repository.SearchLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchLogService {
    private final SearchLogRepository searchLogRepository;
}
