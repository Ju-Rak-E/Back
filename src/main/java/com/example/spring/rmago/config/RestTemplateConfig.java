package com.example.spring.rmago.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType; // MediaType 임포트
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter; // StringHttpMessageConverter 임포트
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets; // StandardCharsets 임포트
import java.util.ArrayList;
import java.util.Collections; // Collections 임포트
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        // 1. StringHttpMessageConverter 추가 및 text/xml 미디어 타입 지원
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(StandardCharsets.UTF_8);
        List<MediaType> supportedTextXmlMediaTypes = new ArrayList<>(stringHttpMessageConverter.getSupportedMediaTypes());
        supportedTextXmlMediaTypes.add(MediaType.TEXT_XML); // text/xml 추가
        stringHttpMessageConverter.setSupportedMediaTypes(supportedTextXmlMediaTypes);
        messageConverters.add(stringHttpMessageConverter); // 제일 먼저 추가하여 우선순위 높게

        // 2. MappingJackson2HttpMessageConverter (JSON) 추가
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonHttpMessageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON)); // JSON만 지원하도록 명시
        messageConverters.add(jsonHttpMessageConverter);

        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }
}