package com.example.spring.rmago.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/health")
    public ResponseEntity<String> health() {

        System.out.println("Health Check***********!!");
        return ResponseEntity.ok("백엔드에서 보낸다 연결되었다고");
    }
}
