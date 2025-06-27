package com.example.spring.rmago.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api/laas")
public class LaasController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/recommend")
    public ResponseEntity<String> getRecommendation(@RequestHeader(name = "Authorization", required = false) String authorizationHeader,
            @RequestBody Map<String, Object> userInput) {
        System.out.println("🔥 요청 도착: " + userInput);
        HttpHeaders headers = new HttpHeaders();
        headers.set("project", "KNTO-PROMPTON-169");
        headers.set("apiKey", "f6c5aad9a0ad8b0a7e673f9cccd1442d3cae1f08a43df9b72afcebb811f126eb");
        headers.set("Authorization", authorizationHeader);
        headers.setContentType(MediaType.APPLICATION_JSON);

        System.out.println("🔵 받은 사용자 입력:");
        System.out.println("  ▶ title: " + userInput.get("title"));
        System.out.println("  ▶ lat: " + userInput.get("lat"));
        System.out.println("  ▶ lng: " + userInput.get("lng"));

        String title = userInput.get("title").toString();
        double lat = (double) userInput.get("lat");
        double lng = (double) userInput.get("lng");

        Map<String, Object> requestBody = Map.of(
                "hash", "c6b6b9c296870704ea51684fc44b38ca6deb26e39b8e120a59ee9a2a462c7d47",
                "params", Map.of(
                        "title", title,
                        "lat", lat,
                        "lng", lng
                )
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://api-laas.wanted.co.kr/api/preset/v2/chat/completions",
                HttpMethod.POST,
                entity,
                String.class
        );
        System.out.println("🔁 LAAS 응답: " + response.getBody());
        return ResponseEntity.ok(response.getBody());
    }
}