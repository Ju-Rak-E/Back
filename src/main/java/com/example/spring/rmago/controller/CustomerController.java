package com.example.spring.rmago.controller;

import com.example.spring.rmago.dto.KakaoLoginRequestDto;
import com.example.spring.rmago.dto.TokenReissueRequestDto;
import com.example.spring.rmago.dto.TokenResponseDto;
import com.example.spring.rmago.entity.Customer;
import com.example.spring.rmago.service.CustomerService;
import com.example.spring.rmago.swagger.CustomerControllerDocs;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//최초 작업자: 남인경
//수정자 : 김병훈
//사용자가 카카오 소셜 로그인할때의 컨트롤러


@RestController
@RequestMapping("/customer")
@RequiredArgsConstructor
public class CustomerController implements CustomerControllerDocs {
    private final CustomerService customerService;

    // ✅ Flutter에서 Kakao accessToken을 받아 로그인 처리 (access + refresh 발급)
    @PostMapping("/login/kakao")
    public ResponseEntity<TokenResponseDto> kakaologin(@RequestBody KakaoLoginRequestDto requestDto) {
        TokenResponseDto responseDto = customerService.kakaoLogin(requestDto.getAccessToken());
        return ResponseEntity.ok(responseDto);
    }

    // ✅ RefreshToken을 받아 access/refresh 재발급 요청 처리
    @PostMapping("/reissue")
    public ResponseEntity<TokenResponseDto> reissue(@RequestBody TokenReissueRequestDto requestDto) {
        TokenResponseDto newToken = customerService.reissue(requestDto.getRefreshToken());
        return ResponseEntity.ok(newToken);
    }

}

