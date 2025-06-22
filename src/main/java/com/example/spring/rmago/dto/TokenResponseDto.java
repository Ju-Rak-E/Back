package com.example.spring.rmago.dto;

//최초 작업자 : 김병훈
//작성일 : 2025-05-30

//jwt 응답 DTO

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class TokenResponseDto {
    private String message;  // 메시지 필드
    private String accessToken;
    private String refreshToken;
}