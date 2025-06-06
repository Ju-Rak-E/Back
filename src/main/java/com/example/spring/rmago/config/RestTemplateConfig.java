package com.example.spring.rmago.config;


//최초 작성자: 김병훈
//최초 작성일: 2025-06-05
//빈으로 등록하여 CustomerService에서 사용할 수 있도록 하기 위한 클래스

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
