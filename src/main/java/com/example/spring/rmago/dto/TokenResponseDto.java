package com.example.spring.rmago.dto;

//최초 작업자 : 김병훈
//작성일 : 2025-05-30

//jwt 응답 DTO

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenResponseDto {
    private String message;  // 메시지 필드
    private String accessToken;
    private String refreshToken;

    // 기본 생성자
    public TokenResponseDto() {}

    // 성공 시 사용되는 생성자
    public TokenResponseDto(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // 오류 메시지 포함 생성자
    public TokenResponseDto(String message, String accessToken, String refreshToken) {
        this.message = message;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}