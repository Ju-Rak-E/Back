package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.KakaoLoginRequestDto;
import com.example.spring.rmago.dto.TokenReissueRequestDto;
import com.example.spring.rmago.dto.TokenResponseDto;
import com.example.spring.rmago.service.CustomerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping( "/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    // ✅ Flutter에서 Kakao accessToken을 받아 로그인 처리
    @PostMapping("/login/kakao/android")
    public ResponseEntity<TokenResponseDto> kakaologinForAndroid(@RequestBody KakaoLoginRequestDto requestDto) {

        System.out.println("카카오 로그인 요청받음.컨트롤러에서");
        System.out.println("받은 accessToken: " + requestDto.getAccessToken());

        if (requestDto.getAccessToken() == null || requestDto.getAccessToken().isEmpty()) {
            System.out.println("토큰을 받지 못했거나 빈문자열입니다.");
            return ResponseEntity.badRequest().build();
        }

        // 안드로이드 로그인에 맞는 메서드 호출
        TokenResponseDto responseDto = customerService.kakaoLoginForAndroid(requestDto.getAccessToken());
        return ResponseEntity.ok(responseDto);
    }

    // ✅ 웹에서 authorizationCode를 받아 로그인 처리 (웹 로그인)
    @PostMapping("/login/kakao/web")
    public ResponseEntity<TokenResponseDto> kakaologinForWeb(@RequestParam String authorizationCode, @RequestParam String redirectUri) {
        System.out.println("받은 authorizationCode: " + authorizationCode);

        if (authorizationCode == null || authorizationCode.isEmpty()) {
            System.out.println("authorizationCode를 받지 못했습니다.");
            return ResponseEntity.badRequest().build();
        }

        // 웹 로그인에 맞는 메서드 호출
        TokenResponseDto responseDto = customerService.kakaoLoginForWeb(authorizationCode, redirectUri);
        return ResponseEntity.ok(responseDto);
    }

    // ✅ RefreshToken을 받아 access/refresh 재발급 요청 처리
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody Map<String, String> requestBody) {
        String refreshToken = requestBody.get("refreshToken");

        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(new TokenResponseDto("리프레시 토큰이 없습니다", null, null));
        }

        try {
            log.info("(컨트롤러) RefreshToken 재발급 요청 수신: {}", refreshToken);
            TokenResponseDto newToken = customerService.reissue(refreshToken);
            return ResponseEntity.ok(newToken);
        } catch (RuntimeException e) {
            return ResponseEntity.status(403).body(new TokenResponseDto(e.getMessage(), null, null));
        }
    }

}
